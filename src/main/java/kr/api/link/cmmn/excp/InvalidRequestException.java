package kr.api.link.cmmn.excp;

public class InvalidRequestException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public InvalidRequestException(String string) {
		super(string);
	}
	public InvalidRequestException(Throwable cause) {
        super(cause);
    }
	public InvalidRequestException(String string, Throwable cause) {
        super(string, cause);
    }
}
