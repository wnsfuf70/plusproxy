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
public class DecryptionTransformer {

    private final DigitalCheck digitalCheck;
    private final CodecFactory codecFactory;

    public DecryptionTransformer(DigitalCheck digitalCheck, CodecFactory codecFactory) {
        this.digitalCheck = digitalCheck;
        this.codecFactory = codecFactory;
    }

    public byte[] transform(CryptoContext ctx, byte[] input, CryptoProcessor processor) throws Exception {
        
    	String cryptoTypeString = ctx.get(CryptoKeys.CRYPTO_TYPE);
    	CryptoType type = CryptoType.from(cryptoTypeString);
    	
    	if (!CryptoContext.Util.isResponseCryptoEnabled(ctx)) {
    		log.debug("[{}] [RESPONSE RECIVE] : No Crypto Service...",type);
    	    return input;
    	}

        byte[] decoded = input;
        EncodingType encodingType = CryptoContext.Util.getEncodingType(ctx);
        if (encodingType != EncodingType.NONE) {
        	log.debug("[{}] start message decoding... {}",type,encodingType);
	        Codec codec = codecFactory.get(encodingType); 
	        decoded = codec.decode(input);
	        
        }
 
        if (CryptoContext.Util.isSignEnabled(ctx)) {
        	log.debug("[{}] start message validate ...",type);
        	decoded = digitalCheck.verify(decoded, ctx); 
        }
        
        log.debug("[{}] start message decrypt ...",type);
        return processor.decrypt(decoded, ctx);
        
    }
    
}