package ge.dl;

public class BackupServerLauncher {

	public static void main(String[] args) {
		
		ServerManager manager = new ServerManager(9999, "localhost", 8888, 6678, 5678);
		
		manager.startServer();
	}
}
