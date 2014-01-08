package ge.dl;

import ge.dl.api.Event;
import ge.dl.api.EventListener;

public class LocatorClientLauncher {

	public static void main(String[] args) {
		
		LocatorClient client = LocatorClientFactory.createLocatorClient("localhost", 9999, "10.114.193.158", 9999, "10.114.193.158");
		
		client.addListener(new EventListener() {
			
			@Override
			public void actionPerformed(Event e) {
				if(e.getEventType().equals("Failover")) {
					System.out.println("Recevied Failover Event");
				} else if(e.getEventType().equals("Recover")) {
					System.out.println("Recevied Recover Event");
				}
				
			}
		});
		
		client.startClient();
	}
}
