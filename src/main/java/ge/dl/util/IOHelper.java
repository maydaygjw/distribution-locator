package ge.dl.util;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;

public class IOHelper {

	public static void write(BufferedOutputStream bos, byte[] buf) {
		try {
			bos.write(buf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void write(Socket sock, byte[] buf) {
	}

	public static void write(Socket sock, Serializable serializable) {
		OutputStream os = SocketHelper.getOutputStream(sock);

		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(os);
			oos.writeObject(serializable);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static Object readObject(Socket sock) {
		Object result = null;

		try {
			InputStream is = sock.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			result = ois.readObject();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
}
