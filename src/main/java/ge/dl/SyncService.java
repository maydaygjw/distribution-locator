package ge.dl;

import ge.dl.api.DataService;
import ge.dl.api.MemberInfo;
import ge.dl.util.UDPClientUtil;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SyncService extends Thread {
	
	private Set<String> locatorMembers;
	
	public void setLocatorMembers(Set<String> locatorMembers) {
		this.locatorMembers = locatorMembers;
	}
	
	private int syncPort;
	private int backupSyncPort;
	
	private DataService dataService;
	
	
	
	public SyncService(int syncPort, String backupSyncIp, int backupSyncPort, DataService dataService) {
		this.syncPort = syncPort;
		this.backupSyncPort = backupSyncPort;
		this.dataService = dataService;
		
		locatorMembers = new HashSet<String>();
		locatorMembers.add(backupSyncIp + backupSyncPort);
	}
	
	public void addLocatorMember(String ip) {
		
	}

	@Override
	public void run() {
		
		try {
			startSyncServer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void startSyncServer() throws Exception {

		byte buf[] = new byte[1024];
		DatagramPacket dp = new DatagramPacket(buf, buf.length);
		DatagramSocket ds = new DatagramSocket(5678);

		System.out.println("Sync Server started at: " + syncPort);

		while (true) {
			ds.receive(dp);
			ByteArrayInputStream bais = new ByteArrayInputStream(buf);
			ObjectInputStream ois = new ObjectInputStream(bais);

			HashMap infos = (HashMap<String, MemberInfo>) ois.readObject();
			dataService.synchronize(infos);
		}
	}

	public void syncAll() {
		for(String backupSync: locatorMembers) {
			String[] ipAndPort = backupSync.split(",");
			String backupSyncIp = ipAndPort[0];
			int backupSyncPort = Integer.valueOf(ipAndPort[1]);
			
			HashMap context = (HashMap) dataService.getAllMembers();
			
			UDPClientUtil.send(backupSyncIp, backupSyncPort, context);
		}
		
	}
}
