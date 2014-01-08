package ge.dl.api;

import java.io.Serializable;


public class MemberInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static final long TIMEOUT = 10000;

	
	//host name
	private String clientId;
	
	private String backupClientId;
	
	private long activeTS;
	
	private boolean alive;
	
	private boolean failoverStarted;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getBackupClientId() {
		return backupClientId;
	}

	public void setBackupClientId(String backupClientId) {
		this.backupClientId = backupClientId;
	}

	public long getActiveTS() {
		return activeTS;
	}

	public void setActiveTS(long activeTS) {
		this.activeTS = activeTS;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public boolean isFailoverStarted() {
		return failoverStarted;
	}

	public void setFailoverStarted(boolean failoverStarted) {
		this.failoverStarted = failoverStarted;
	}

	public boolean timeout() {
		
		System.out.println((System.currentTimeMillis() - this.getActiveTS()));
		
		return (System.currentTimeMillis() - this.getActiveTS()) > TIMEOUT ;
	}
	
	
	
	
}
