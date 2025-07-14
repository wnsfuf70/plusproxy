package kr.api.link.cmmn.excp;

public class DifferentDataKeyException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public DifferentDataKeyException(String string) {
		super(string);
	}
	public DifferentDataKeyException(Throwable cause) {
        super(cause);
    }
	public DifferentDataKeyException(String string, Throwable cause) {
        super(string, cause);
    }
}
