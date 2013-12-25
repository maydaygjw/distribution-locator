package ge.dl.api;

public class EventImpl implements Event{
	
	private String eventType;
	
	

	public EventImpl(String eventType) {
		super();
		this.eventType = eventType;
	}



	@Override
	public String getEventType() {
		
		return eventType;
	}

}
