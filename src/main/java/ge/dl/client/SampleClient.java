package ge.dl.client;

import ge.dl.api.Message;
import ge.dl.util.IOHelper;

import java.net.Socket;

public class SampleClient {

	public static void main(String[] args) throws  Exception {
		
		Socket sock = new Socket("localhost", 8888);
		
		Message msg = new Message(Message.RegisterRequest);
		
		IOHelper.write(sock, msg);
	}
}
