package ge.dl.api;

import ge.dl.util.CommonUtil;
import ge.dl.util.UDPClientUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RegistryInfo {

	public Map<String, String> locatorGroup = new HashMap<String, String>();

	public Map<String, MemberInfo> members = new HashMap<String, MemberInfo>();

	public MemberInfo getMemberInfo(String clientID) {

		MemberInfo info;

		synchronized (this) {
			info = members.get(clientID);
		}

		return info;
	}

	public void updateActiveTS(String clientID) {

		synchronized (this) {
			MemberInfo info = members.get(clientID);
			if (info != null)
				info.setActiveTS(CommonUtil.getCurrentTimestamp());
		}

	}

	public synchronized void setBackupStarted(String clientID) {

		MemberInfo info = getMemberInfo(clientID);
		info.setFailoverStarted(true);

	}

	public void addMember(MemberInfo info) {
		members.put(info.getClientId(), info);
	}

	public void synchronize(HashMap<String, MemberInfo> members) {

		synchronized (this) {
			this.members = members;
		}
	}

	public void synchronizeAll(Set<String> locatorMembers) {
		for (String member : locatorMembers) {
			UDPClientUtil.send(member, 5678, members);
		}
	}
	
	public Map<String, MemberInfo> getAllMembers() {
		
		return this.members;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
