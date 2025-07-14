package kr.api.link.cmmn.security.utils;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Service;

import kr.api.link.cmmn.security.core.context.CryptoContext;
import kr.api.link.cmmn.security.processor.DynamicCryptoProcessor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemplateCryptoService {
	
    private final VelocityEngine velocityEngine;
    private final DynamicCryptoProcessor cryptoProcessor;

    public String renderAndEncrypt(String template, VelocityContext vCtx, CryptoContext cCtx) throws Exception{
        StringWriter buf = new StringWriter();
        velocityEngine.mergeTemplate(template, "UTF-8", vCtx, buf);
        byte[] cipher = cryptoProcessor.encrypt(buf.toString().getBytes(StandardCharsets.UTF_8), cCtx);
        return new String(cipher, StandardCharsets.UTF_8);
    }
    public String decrypt(byte[] cipherText, CryptoContext ctx) throws Exception{
        byte[] plain = cryptoProcessor.decrypt(cipherText, ctx);
        return new String(plain, StandardCharsets.UTF_8);
    }
}
