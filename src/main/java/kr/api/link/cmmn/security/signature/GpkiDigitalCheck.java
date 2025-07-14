package kr.api.link.cmmn.security.signature;

import org.springframework.stereotype.Component;

import kr.api.link.cmmn.security.core.context.CryptoContext;
import kr.api.link.cmmn.security.core.module.GPKIModule;

@Component
public class GpkiDigitalCheck extends DigitalCheck {

    private final GPKIModule module = GPKIModule.INSTANCE;

    @Override
    public byte[] sign(byte[] data, CryptoContext ctx) throws Exception {
        return module.sign(data);
    }

    @Override
    public byte[] verify(byte[] signedData, CryptoContext ctx) throws Exception {
        return module.validate(signedData);
    }
    
}