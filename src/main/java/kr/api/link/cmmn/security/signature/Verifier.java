package kr.api.link.cmmn.security.signature;

import kr.api.link.cmmn.security.core.context.CryptoContext;

public interface Verifier {
	
	byte[] verify(byte[] data, CryptoContext ctx) throws Exception;
	
}
