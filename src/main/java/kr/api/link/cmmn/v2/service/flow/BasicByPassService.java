package kr.api.link.cmmn.v2.service.flow;

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Resource;
import kr.api.link.cmmn.excp.ResponseTransformException;
import kr.api.link.cmmn.security.core.context.CryptoContext;
import kr.api.link.cmmn.security.processor.DynamicCryptoProcessor;
import kr.api.link.cmmn.security.transformer.DecryptionTransformer;
import kr.api.link.cmmn.security.transformer.EncryptionTransformer;
import kr.api.link.cmmn.v2.configurable.ConfigurableInvoke;
import kr.api.link.cmmn.v2.configurable.model.ConfigurableContext;
import kr.api.link.cmmn.v2.configurable.model.HttpRequestContext;
import kr.api.link.cmmn.v2.configurable.model.HttpResponseContext;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig.Backend;
import kr.api.link.cmmn.v2.service.CommonService;
import kr.api.link.cmmn.v2.service.component.api.RestTempleteHttpCaller;
import kr.api.link.cmmn.v2.service.component.schema.JsonRequestSchemaValidator;
import kr.api.link.cmmn.v2.service.component.schema.JsonResponseSchemaValidator;
import kr.api.link.cmmn.v2.service.flow.isp.Convertable;
import kr.api.link.cmmn.v2.service.flow.isp.combine.LockingConverterChain;
import kr.api.link.cmmn.v2.service.flow.isp.model.HttpFacadeInterfaceHolder;
import kr.api.link.cmmn.v2.service.flow.isp.model.HttpFacadeServiceTemplete;
import kr.api.link.cmmn.v2.service.support.RequestConvertableChain;
import kr.api.link.cmmn.v2.service.support.ResponseConvertableChain;
import kr.api.link.cmmn.v2.service.support.util.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public final class BasicByPassService implements CommonService ,InitializingBean {
	
	@Resource(name="getHttpFacadeServiceTemplete")
	HttpFacadeServiceTemplete facade;
	
	@Resource(type = RestTempleteHttpCaller.class)
	RestTempleteHttpCaller caller;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	JsonRequestSchemaValidator requestSchemaValidator;
	
	@Autowired
	JsonResponseSchemaValidator responseSchemaValidator;
	
	@Autowired
	DynamicCryptoProcessor dynamicCryptoProcessor;
	
	@Autowired
	EncryptionTransformer encryptionTransformer;
	
	@Autowired
	DecryptionTransformer decryptionTransformer;
	
	@Override
	public ResponseEntity<JsonNode> service(HttpRequestContext<JsonNode> httpRequestContext) throws Exception {
		log.debug("service bypass {}",httpRequestContext.getPath());
		return facade.facadeProcess(httpRequestContext,httpRequestContext.getEntity());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		HttpFacadeInterfaceHolder interfaceHolder = facade.getInterfaceHolder();
		
		
		LockingConverterChain<RequestEntity<JsonNode>, RequestEntity<String>> requestChain = new LockingConverterChain<RequestEntity<JsonNode>, RequestEntity<String>>();
		requestChain.setDefaultStartAndEnd(new Convertable<RequestEntity<JsonNode>, HttpRequestContext<String>>() {
					@Override
					public HttpRequestContext<String> transform(ConfigurableContext ctx, RequestEntity<JsonNode> originSource)
							throws Exception {
						Map<String, String> singleValueMap = originSource.getHeaders().toSingleValueMap();
						String prettyString = originSource.getBody().toPrettyString();
						HttpRequestContext<Object> realType = HttpRequestContext.getRealType(ctx);
						RequestEntity<String> entity = 
							Utils.Http.makeRequestEntity(
								originSource.getUrl(),
								originSource.getMethod(), 
								singleValueMap,
								prettyString,
								MediaType.APPLICATION_JSON);
						return HttpRequestContext.generate(ctx.getInvoke(),entity,realType.getQueryStringMap());
					}
				}, new Convertable<HttpRequestContext<String>, RequestEntity<String>>() {
					@Override
					public RequestEntity<String> transform(ConfigurableContext ctx, HttpRequestContext<String> originSource) throws Exception {
						
						ConfigurableInvoke invoke = ctx.getInvoke();
						ServiceConfig serviceConfig = invoke.getServiceConfig();
						Backend backend = serviceConfig.getBackend();
						String address = backend.getAddress();
						HttpMethod mehtod = backend.getMehtod();
						Map<String, String> staticProp = backend.getHeader().getStaticProp();
						originSource.getHeaders().putAll(staticProp);
						return Utils.Http.makeRequestEntity(
										address,
										mehtod, 
										originSource.getHeaders(),
										originSource.getBody(),
										MediaType.APPLICATION_JSON);
					}
				});
		
		requestChain.setNextConverter(new RequestConvertableChain() {
			@Override
			public HttpRequestContext<String> transform(ConfigurableContext ctx, HttpRequestContext<String> originSource)
					throws Exception {
				
				CryptoContext cryptoContext = ctx.getInvoke().getCryptoContext();
				CryptoContext.Util.traceContext(cryptoContext, log);
				
				String body = originSource.getBody();
				byte[] transform = encryptionTransformer.transform(cryptoContext, body.getBytes(), dynamicCryptoProcessor);
				String string = new String(transform);
				log.debug("encrypt result : {}", string);
				originSource.setBody(new String(transform));
				return originSource;
			}
		});
		
		
		LockingConverterChain<ResponseEntity<String>, HttpResponseContext<JsonNode>> responseChain = new LockingConverterChain<ResponseEntity<String>, HttpResponseContext<JsonNode>>();
		responseChain.setDefaultStartAndEnd(new Convertable<ResponseEntity<String>,HttpResponseContext<String>>() {
					@Override
					public HttpResponseContext<String> transform(ConfigurableContext ctx, ResponseEntity<String> originSource) throws Exception {
						return HttpResponseContext.generate(ctx.getInvoke(), originSource);
					}
				}, new Convertable<HttpResponseContext<String>,HttpResponseContext<JsonNode>>() {
					@Override
					public HttpResponseContext<JsonNode> transform(ConfigurableContext ctx,
						HttpResponseContext<String> originSource) throws Exception {
						BodyBuilder bodyBuilder = ResponseEntity.ok();
						ResponseEntity<JsonNode> body = null;
						try {
							JsonNode node = objectMapper.readTree(originSource.getBody());
							 body = bodyBuilder.headers(originSource.getReadOnlyOriginHeader()).body(node);
						}
						catch(Exception e) {
						  throw new ResponseTransformException("응답메세지가 JSON 형식이 아닙니다.");
						}
						
					    return HttpResponseContext.generate(ctx.getInvoke(), body);
					}
				});
		
		responseChain.setNextConverter(new ResponseConvertableChain() {
					@Override
					public HttpResponseContext<String> transform(ConfigurableContext ctx, HttpResponseContext<String> originSource)
							throws Exception {
						return originSource;
					}
				});
		
		responseChain.setNextConverter(new ResponseConvertableChain() {
			
			@Override
			public HttpResponseContext<String> transform(ConfigurableContext ctx, HttpResponseContext<String> originSource)
					throws Exception {
				
				CryptoContext cryptoContext = ctx.getInvoke().getCryptoContext();
				CryptoContext.Util.traceContext(cryptoContext, log);
				
				String body = originSource.getBody();
				byte[] transform = decryptionTransformer.transform(cryptoContext, body.getBytes(), dynamicCryptoProcessor);
				String string = new String(transform);
				log.debug("decrypted result: {}", string);
				originSource.setBody(new String(transform));
				return originSource;
			}
			
		});
		
		
		Convertable<HttpResponseContext<JsonNode>, ResponseEntity<JsonNode>> convertable = new Convertable<HttpResponseContext<JsonNode>,ResponseEntity<JsonNode>>() {
			@Override
			public ResponseEntity<JsonNode> transform(ConfigurableContext ctx, HttpResponseContext<JsonNode> originSource)
					throws Exception {
				BodyBuilder bodyBuilder = ResponseEntity.ok();
				JsonNode node = originSource.getBody();
				originSource.getResponseHeaders().forEach((k,v)->{
					bodyBuilder.header(k,v);
				});
				
				ResponseEntity<JsonNode> entity = bodyBuilder.body(node);
			    return entity;
			}
		};
		
		interfaceHolder.setRequestConvertor(requestChain);
		interfaceHolder.setResponseConvertor(responseChain);
		interfaceHolder.setResponseFinaltransformer(convertable);
		interfaceHolder.setRequestMessageValidator(requestSchemaValidator);
		interfaceHolder.setResponseMessageValidator(responseSchemaValidator);
		interfaceHolder.setApiCaller(caller);
		interfaceHolder.setApiCallErrorTransfomer(caller);
		
		
	}
	
}