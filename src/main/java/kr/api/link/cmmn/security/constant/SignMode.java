package kr.api.link.cmmn.security.constant;

public enum SignMode {
	
    ENABLED(true),
    DISABLED(false);

    private final boolean enabled;

    SignMode(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public static SignMode from(boolean flag) {
        return flag ? ENABLED : DISABLED;
    }
    
}