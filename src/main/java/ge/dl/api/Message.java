package ge.dl.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Message implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int HeartBeat = 0x00001;
	public static final int HeartBeatAck = 0x00002;
	public static final int ConnectionLostDetected = 0x00003;
	public static final int RegisterRequest = 0x00004;
	public static final int RegisterSuccess = 0x00005;
	public static final int RegisterReject = 0x00006;
	public static final int AllMembersStandBy = 0x00007;
	public static final int NoConnection = 0x00008;
	public static final int Disconnect = 0x00009;
	public static final int ConnectionPending = 0x00010;
	public static final int BackupRequest = 0x00011;
	public static final int BackupStarted = 0x00012;
	
	private Map<String, Object> context = new HashMap<String, Object>();
	
	private int type;

	public int getType() {
		return type;
	}
	
	public Message(int type) {
		this.type = type;
	}
	
	public void put(String key, Object value) {
		this.context.put(key, value);
	}
	
	public Message() {
		
	}
	
	
	public Object get(String key) {
		return context.get(key);
	}
	
	public static final class MessageKey {
		public static final String CLIENT_ID = "ClientID";
		public static final String BACKUP_ID = "BackupID";
	}

	@Override
	public String toString() {
		switch (this.type) {
		case 0x00001:
			
			return "HeartBeat ";
			
		default:
			return super.toString();
		}
	}
}
