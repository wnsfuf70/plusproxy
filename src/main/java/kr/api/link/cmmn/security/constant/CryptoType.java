package kr.api.link.cmmn.security.constant;

public enum CryptoType {
	
	GPKI, ARIA;

	public static CryptoType from(String value) {
		
		if (value == null)
			return GPKI;
		
		try {
			return CryptoType.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid CryptoType: " + value);
		}
		
	}
	
}