package kr.api.link.cmmn.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "crypto.gpki")
@Data
public class GpkiProperties {
	
    private String mountPath;
    private String copyPath;
    private String certId;
    private String envKeyPassword;
    private String sigKeyPassword;
    private boolean testGpki;
    private String ldapUrl;
    
}