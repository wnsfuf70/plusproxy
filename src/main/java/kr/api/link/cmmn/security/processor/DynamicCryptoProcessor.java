package kr.api.link.cmmn.security.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.api.link.cmmn.security.constant.CryptoType;
import kr.api.link.cmmn.security.core.CryptoProcessor;
import kr.api.link.cmmn.security.core.context.CryptoContext;

public class DynamicCryptoProcessor implements CryptoProcessor {

    private final Map<CryptoType, CryptoProcessor> registry = new HashMap<>();

    public DynamicCryptoProcessor(List<CryptoProcessor> processors) {
    	
        for (CryptoProcessor processor : processors) {
            String typeName = processor.getType();
            if (typeName == null) {
                throw new IllegalArgumentException("Processor returned null for getType(): " + processor.getClass());
            }
            CryptoType type = CryptoType.from(typeName);
            registry.put(type, processor);
        }
        
    }

    private CryptoProcessor resolve(CryptoContext ctx) {
        CryptoType type = CryptoContext.Util.getCryptoType(ctx);
        CryptoProcessor processor = registry.get(type);

        if (processor == null) {
            throw new IllegalStateException("No CryptoProcessor registered for type: " + type);
        }
        return processor;
    }

    @Override
    public byte[] encrypt(byte[] plain, CryptoContext ctx) throws Exception {
        return resolve(ctx).encrypt(plain, ctx);
    }

    @Override
    public byte[] decrypt(byte[] cipher, CryptoContext ctx) throws Exception {
        return resolve(ctx).decrypt(cipher, ctx);
    }

    @Override
    public String getType() {
        return "DYNAMIC";
    }
}
