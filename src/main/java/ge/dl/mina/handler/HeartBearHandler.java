package ge.dl.mina.handler;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class HeartBearHandler extends IoHandlerAdapter {

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		
		cause.printStackTrace();
	}
	
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		// TODO Auto-generated method stub
		super.messageReceived(session, message);
	}
	
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		
		System.out.println( "IDLE " + session.getIdleCount( status ));
	}

}
