package ge.dl;

public class ServerLauncher {

	public static void main(String[] args) throws Exception {

		LocatorService ls = new LocatorService(12346, "localhost", 12345);
		ls.start();
		
		SyncService ss = new SyncService(ls); 
		ss.start();

	}

}
