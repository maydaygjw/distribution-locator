package ge.dl;

public class LocatorClientLauncher {

	public static void main(String[] args) {
		
		LocatorClient client = LocatorClientFactory.createLocatorClient("localhost", 8888, "localhost", 9999);
		
		client.startClient();
	}
}
