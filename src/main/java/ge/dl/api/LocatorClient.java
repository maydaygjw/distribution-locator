package ge.dl.api;

public interface LocatorClient {

	void addEventListener(EventListener listener);
	
	void triggerEvent(Event e);
	
	
}
