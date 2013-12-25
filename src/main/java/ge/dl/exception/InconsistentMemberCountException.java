package ge.dl.exception;

public class InconsistentMemberCountException extends Exception {

private static final long serialVersionUID = 1L;
	
	public InconsistentMemberCountException(Throwable cause) {
		super(cause);
	}
	
	public InconsistentMemberCountException(String name) {
		super(name);
	}
}
