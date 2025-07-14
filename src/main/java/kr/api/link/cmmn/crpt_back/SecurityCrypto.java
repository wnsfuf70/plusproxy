package kr.api.link.cmmn.crpt_back;

import java.io.File;

import org.springframework.util.ObjectUtils;

import com.gpki.gpkiapi.exception.GpkiApiException;

import kr.api.link.cmmn.excp.InitializeGPKIException;
import kr.api.link.cmmn.excp.RequestEncryptException;
import kr.api.link.cmmn.excp.ResponseDecryptException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityCrypto {
	
	Crypto crypto;
	
	private byte[] targetCert;

	/**
	 * 요청 바디 암호화
	 * (GPKI : encrypt -> sign -> encode)
	 */
	public byte[] encrypt(byte[] plain) throws RequestEncryptException {
		try {
			return crypto.encrypt(plain,targetCert);
		}
		catch(Exception e) {
			throw new RequestEncryptException("GPKI Encrypt Error...",e);
		}
	}

	//전자서명
	public byte[] sign(byte[] bytes) throws RequestEncryptException {
		try {
			return crypto.sign(bytes);	
		}
		catch(Exception e) {
			throw new RequestEncryptException("GPKI Signed.. Error",e);
		}
	}
	
	public String encode(byte[] encoderingBytes) {
		try {
			return crypto.base64Encode(encoderingBytes);
		}
		catch(Exception e) {
			throw new RequestEncryptException("GPKI Encode.. Error",e);
		}
	}
	
	/**
	 * 응답 바디 복호화
	 * (GPKI : decode -> validate -> decrypt)
	 */
	public byte[] decode(String encrypted) throws ResponseDecryptException {
		try {
			return crypto.base64Decode(encrypted); 
		} catch (Exception e) {
			throw new ResponseDecryptException("GPKI decrypt.. Error",e);
			//throw new ResponseDecryptException("GPKI Module not initialized.",e);
		}
	}

	public byte[] validate(byte[] validaBytes) throws ResponseDecryptException {
		try {
			return crypto.validate(validaBytes);
		} catch (Exception e) {
			throw new ResponseDecryptException("GPKI validate Error...",e);
		}
	}
	
	public byte[] decrypt(byte[] encrypted) throws ResponseDecryptException {
		try {
			
			return crypto.decrypt(encrypted);
		} catch (Exception e) {
			throw new ResponseDecryptException("GPKI decrypt.. Error",e);
			//throw new ResponseDecryptException("GPKI Module not initialized.",e);
		}
	}
}