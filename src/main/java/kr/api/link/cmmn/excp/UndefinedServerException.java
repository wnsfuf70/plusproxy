package kr.api.link.cmmn.excp;

public class UndefinedServerException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public UndefinedServerException(String string) {
		super(string);
	}
	public UndefinedServerException(Throwable cause) {
        super(cause);
    }
	public UndefinedServerException(String string, Throwable cause) {
        super(string, cause);
    }
}
