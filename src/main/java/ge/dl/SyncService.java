package ge.dl;

import ge.dl.api.MemberInfo;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Set;

public class SyncService extends Thread {
	
	private Set<String> locatorMembers;
	
	public void setLocatorMembers(Set<String> locatorMembers) {
		this.locatorMembers = locatorMembers;
	}
	
	private LocatorService ls;
	
	public SyncService(LocatorService service) {
		this.ls = service;
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

		System.out.println("Sync Server started at: " + 5678);

		while (true) {
			ds.receive(dp);
			ByteArrayInputStream bais = new ByteArrayInputStream(buf);
			ObjectInputStream ois = new ObjectInputStream(bais);

			HashMap infos = (HashMap<String, MemberInfo>) ois.readObject();
			ls.syncLocatorMember(infos);
		}
	}
}
