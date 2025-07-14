package kr.api.link.cmmn.security.transformer;

import org.springframework.stereotype.Component;

import kr.api.link.cmmn.security.constant.CryptoType;
import kr.api.link.cmmn.security.constant.EncodingType;
import kr.api.link.cmmn.security.core.CryptoProcessor;
import kr.api.link.cmmn.security.core.context.CryptoContext;
import kr.api.link.cmmn.security.core.context.CryptoKeys;
import kr.api.link.cmmn.security.encoding.Codec;
import kr.api.link.cmmn.security.encoding.CodecFactory;
import kr.api.link.cmmn.security.signature.DigitalCheck;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EncryptionTransformer {

    private final DigitalCheck digitalCheck;
    private final CodecFactory codecFactory;

    public EncryptionTransformer(DigitalCheck digitalCheck, CodecFactory codecFactory) {
        this.digitalCheck = digitalCheck;
        this.codecFactory = codecFactory;
    }

    public byte[] transform(CryptoContext ctx, byte[] plain, CryptoProcessor processor) throws Exception {
    	
    	String cryptoTypeString = ctx.get(CryptoKeys.CRYPTO_TYPE);
    	CryptoType type = CryptoType.from(cryptoTypeString);
    	
    	if (!CryptoContext.Util.isRequestCryptoEnabled(ctx)) {
    		log.debug("[{}][REQUEST SEND] : No Crypto Service...",type);
    		return plain;
        }
    	
    	 log.debug("[{}] start message encrypt ...",type);
        byte[] encrypted = processor.encrypt(plain, ctx);

        if (CryptoContext.Util.isSignEnabled(ctx)) {
        	log.debug("[{}] start message sign...",type);
            encrypted = digitalCheck.sign(encrypted, ctx);
        }

        EncodingType encodingType = CryptoContext.Util.getEncodingType(ctx);
        if (encodingType != EncodingType.NONE) {
        	log.debug("[{}] start message encoding... {}",type,encodingType);
            Codec codec = codecFactory.get(encodingType); // ✅ 팩토리 선택
            encrypted = codec.encode(encrypted);
        }

        return encrypted;
    }
}