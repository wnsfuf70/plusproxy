package kr.api.link.cmmn.excp;

public class PathNotFoundException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public static final String NOT_FOUND_MESSAGE_PREFIX = "request path not found : ";
	
	public PathNotFoundException(String string) {
		super(string);
	}
	public PathNotFoundException(Throwable cause) {
        super(cause);
    }
	public PathNotFoundException(String string, Throwable cause) {
        super(string, cause);
    }

}
