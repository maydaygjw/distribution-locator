package ge.dl.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class CommonUtil {

	public static String getIP(String localhost) {
		
		if(!"127.0.0.1".equals(localhost) && !"localhost".equals(localhost)) {
			return localhost;
		}
		
		String result = null;  
        Enumeration<NetworkInterface> interfaces = null;  
        try {  
            interfaces = NetworkInterface.getNetworkInterfaces();  
        } catch (SocketException e) {  
            // handle error  
        }  

        if (interfaces != null) {  
            while (interfaces.hasMoreElements() && !"".equals(result)) {  
                NetworkInterface i = interfaces.nextElement();  
                Enumeration<InetAddress> addresses = i.getInetAddresses();  
                while (addresses.hasMoreElements() && (result == null || result.isEmpty())) {  
                    InetAddress address = addresses.nextElement();  
                    if (!address.isLoopbackAddress()  &&  
                            address.isSiteLocalAddress()) {  
                        result = address.getHostAddress();  
                    }  
                }  
            }  
        }
        
        return result;
	}
	
	public static void main(String[] args) {
		System.out.println(CommonUtil.getIP("127.0.0.1"));
	}
}
