package kr.api.link.cmmn.excp;

public class ResponseDecryptException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public ResponseDecryptException(String string) {
		super(string);
	}
	public ResponseDecryptException(Throwable cause) {
        super(cause);
    }
	public ResponseDecryptException(String string, Throwable cause) {
        super(string, cause);
    }
}
