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
import kr.api.link.cmmn.v2.configurable.ConfigurableInvoke;
import kr.api.link.cmmn.v2.configurable.model.ConfigurableContext;
import kr.api.link.cmmn.v2.configurable.model.HttpRequestContext;
import kr.api.link.cmmn.v2.configurable.model.HttpResponseContext;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig.Backend;
import kr.api.link.cmmn.v2.service.CommonService;
import kr.api.link.cmmn.v2.service.component.api.RestTempleteHttpCaller;
import kr.api.link.cmmn.v2.service.component.convert.VelocityRequestConverter;
import kr.api.link.cmmn.v2.service.component.convert.VelocityResponseConverter;
import kr.api.link.cmmn.v2.service.component.schema.JsonRequestSchemaValidator;
import kr.api.link.cmmn.v2.service.component.schema.JsonResponseSchemaValidator;
import kr.api.link.cmmn.v2.service.flow.isp.Convertable;
import kr.api.link.cmmn.v2.service.flow.isp.combine.LockingConverterChain;
import kr.api.link.cmmn.v2.service.flow.isp.model.HttpFacadeInterfaceHolder;
import kr.api.link.cmmn.v2.service.flow.isp.model.HttpFacadeServiceTemplete;
import kr.api.link.cmmn.v2.service.support.RequestConvertableChain;
import kr.api.link.cmmn.v2.service.support.ResponseConvertableChain;
import kr.api.link.cmmn.v2.service.support.util.Utils;

@Component
public class ConfigurableService implements CommonService ,InitializingBean {
	
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
	
	@Resource(type = VelocityRequestConverter.class)
	VelocityRequestConverter velocityRequestConverter;
	
	@Resource(type = VelocityResponseConverter.class)
	VelocityResponseConverter velocityResponseConverter;
	
	@Override
	public ResponseEntity<JsonNode> service(HttpRequestContext<JsonNode> requestContext) throws Exception {
		return facade.facadeProcess(requestContext,requestContext.getEntity());
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
					return velocityRequestConverter.transform(ctx, originSource);
				}
				
		}).complete();
		
		
		LockingConverterChain<ResponseEntity<String>, HttpResponseContext<JsonNode>> responseChain = new LockingConverterChain<ResponseEntity<String>, HttpResponseContext<JsonNode>>();
		
		responseChain.setDefaultStartAndEnd(new Convertable<ResponseEntity<String>,HttpResponseContext<String>>() {
			
			@Override
			public HttpResponseContext<String> transform(ConfigurableContext ctx, ResponseEntity<String> originSource) throws Exception {
				return HttpResponseContext.generate(ctx.getInvoke(), originSource);
			}
			
		},new Convertable<HttpResponseContext<String>,HttpResponseContext<JsonNode>>() {
			
			@Override
			public HttpResponseContext<JsonNode> transform(ConfigurableContext ctx,
				HttpResponseContext<String> originSource) throws Exception {
				BodyBuilder bodyBuilder = ResponseEntity.ok();
				JsonNode node = objectMapper.readTree(originSource.getBody());
				ResponseEntity<JsonNode> body = bodyBuilder.headers(originSource.getReadOnlyOriginHeader()).body(node);
			    return HttpResponseContext.generate(ctx.getInvoke(), body);
			}
			
		});
		
		ResponseConvertableChain responseConvertableChain = new ResponseConvertableChain() {
			@Override
			public HttpResponseContext<String> transform(ConfigurableContext ctx, HttpResponseContext<String> originSource)
					throws Exception {
				return velocityResponseConverter.transform(ctx, originSource);
			}
		};
		
		responseChain.setNextConverter(responseConvertableChain).complete();
		
		
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