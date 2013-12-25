package ge.dl;

import ge.dl.api.RegisterInfo;
import ge.dl.util.CommonUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class LocatorService {
	
	private static Map<String, RegisterInfo> infos = new HashMap<String, RegisterInfo>();
	
	static CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
	
	private static Set<String> exceptionClientIdSet = new HashSet<String>();
	private static Set<String> recoverClientIdSet = new HashSet<String>();
	
	public static void main(String[] args) {

		Selector selector = null;
		ServerSocketChannel server = null;
		try {
			// Selector
			selector = Selector.open();

			//Open Server Socket
			server = ServerSocketChannel.open();
			server.configureBlocking(false);
			server.register(selector, SelectionKey.OP_ACCEPT);

			// IP Address
			InetSocketAddress ip = new InetSocketAddress(12345);
			server.socket().bind(ip);

			while (true) {
				selector.select();
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey key = it.next();
					it.remove();

					if (key.isAcceptable()) {
						ServerSocketChannel server2 = (ServerSocketChannel) key.channel();
						SocketChannel channel = server2.accept();
						channel.configureBlocking(false);
						SelectionKey clientKey = channel.register(selector, SelectionKey.OP_READ);
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						
						clientKey.attach(buffer);

						System.out.println("Client Connected from: " + channel.socket().getInetAddress().getHostAddress()
								+ ":" + channel.socket().getPort());
					} else if (key.isReadable()) {
						SocketChannel channel = (SocketChannel) key.channel();

						CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

						ByteBuffer buffer = ByteBuffer.allocate(50);
						
						try{
							channel.read(buffer);
						}catch(IOException e) {
							channel.close();
							System.out.println("a Client quit");
							continue;
						}
						
						
						
						buffer.flip();
						String msg = decoder.decode(buffer).toString();
						System.out.println("Recevied Message: " + msg);
						
						postInfo(channel, msg);
						
						sendback(channel, msg);
						
						
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//
			try {
				selector.close();
				server.close();
			} catch (IOException e) {
			}
		}
	}

	private static void sendback(SocketChannel channel, String msg) {
		
		String clientIP = CommonUtil.getIP(channel.socket().getInetAddress().getHostAddress());
		
		//System.out.println("send back to client: " + clientIP);
		
		String retString = "HeartBearAck";
		
		for(RegisterInfo info : infos.values()) {
			if(info.isAlive() && info.timeout()) {
				info.setAlive(false);
				exceptionClientIdSet.add(info.getBackupClientId());
				System.out.println("Server added " + info.getBackupClientId() + " into exception set");
			}
			
			else if(!info.isAlive() && !info.timeout()) {
				info.setAlive(true);
				recoverClientIdSet.add(info.getBackupClientId());
				System.out.println("Server added " + info.getBackupClientId() + " into recover set");
			}
		}
		
		if(exceptionClientIdSet.contains(clientIP)) {
			retString += "#Failover";
			exceptionClientIdSet.remove(clientIP);
		}else if(recoverClientIdSet.contains(clientIP)) {
			retString += "#Recover";
			recoverClientIdSet.remove(clientIP);
		}
		
		
		
		
		try {
			channel.write(encoder.encode(CharBuffer.wrap(retString)));
		} catch (CharacterCodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void postInfo(SocketChannel channel, String msg) {
		
		//System.out.println("postInfo msg: " + msg);
		
		String[] msgArray = msg.split("#");
		
		String clientIP = CommonUtil.getIP(channel.socket().getInetAddress().getHostAddress());
		
		if(infos.get(clientIP) == null) {
			RegisterInfo info = new RegisterInfo();
			info.setClientId(clientIP);
			info.setActiveTS(System.currentTimeMillis());
			info.setAlive(true);
			info.setBackupClientId(msgArray[1]);
			info.setFailoverStarted(false);
			
			System.out.println("Added a new entry into Registry Map: ");
			System.out.println("clientIP: " + clientIP);
			System.out.println("setBackupClientId: " + msgArray[1]);
			
			
			infos.put(clientIP, info);
		} else {
			
			infos.get(clientIP).setActiveTS(System.currentTimeMillis());
		}
		
		if(msgArray.length == 3) {
			String extraAction = msgArray[2];	//Backup started
			for(RegisterInfo info : infos.values()) {
				if(clientIP.equals(info.getBackupClientId())) {
					info.setFailoverStarted(true);
				}
			}
		}
		
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
