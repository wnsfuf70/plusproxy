package kr.api.link.cmmn.security.signature;

import kr.api.link.cmmn.security.core.context.CryptoContext;

public abstract class DigitalCheck implements Signer, Verifier {
    
    @Override
    public abstract byte[] sign(byte[] data, CryptoContext ctx) throws Exception;

    @Override
    public abstract byte[] verify(byte[] signedData, CryptoContext ctx) throws Exception;
    
}