package ge.dl;

public class ServerLauncher {

	public static void main(String[] args) {
		
		ServerManager manager = new ServerManager(8888, "localhost", 9999, 5678, 6678);
		
		manager.startServer();
	}
}
