package kr.api.link.cmmn.excp;

public class RequestEncryptException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public RequestEncryptException(String string) {
		super(string);
	}
	public RequestEncryptException(Throwable cause) {
        super(cause);
    }
	public RequestEncryptException(String string, Throwable cause) {
        super(string, cause);
    }
}
