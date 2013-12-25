package ge.dl.util;

import ge.dl.exception.NetIOException;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class SocketHelper {
	
	public static void setKeepAlive(Socket sock) {
		try {
			sock.setKeepAlive(true);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static InputStream getInputStream(Socket s) {
		
		InputStream is = null;
		
		try {
			is = s.getInputStream();
		} catch (IOException e) {
			throw new NetIOException(e);
		}
		
		return is;
	}
	
	public static OutputStream getOutputStream(Socket s) {
		OutputStream out = null;
		
		try {
			out = s.getOutputStream();
		}catch(IOException e) {
			throw new NetIOException(e);
		}
		
		return out;
	}
	
	public static BufferedOutputStream getBufferedOutputStream(Socket s) {
		OutputStream out = getOutputStream(s);
		return new BufferedOutputStream(out);
	}

	public static void closeIn(InputStream in) {
		if(in != null) {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void closeOut(OutputStream out) {
		if(out != null) {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void closeSocket(Socket sock) {
		if(sock != null) {
			try {
				sock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void setSoTimeout(Socket sock, int timeout) {
		try {
			sock.setSoTimeout(timeout);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
