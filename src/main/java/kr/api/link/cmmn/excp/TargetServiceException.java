package kr.api.link.cmmn.excp;

public class TargetServiceException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public int status = 505;
	public TargetServiceException(String string) {
		super(string);
	}
	public TargetServiceException(Throwable cause) {
		super(cause);
	}
	public TargetServiceException(String string, Throwable cause) {
		super(string, cause);
	}
	public TargetServiceException(int status, String string) {
		super(string);
		this.status = status;
	}
	public TargetServiceException(int status, Throwable cause) {
        super(cause);
        this.status = status;
    }
	public TargetServiceException(int status, String string, Throwable cause) {
        super(string, cause);
        this.status = status;
    }
}
