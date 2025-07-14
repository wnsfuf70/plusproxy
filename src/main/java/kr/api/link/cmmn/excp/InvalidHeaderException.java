package kr.api.link.cmmn.excp;

public class InvalidHeaderException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public InvalidHeaderException(String string) {
		super(string);
	}
	public InvalidHeaderException(Throwable cause) {
        super(cause);
    }
	public InvalidHeaderException(String string, Throwable cause) {
        super(string, cause);
    }
}
