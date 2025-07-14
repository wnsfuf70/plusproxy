package kr.api.link.mohw.service003.v1;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;



@Profile(Service003Configuration.SERVICE)
@RefreshScope
@Configuration("service003v1Configuration")
@ConfigurationProperties(Service003Configuration.SERVICE + "." + Service003Configuration.VERSION)
public class Service003Configuration {

	/**
	 * 한국사회보장정보원(행공 by-pass)
	 * 
	 * 확인서 발급 4종
	 * (기초연금수급자확인서 발급)
	 * (차상위계층확인서 발급)
	 * (장애인연금,장애수당,장애아동수당대상자확인서 발급)
	 * (자활근로자확인서 발급)
	 */
	protected static final String SERVICE = "service003";
	protected static final String VERSION = "v1";

}
