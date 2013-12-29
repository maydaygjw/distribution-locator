package ge.dl;

public class ServerLauncher {

	public static void main(String[] args) throws Exception {

		LocatorService ls = new LocatorService();
		ls.start();
		
		SyncService ss = new SyncService(ls); 
		ss.start();

	}

}
