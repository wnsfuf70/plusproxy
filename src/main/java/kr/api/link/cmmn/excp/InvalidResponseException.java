package kr.api.link.cmmn.excp;

public class InvalidResponseException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public InvalidResponseException(String string) {
		super(string);
	}
	public InvalidResponseException(Throwable cause) {
        super(cause);
    }
	public InvalidResponseException(String string, Throwable cause) {
        super(string, cause);
    }
}
