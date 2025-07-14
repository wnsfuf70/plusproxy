package kr.api.link.cmmn.crpt_back;

import java.util.Base64;

public interface Crypto {
	
	//암호화
	public byte[] encrypt(byte[] plain , byte[] targetCert) throws Exception;
	
	//전자서명
	public byte[] sign(byte[] encrypted) throws Exception;
	
	//인코딩
	public default String base64Encode(byte[] singOrEncrypted) throws IllegalArgumentException {
		return Base64.getEncoder().encodeToString(singOrEncrypted);
	}
	
	//디코딩
	public default byte[] base64Decode(String encodedString) throws IllegalArgumentException {
		return Base64.getDecoder().decode(encodedString);
	}

	//전자서명검증
	public byte[] validate(byte[] decoded) throws Exception;
	
	//복호화
	public byte[] decrypt(byte[] encryptedOrValidated) throws Exception;
	
}