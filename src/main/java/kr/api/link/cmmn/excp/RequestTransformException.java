package kr.api.link.cmmn.excp;

public class RequestTransformException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public RequestTransformException(String string) {
		super(string);
	}
	public RequestTransformException(Throwable cause) {
        super(cause);
    }
	public RequestTransformException(String string, Throwable cause) {
        super(string, cause);
    }
}
