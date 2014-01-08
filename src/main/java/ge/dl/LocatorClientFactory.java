package ge.dl;

public class LocatorClientFactory {

	public static LocatorClient createLocatorClient(String locatorIP, int locatorPort, String backupLocatorIP, int backupLocatorPort, String backupIP) {
		
		LocatorClient locatorClient = new LocatorClient();
		locatorClient.setParam(locatorIP, locatorPort, backupLocatorIP, backupLocatorPort, backupIP);
		
		return locatorClient;
	}
}
