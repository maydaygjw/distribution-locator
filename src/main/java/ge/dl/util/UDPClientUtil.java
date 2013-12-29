package ge.dl.util;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class UDPClientUtil {

	public static void send(String ip, Object obj) {

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(oos);

			byte[] buf = baos.toByteArray();
			System.out.println(buf.length);

			DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(ip, 5678));
			DatagramSocket ds = new DatagramSocket(null);
			ds.send(dp);
			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
