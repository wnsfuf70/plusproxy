package kr.api.link.cmmn.security.processor;

import org.springframework.stereotype.Component;

import kr.api.link.cmmn.security.core.CryptoProcessor;
import kr.api.link.cmmn.security.core.context.CryptoContext;
import kr.api.link.cmmn.security.core.module.GPKIModule;

@Component
public class GPKIProcessor implements CryptoProcessor {

    private final GPKIModule module = GPKIModule.INSTANCE;

    @Override
    public byte[] encrypt(byte[] plain, CryptoContext ctx) throws Exception {
        byte[] cert = module.getCert(ctx);
        return module.encrypt(plain, cert);
    }

    @Override
    public byte[] decrypt(byte[] cipher, CryptoContext ctx) throws Exception {
        return module.decrypt(cipher);
    }

    @Override
    public String getType() {
        return "GPKI";
    }
}
