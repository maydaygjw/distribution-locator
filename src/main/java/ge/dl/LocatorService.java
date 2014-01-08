package ge.dl;

import ge.dl.api.MemberInfo;
import ge.dl.api.DataService;
import ge.dl.util.CommonUtil;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
import java.util.prefs.BackingStoreException;

public class LocatorService extends Thread{

	//private static Map<String, MemberInfo> infos = new HashMap<String, MemberInfo>();
	
	
	private DataService registry = new DataService();

	static CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
	
	private int localport;
	private String backupIP;
	private int backupPort;
	
	private SyncService ss;
	

	public Set<String> locatorMemberSet = new HashSet<String>();

	private Set<String> exceptionClientIdSet = new HashSet<String>();
	private Set<String> recoverClientIdSet = new HashSet<String>();
	
	public LocatorService(int localport, String backupIP, int backupport, DataService dataService, SyncService ss) {
		this.localport = localport;
		this.backupIP = backupIP;
		this.backupPort = backupport;
		this.registry = dataService;
		this.ss = ss;
	}

	
	@Override
	public void run() {
		
		startServer();
	}
	
	

	public void startServer() {
		Selector selector = null;
		ServerSocketChannel server = null;
		try {
			// Selector
			selector = Selector.open();

			// Open Server Socket
			server = ServerSocketChannel.open();
			server.configureBlocking(false);
			server.register(selector, SelectionKey.OP_ACCEPT);

			// IP Address
			InetSocketAddress ip = new InetSocketAddress(localport);
			server.socket().bind(ip);
			
			System.out.println("Locator Service started at: " + localport);

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

						System.out
								.println("Client Connected from: " + channel.socket().getInetAddress().getHostAddress()
										+ ":" + channel.socket().getPort());
					} else if (key.isReadable()) {
						SocketChannel channel = (SocketChannel) key.channel();

						CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

						ByteBuffer buffer = ByteBuffer.allocate(50);

						try {
							channel.read(buffer);
						} catch (IOException e) {
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
	
	public void syncLocatorMember(HashMap<String, MemberInfo> members) {
		registry.synchronize(members);
	}

	private void sendback(SocketChannel channel, String msg) {

		String clientIP = CommonUtil.getIP(channel.socket().getInetAddress().getHostAddress());

		// System.out.println("send back to client: " + clientIP);

		String retString = "HeartBearAck";

		for (MemberInfo info : this.registry.getAllMembers().values()) {
			if (info.isAlive() && info.timeout()) {
				info.setAlive(false);
				exceptionClientIdSet.add(info.getBackupClientId());
				System.out.println("Server added " + info.getBackupClientId() + " into exception set");
			}

			else if (!info.isAlive() && !info.timeout()) {
				info.setAlive(true);
				recoverClientIdSet.add(info.getBackupClientId());
				System.out.println("Server added " + info.getBackupClientId() + " into recover set");
			}
		}

		if (exceptionClientIdSet.contains(clientIP)) {
			retString += "#Failover";
			exceptionClientIdSet.remove(clientIP);
		} else if (recoverClientIdSet.contains(clientIP)) {
			retString += "#Recover";
			recoverClientIdSet.remove(clientIP);
		}
		
		ss.syncAll();

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

	private void postInfo(SocketChannel channel, String msg) {

		// System.out.println("postInfo msg: " + msg);

		String[] msgArray = msg.split("#");

		String clientIP = CommonUtil.getIP(channel.socket().getInetAddress().getHostAddress());

		if (registry.getMemberInfo(clientIP) == null) {
			MemberInfo info = new MemberInfo();
			info.setClientId(clientIP);
			info.setActiveTS(System.currentTimeMillis());
			info.setAlive(true);
			info.setBackupClientId(msgArray[1]);
			info.setFailoverStarted(false);

			System.out.println("Added a new entry into Registry Map: ");
			System.out.println("clientIP: " + clientIP);
			System.out.println("setBackupClientId: " + msgArray[1]);

			registry.addMember(info);
		} else {

			registry.updateActiveTS(clientIP);
		}

		if (msgArray.length == 3) {
			String extraAction = msgArray[2]; // Backup started
			registry.setBackupStarted(clientIP);
		}

		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
