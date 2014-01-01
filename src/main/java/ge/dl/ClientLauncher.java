package ge.dl;

import ge.dl.api.Event;
import ge.dl.api.EventListener;

import java.util.ArrayList;
import java.util.List;

public class ClientLauncher {
	
	private List<EventListener> listeners = new ArrayList<EventListener>();
	
	public void triggerEvent(Event e) {
		
		for(EventListener l: listeners) {
			l.actionPerformed(e);
		}
	}
	
	public void addListener(EventListener el) {
		listeners.add(el);
	}

	public static void main(String[] args) {
		
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
		
		LocatorClient client = new LocatorClient("localhost", 12345, "localhost", 12346, "localhost");
		client.start();
	}
}
