package kr.api.link.cmmn.v2.configurable;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import kr.api.link.cmmn.v2.configurable.init.LoadDatabaseTransistor;
import kr.api.link.cmmn.v2.configurable.init.LoadPropertiesTransistor;
import kr.api.link.cmmn.v2.configurable.init.LoadTransistor;
import kr.api.link.cmmn.v2.service.flow.isp.model.HttpFacadeInterfaceHolder;
import kr.api.link.cmmn.v2.service.flow.isp.model.HttpFacadeServiceTemplete;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ConfigurableServiceConfiguration {
	
	@Value("${service.external.location}")
	String loadType;
	
	@Bean
	public VelocityEngine getVelocityEngine() {
		VelocityEngine engine = new VelocityEngine();
		//engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.setProperty("input.encoding", "UTF-8");
		engine.setProperty("output.encoding", "UTF-8");
		engine.setProperty("file.resource.loader.cache", false);
		engine.init();
		return engine;
	}
	
	@Bean("LoadTransistor")
	public LoadTransistor getLoadTransistor() throws InterruptedException {
		
		log.debug("load type : {}",loadType);
		if("DB".equalsIgnoreCase(loadType)) {
			return new LoadDatabaseTransistor();
		}
		
		return new LoadPropertiesTransistor();
	}
	
    @Bean(name = "getHttpFacadeServiceTemplete")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public HttpFacadeServiceTemplete httpFacadeServiceTemplete() {
        HttpFacadeServiceTemplete serviceTemplete = new HttpFacadeServiceTemplete(new HttpFacadeInterfaceHolder());
        return serviceTemplete;
    }
    
    @Bean(name = "getRestTemplate")
    public RestTemplate restTemplate() {
        try {
            // 1. SSLContext: 모든 인증서 신뢰
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial(null, TrustAllStrategy.INSTANCE) // Self-signed 포함 모든 인증서 신뢰
                    .build();

            // 2. 커넥션 풀 매니저 생성 (SSL 설정 포함)
            PoolingHttpClientConnectionManager connectionManager =
                    PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(
                        SSLConnectionSocketFactoryBuilder.create()
                                .setSslContext(sslContext)
                                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE) // 호스트네임 검증 무시
                                .build()
                )
                .build();

            connectionManager.setMaxTotal(200);             // 전체 커넥션 수
            connectionManager.setDefaultMaxPerRoute(50);    // 라우트당 최대 커넥션 수
            
            // 3. HttpClient 생성
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .build();

            // 4. RestTemplate에 HttpClient 적용
            HttpComponentsClientHttpRequestFactory factory =
                    new HttpComponentsClientHttpRequestFactory(httpClient);
            factory.setConnectTimeout(5000); // 연결 타임아웃 5초
            
            return new RestTemplate(factory);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create RestTemplate with SSL disabled", e);
        }
    }
    
}