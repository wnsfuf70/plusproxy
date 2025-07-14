package kr.api.link.cmmn.security.processor;

import kr.api.link.cmmn.crpt_back.ARIAModule;
import kr.api.link.cmmn.excp.ARIACryptoException;
import kr.api.link.cmmn.excp.InitializeARIAException;
import kr.api.link.cmmn.security.core.CryptoProcessor;
import kr.api.link.cmmn.security.core.context.CryptoContext;
import kr.api.link.cmmn.security.core.context.CryptoKeys;

public class ARIAProcessor implements CryptoProcessor {

    @Override
    public byte[] encrypt(byte[] plain, CryptoContext ctx) throws Exception {
        ARIAModule module = buildModule(ctx);
        return module.encrypt(plain);
    }

    @Override
    public byte[] decrypt(byte[] cipher, CryptoContext ctx) throws Exception {
        ARIAModule module = buildModule(ctx);
        return module.decrypt(cipher);
    }

    @Override
    public String getType() {
        return "ARIA";
    }

    private ARIAModule buildModule(CryptoContext ctx) throws InitializeARIAException {
    	
        int keySize = ctx.getOrDefault(CryptoKeys.ARIA_KEY_SIZE, 128);
        String key = ctx.get(CryptoKeys.ARIA_KEY_ENC);
        
        if (key == null || key.isEmpty()) {
            throw new InitializeARIAException("<crypto.aria.key> is empty.");
        }

        if (keySize != 128 && keySize != 192 && keySize != 256) {
            throw new InitializeARIAException("Invalid ARIA keySize: " + keySize);
        }

        try {
            String normalizedKey = key.length() > (keySize / 8)
                ? key.substring(0, keySize / 8)
                : key;
            
            return new ARIAModule(keySize, normalizedKey.getBytes());
        } catch (ARIACryptoException e) {
            throw new InitializeARIAException(e);
        }
        
    }
}