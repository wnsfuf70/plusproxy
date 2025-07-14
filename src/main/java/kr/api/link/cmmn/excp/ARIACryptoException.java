package kr.api.link.cmmn.excp;

public class ARIACryptoException extends Exception {
	private static final long serialVersionUID = 1L;
	public ARIACryptoException(String string) {
		super(string);
	}
	public ARIACryptoException(Throwable cause) {
		super(cause);
	}
	public ARIACryptoException(String string, Throwable cause) {
        super(string, cause);
    }
}
