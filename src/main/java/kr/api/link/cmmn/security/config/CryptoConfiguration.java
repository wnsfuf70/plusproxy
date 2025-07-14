package kr.api.link.cmmn.security.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.api.link.cmmn.security.core.module.GPKIModule;
import kr.api.link.cmmn.security.encoding.CodecFactory;
import kr.api.link.cmmn.security.processor.ARIAProcessor;
import kr.api.link.cmmn.security.processor.DynamicCryptoProcessor;
import kr.api.link.cmmn.security.processor.GPKIProcessor;
import kr.api.link.cmmn.security.signature.DigitalCheck;
import kr.api.link.cmmn.security.signature.GpkiDigitalCheck;
import kr.api.link.cmmn.security.transformer.DecryptionTransformer;
import kr.api.link.cmmn.security.transformer.EncryptionTransformer;

@Configuration
public class CryptoConfiguration {
	
    @Bean
    public GPKIModule gpkiModule(GpkiProperties props) {
    	
        try {
            GPKIModule.INSTANCE.initialize(
    		props.getMountPath(),
            props.getCopyPath(),
            props.getCertId(),
            props.getEnvKeyPassword(),
            props.getSigKeyPassword(),
            props.isTestGpki(),
            props.getLdapUrl()
            );
            return GPKIModule.INSTANCE;
        } 
        catch (Exception e) {
            throw new IllegalStateException("Failed to initialize GPKIModule", e);
        }
        
    }

    @Bean
    public GPKIProcessor gpkiProcessor() {
        return new GPKIProcessor();
    }

    @Bean
    public ARIAProcessor ariaProcessor() {
        return new ARIAProcessor();
    }

    @Bean
    public CodecFactory codecFactory() {
        return new CodecFactory();
    }

    @Bean
    public DigitalCheck digitalCheck() {
        return new GpkiDigitalCheck();
    }

    @Bean
    public EncryptionTransformer encryptionTransformer(DigitalCheck digitalCheck, CodecFactory codecFactory) {
        return new EncryptionTransformer(digitalCheck, codecFactory);
    }

    @Bean
    public DecryptionTransformer decryptionTransformer(DigitalCheck digitalCheck, CodecFactory codecFactory) {
        return new DecryptionTransformer(digitalCheck, codecFactory);
    }

    @Bean
    public DynamicCryptoProcessor dynamicCryptoProcessor(GPKIProcessor gpkiProcessor, ARIAProcessor ariaProcessor) {
        return new DynamicCryptoProcessor(List.of(gpkiProcessor, ariaProcessor));
    }
    
}