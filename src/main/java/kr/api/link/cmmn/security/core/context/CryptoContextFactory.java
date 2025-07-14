package kr.api.link.cmmn.security.core.context;

import kr.api.link.cmmn.security.constant.CryptoType;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig.Crypto;

public class CryptoContextFactory {

    public static CryptoContext fromServiceConfig(ServiceConfig serviceConfig) {
    	
    	Crypto crypto = serviceConfig.getCrypto();
    	
        CryptoContext ctx = new CryptoContext();

        ctx.put(CryptoKeys.USE_CRYPTO, crypto.isUse());
        ctx.put(CryptoKeys.USE_CRYPTO_REQUEST, crypto.isUse());
        ctx.put(CryptoKeys.USE_CRYPTO_RESPONSE, crypto.isUse());
        
        if(crypto.getUseRequest()!=null) {
        	 ctx.put(CryptoKeys.USE_CRYPTO_REQUEST, crypto.getUseRequest());
        }
        
        if(crypto.getUseResponse()!=null) {
        	ctx.put(CryptoKeys.USE_CRYPTO_RESPONSE, crypto.getUseResponse());
        }
        
        // Encoding
        //ctx.put(CryptoKeys.USE_ENCODING, crypto.getEncoding().isUse());
        ctx.put(CryptoKeys.ENCODING_TYPE, crypto.getEncoding().getType());
        // Signing
        ctx.put(CryptoKeys.USE_GPKI_SIGN, crypto.getSign().isUse());
        
        ctx.put(CryptoKeys.CRYPTO_TYPE, crypto.getType());
        if(CryptoType.from(crypto.getType()) == CryptoType.GPKI) {
        	 // GPKI
            ServiceConfig.Crypto.Gpki gpki = crypto.getGpki();
            
            ctx.put(CryptoKeys.TARGET_GPKI_CERT_ID, gpki.getTargetServerId());
            ctx.put(CryptoKeys.FORCE_REFRESH, gpki.getForceRefresh() != null ? gpki.getForceRefresh() : false);
            
            long forceRefreshTimeMillis = gpki.getForceRefreshTimeMillis();
            if(forceRefreshTimeMillis > 0) {
            	ctx.put(CryptoKeys.FORCE_REFRESH_TIME_MILLIS, forceRefreshTimeMillis);
            }
            
            if(gpki.getLdap().getUse()!=null) {
            	ctx.put(CryptoKeys.USE_LDAP, gpki.getLdap().getUse());
            }
            
            if(gpki.getLdapUrl()!=null) {
            	ctx.put(CryptoKeys.LDAP_URL, gpki.getLdapUrl());
            }
            
        }
        else if(CryptoType.from(crypto.getType()) == CryptoType.ARIA) {
	    	 // ARIA
	        ServiceConfig.Crypto.Aria aria = crypto.getAria();
	        ctx.put(CryptoKeys.ARIA_KEY_ENC, aria.getKeyEnc());
	        
	        if(aria.getKeyMac()==null) {
	        	ctx.put(CryptoKeys.ARIA_KEY_ENC, aria.getKeyMac());
	        }
	        else {
	        	ctx.put(CryptoKeys.ARIA_KEY_MAC, aria.getKeyMac());
	        }
	        
	        ctx.put(CryptoKeys.ARIA_KEY_SIZE, aria.getKeySize());
        }
        
        return ctx;
    }
    
}
