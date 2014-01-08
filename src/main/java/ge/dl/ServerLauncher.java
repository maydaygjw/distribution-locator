package ge.dl;

public class ServerLauncher {

	public static void main(String[] args) {
		
		ServerManager manager = new ServerManager(9999, "10.114.193.158", 9999, 6678, 5678);
		
		manager.startServer();
	}
}
