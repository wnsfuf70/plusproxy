package kr.api.link.cmmn.excp;

public class InitializeARIAException extends Exception {
	private static final long serialVersionUID = 1L;
	public InitializeARIAException(String string) {
		super(string);
	}
	public InitializeARIAException(Throwable cause) {
        super(cause);
    }
	public InitializeARIAException(String string, Throwable cause) {
        super(string, cause);
    }
}
