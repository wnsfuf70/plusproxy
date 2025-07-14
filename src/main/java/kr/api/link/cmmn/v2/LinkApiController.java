package kr.api.link.cmmn.v2;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.bind.annotation.RequestMethod.TRACE;

import java.nio.ByteBuffer;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.api.link.cmmn.excp.InvalidRequestException;
import kr.api.link.cmmn.v2.app.GlobalPreference;
import kr.api.link.cmmn.v2.configurable.ConfigurableInvoke;
import kr.api.link.cmmn.v2.configurable.ConfigurableProcess;
import kr.api.link.cmmn.v2.configurable.model.HttpRequestContext;
import kr.api.link.cmmn.v2.configurable.model.HttpResponseContext;
import kr.api.link.cmmn.v2.configurable.model.ServiceDataRegistry;
import kr.api.link.cmmn.v2.configurable.model.ServiceDataRegistry.Entry;
import kr.api.link.cmmn.v2.service.CommonService;
import kr.api.link.cmmn.v2.service.component.convert.VelocityResponseConverter;
import kr.api.link.cmmn.v2.service.component.schema.JsonRequestSchemaValidator;
import kr.api.link.cmmn.v2.service.support.util.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class LinkApiController {
	
	@Autowired
	ConfigurableProcess configurableProcess;
	//path = "{*path}"
	@RequestMapping(
		  path = "/api/{*path}"
		, method = { GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE } 
		, produces = { MediaType.APPLICATION_JSON_VALUE }
		, consumes = { MediaType.APPLICATION_JSON_VALUE }
	)
	public @ResponseBody ResponseEntity<JsonNode> linkAPI (
		@PathVariable("path") String path,
		@RequestParam(required = false) Map<String,Object> queryStringMap,
		RequestEntity<JsonNode> request
	) throws Exception {
		
		/*
		log.debug(request.getMethod().name());
		log.debug("path : " + request.getUrl().getRawPath());
		log.debug("typeName : {}", request.getType().getTypeName());
		log.debug("queryStringMap : {}", queryStringMap);
		log.debug("queryString : {}", request.getUrl().getQuery());
		log.debug("queryStringRaw : {}", request.getUrl().getRawQuery());
		log.debug("httpHeader : {}", request.getHeaders());
		log.debug("request Json : {}", request.getBody());
		*/
		
		ConfigurableInvoke invoke = configurableProcess.invokeService(request.getUrl().getRawPath());
		CommonService serviceImplement = invoke.getServiceImplement();
		
		HttpRequestContext<JsonNode> httpRequestContext = HttpRequestContext.generate(invoke,request, queryStringMap);
		log.debug("{}", ToStringBuilder.reflectionToString(httpRequestContext),ToStringStyle.SHORT_PREFIX_STYLE);
		
		if(path.endsWith("mock")) {
			
			String serviceId = invoke.getServiceId();
			log.debug("service id mock : {}",serviceId);
			
			JsonRequestSchemaValidator valid = new JsonRequestSchemaValidator();
			boolean validate = valid.validate(httpRequestContext);
			if(!validate) throw new InvalidRequestException(request.getBody().toString());
			
			ServiceDataRegistry registry = GlobalPreference.getServiceDataResitryByServiceId(serviceId);
			Entry entry = registry.getResponseVm();
			
			Entry responseOrigin = registry.getResponseOrigin();
			ByteBuffer responseOriginBuffer = responseOrigin.getData();
			ResponseEntity<String> srcBuffer = getSrcBuffer(responseOriginBuffer,entry.getDataType());
			
			HttpResponseContext<String> httpResponseContext = HttpResponseContext.generate(invoke,srcBuffer);
			
			HttpResponseContext<String> transform  = converter.transform(httpResponseContext,httpResponseContext);
			ObjectMapper mapper = new ObjectMapper();
			
			JsonNode convertValue = mapper.convertValue(transform,JsonNode.class);
			
			ResponseEntity<JsonNode> mock = getSrcOnlySupportJsonBuffer(entry.getData());
			//ResponseEntity<JsonNode> mock = ResponseEntity.ok(convertValue);
			
			return mock;
		}
		
		return serviceImplement.service(httpRequestContext);
	}
	@Autowired
	VelocityResponseConverter converter;
	
	@RequestMapping(
			  path = "/desc/{serviceId}/{position}/{target}/{src}"
			, method = {GET} 
			, produces = { MediaType.ALL_VALUE }
			, consumes = { MediaType.ALL_VALUE }
	)
	public ResponseEntity<String> linkDesciption ( 
			@PathVariable(name = "serviceId") String serviceId, 
			@PathVariable(name = "position") String position,
			@PathVariable(name = "target") String target,
			@PathVariable(name = "src") String src) throws Exception {
		
		log.debug("service id : {}",serviceId);
		log.debug("position : {}",position);
		
		ServiceDataRegistry registry = GlobalPreference.getServiceDataResitryByServiceId(serviceId);
		
		Entry entry = null;
		if(position.equalsIgnoreCase("request")) {
			if(target.equalsIgnoreCase("origin")) {
				entry = registry.getRequestOrigin();
				if(src.equalsIgnoreCase("data")) {
					return getSrcBuffer(entry.getData(),entry.getDataType());
				}
				else if(src.equalsIgnoreCase("schema")) {
					return getSrcBuffer(entry.getSchema(),entry.getSchemaType());
				}
			}
			else if(target.equalsIgnoreCase("vm")) {
				entry = registry.getRequestVm();
				if(src.equalsIgnoreCase("data")) {
					return getSrcBuffer(entry.getData(),entry.getDataType());
				}
				else if(src.equalsIgnoreCase("schema")) {
					return getSrcBuffer(entry.getSchema(),entry.getSchemaType());
				}
			}
		}
		else if(position.equalsIgnoreCase("response")) {
			if(target.equalsIgnoreCase("origin")) {
				entry = registry.getResponseOrigin();
				if(src.equalsIgnoreCase("data")) {
					return getSrcBuffer(entry.getData(),entry.getSchemaType());
				}
				else if(src.equalsIgnoreCase("schema")) {
					return getSrcBuffer(entry.getSchema(),entry.getSchemaType());
				}
			}
			else if(target.equalsIgnoreCase("vm")) {
				entry = registry.getResponseVm();
				if(src.equalsIgnoreCase("data")) {
					return getSrcBuffer(entry.getData(),entry.getSchemaType());
				}
				else if(src.equalsIgnoreCase("schema")) {
					return getSrcBuffer(entry.getSchema(),entry.getSchemaType());
				}
			}
		}
		
		return ResponseEntity.notFound().build();
	}
	
	public static ResponseEntity<String> getSrcBuffer(ByteBuffer buffer , MediaType mediaType) {
		return ResponseEntity.ok().contentType(mediaType).body(new String(Utils.getByteBufferAsReadOnly(buffer)));
	}
	
	public static ResponseEntity<JsonNode> getSrcOnlySupportJsonBuffer(ByteBuffer buffer) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String string = new String(Utils.getByteBufferAsReadOnly(buffer));
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(mapper.readTree(string));
	}
	
}