package ge.dl;

import ge.dl.api.DataService;

import java.util.HashMap;

public class ServerManager {

	private SyncService ss;

	private LocatorService ls;
	
	public ServerManager(int localPort, String backupLocatorIp, int backupLocatorPort, int localSyncPort, int backupSyncPort) {
		
		DataService dataService = new DataService();
		
		ss = new SyncService(localSyncPort, backupLocatorIp, backupSyncPort, dataService);
		ls = new LocatorService(localPort, backupLocatorIp, backupLocatorPort, dataService, ss);
	}
	
	private ServerManager() {}
	

	public void startServer() {
		
		ls.start();
		ss.start();
	}


	public void syncLocatorMember(HashMap infos) {
		ls.syncLocatorMember(infos);
	}

}
