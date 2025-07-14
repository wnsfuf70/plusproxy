package kr.api.link.cmmn.excp;

public class TargetApiException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public TargetApiException(String string) {
		super(string);
	}
	public TargetApiException(Throwable cause) {
        super(cause);
    }
	public TargetApiException(String string, Throwable cause) {
        super(string, cause);
    }
}
