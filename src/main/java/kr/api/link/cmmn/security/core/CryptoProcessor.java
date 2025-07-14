package kr.api.link.cmmn.security.core;

import kr.api.link.cmmn.security.core.context.CryptoContext;

public interface CryptoProcessor {
	
    byte[] encrypt(byte[] plaintext, CryptoContext ctx) throws Exception;
    byte[] decrypt(byte[] ciphertext, CryptoContext ctx) throws Exception;

    /** processor type key (e.g., GPKI, ARIA) */
    String getType();
}
