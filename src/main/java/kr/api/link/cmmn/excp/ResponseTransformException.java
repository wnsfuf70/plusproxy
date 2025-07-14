package kr.api.link.cmmn.excp;

public class ResponseTransformException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public ResponseTransformException(String string) {
		super(string);
	}
	public ResponseTransformException(Throwable cause) {
        super(cause);
    }
	public ResponseTransformException(String string, Throwable cause) {
        super(string, cause);
    }
}
