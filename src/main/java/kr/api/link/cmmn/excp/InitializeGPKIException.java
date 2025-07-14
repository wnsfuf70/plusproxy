package kr.api.link.cmmn.excp;

public class InitializeGPKIException extends Exception {
	private static final long serialVersionUID = 1L;
	public InitializeGPKIException(String string) {
		super(string);
	}
	public InitializeGPKIException(Throwable cause) {
        super(cause);
    }
	public InitializeGPKIException(String string, Throwable cause) {
        super(string, cause);
    }
}
