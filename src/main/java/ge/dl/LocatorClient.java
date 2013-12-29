package ge.dl;

import ge.dl.api.Event;
import ge.dl.api.EventImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;

public class LocatorClient extends Thread {

	private CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
	private CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
	private Selector selector = null;
	private SocketChannel socket = null;
	private SelectionKey clientKey = null;

	private String backupIP = "10.114.193.158";

	private Launcher launcher;

	public LocatorClient() {

		// this.launcher = launcher;

		try {
			selector = Selector.open();

			socket = SocketChannel.open();
			socket.configureBlocking(false);
			clientKey = socket.register(selector, SelectionKey.OP_CONNECT);

			InetSocketAddress ip = new InetSocketAddress("localhost", 12345);
			socket.connect(ip);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		while (true) {

			try {

				selector.select();
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey key = it.next();
					it.remove();

					if (key.isConnectable()) {
						SocketChannel channel = (SocketChannel) key.channel();
						if (channel.isConnectionPending())
							channel.finishConnect();
						channel.register(selector, SelectionKey.OP_READ);
						System.out.println("Connected!");

						send("HearBeat#" + backupIP);
					} else if (key.isReadable()) {
						SocketChannel channel = (SocketChannel) key.channel();

						ByteBuffer buffer = ByteBuffer.allocate(50);
						channel.read(buffer);
						buffer.flip();
						String msg = decoder.decode(buffer).toString();
						System.out.println("Recevied:" + msg);
						sendBack(msg);

						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					//selector.close();
					//socket.close();
					
					socket.connect(new InetSocketAddress("localhost", 9999));
				} catch (IOException e) {
				}
			}
		}

	}

	private void sendBack(String msg) {

		String retMsg = "HeartBeat#" + backupIP;

		String[] msgArr = msg.split("#");
		if (msgArr.length == 2) {
			if ("Failover".equals(msgArr[1])) {
				Event e = new EventImpl("Failover");
				launcher.triggerEvent(e);
				retMsg += "#BackupStarted";
			} else if ("Recover".equals(msgArr[1])) {
				Event e = new EventImpl("Recover");
				launcher.triggerEvent(e);
			}
		}

		send(retMsg);

	}

	public void send(String msg) {
		try {
			SocketChannel client = (SocketChannel) clientKey.channel();
			client.write(encoder.encode(CharBuffer.wrap(msg)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			selector.close();
			socket.close();
		} catch (IOException e) {
		}
	}
}
