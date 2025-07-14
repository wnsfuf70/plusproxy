package kr.api.link.cmmn.security.constant;

public enum EncodingType {
	
    BASE64,
    HEX,
    NONE;

    public static EncodingType from(String value) {
        if (value == null) return BASE64;
        try {
            return EncodingType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BASE64;
        }
    }
}