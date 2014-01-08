package ge.dl;

import ge.dl.api.Event;
import ge.dl.api.EventListener;

import java.util.ArrayList;
import java.util.List;

public class LocatorClient {
	
	public void setParam(String locatorIP, int locatorPort, String backupLocatorIP, int backupLocatorPort, String backupIP) {
		clientRunner = new LocatorClientRunner(locatorIP, locatorPort, backupLocatorIP, backupLocatorPort, this, backupIP);
	}
	
	private List<EventListener> listeners = new ArrayList<EventListener>();
	
	private LocatorClientRunner clientRunner;
	
	public void triggerEvent(Event e) {
		
		for(EventListener l: listeners) {
			l.actionPerformed(e);
		}
	}
	
	public void addListener(EventListener el) {
		listeners.add(el);
	}
	
	public void startClient() {
		
		clientRunner.start();
	}
	
	

	/*public static void main(String[] args) {
		
		ClientLauncher launcher = new ClientLauncher();
		
		launcher.addListener(new EventListener() {
			
			@Override
			public void actionPerformed(Event e) {
				
				if(e.getEventType().equals("Failover")) {
					System.out.println("Recevied Failover Event");
				} else if(e.getEventType().equals("Recover")) {
					System.out.println("Recevied Recover Event");
				}
				
			}
		});
		
		LocatorClientRunner client = new LocatorClientRunner("localhost", 8888, "localhost", 9999, "localhost", launcher);
		client.start();
	}*/
}
