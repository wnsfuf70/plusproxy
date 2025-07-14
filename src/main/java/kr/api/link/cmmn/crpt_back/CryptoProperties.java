package kr.api.link.cmmn.crpt_back;

import lombok.Data;

@Data
public class CryptoProperties {
	
	private boolean enable;
	private CryptoType type;
	
	// gpki
	private String targetCertId;
	private boolean useLdap;
	private String ldapUrl = "ldap://10.1.7.118:389/cn=";
	
	// aria
	private int keySize;
	private String key;
	
	public static enum CryptoType {NONE, GPKI, ARIA}
}
