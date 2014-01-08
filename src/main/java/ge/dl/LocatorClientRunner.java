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

public class LocatorClientRunner extends Thread {

	private CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
	private CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
	private Selector selector = null;
	private SocketChannel socket = null;
	private SelectionKey clientKey = null;


	private String backupLocator;
	private int backupLocatorPort;
	private int locatorPort;
	private String locatorIP;
	
	private boolean backupServiceActive = false;
	private boolean clientActive = true;

	private LocatorClient launcher;

	public LocatorClientRunner(String locatorIP, int locatorPort, String backupLocatorIP, int backupLocatorPort, LocatorClient launcher) {

		this.locatorIP = locatorIP;
		this.locatorPort = locatorPort;
		this.backupLocator = backupLocatorIP;
		this.backupLocatorPort = backupLocatorPort;
		this.launcher = launcher;
		
		try {
			connect(this.locatorIP, this.locatorPort);
		} catch (IOException e) {
			e.printStackTrace();
			
			System.out.println("unable to connect to locator");
		}
		
	}
	
	private void connect(String ip, int port) throws IOException {
	
			selector = Selector.open();

			socket = SocketChannel.open();
			socket.configureBlocking(false);
			clientKey = socket.register(selector, SelectionKey.OP_CONNECT);

			InetSocketAddress ipAddr = new InetSocketAddress(ip, port);
			System.out.println("Register connection to " + ip + ", " + port);
			socket.connect(ipAddr);
			
			
		
	}

	@Override
	public void run() {

		while (clientActive) {

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

						send("HeartBeat#" + backupLocator);
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
				
				if(backupServiceActive) {
					System.out.println("Unable to connect to the backup locator, system will exit");
					clientActive = false;
					continue;
				}

				//e.printStackTrace();
				System.out.println("unable to connect to the primary locator, try to connect to the backup locator");
				try {
					connect(backupLocator, backupLocatorPort);
					this.backupServiceActive = true;

				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
			}
		}

	}

	private void sendBack(String msg) {

		String retMsg = "HeartBeat#" + backupLocator;

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
