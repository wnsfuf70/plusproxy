package kr.api.link.cmmn.crpt_back;

import org.springframework.util.ObjectUtils;

import kr.api.link.cmmn.excp.ARIACryptoException;
import kr.api.link.cmmn.excp.InitializeARIAException;
import kr.api.link.cmmn.excp.RequestEncryptException;
import kr.api.link.cmmn.excp.ResponseDecryptException;

public class ARIACrypto {
	
	ARIAModule ariaModule;
	
	/**
	 * ARIA load
	 * 1. key size check & setting
	 * 2. key check & setting
	 * 3. ARIA Module load
	 */
	public ARIACrypto(int keySize, String key) throws InitializeARIAException {
		if (ObjectUtils.isEmpty(keySize)) {
			throw new InitializeARIAException("<crypto.aria.key-size> is empty.");
		} else if (keySize != 128 && keySize != 192 && keySize != 256) {
			throw new InitializeARIAException("invalid <crypto.aria.key-size>");
		} else if (ObjectUtils.isEmpty(key)) {
			throw new InitializeARIAException("<crypto.aria.key> is empty.");
		}
		if (ariaModule == null) {
			try {
				ariaModule = new ARIAModule(keySize, (key.length() > 32 ? key.substring(0, keySize/8) : key).getBytes());
			} catch (ARIACryptoException e) {
				throw new InitializeARIAException(e);
			}
		}
	}

	/**
	 * 요청 바디 암호화
	 * (GPKI : encrypt -> encode)
	 */
	public byte[] encrypt(byte[] plain) throws RequestEncryptException {
		try {
			if (ariaModule != null) {
				byte[] encrypted = ariaModule.encrypt(plain);
				return encrypted;
			} else {
				throw new ARIACryptoException("ARIA Module not initialized.");
			}
		} catch (ARIACryptoException e) {
			throw new RequestEncryptException(e);
		}
	}

	/**
	 * 응답 바디 복호화
	 * (ARIA : decode -> decrypt)
	 */
	public byte[] decrypt(byte[] encrypted) throws ResponseDecryptException {
		try {

			if (ariaModule != null) {
				return ariaModule.decrypt(encrypted);
			} else {
				throw new ARIACryptoException("ARIA Module not initialized.");
			}
		} catch (ARIACryptoException e) {
			throw new ResponseDecryptException(e);
		}
	}


}
