package kr.api.link.cmmn.v2.service.component.convert.type;

import java.io.StringReader;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.api.link.cmmn.v2.configurable.model.HttpRequestContext;
import kr.api.link.cmmn.v2.service.flow.isp.TypeConvertable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RequestContextToVelocityContext implements TypeConvertable<HttpRequestContext<String>,VelocityContext> {
	
	@Autowired
	ObjectMapper mapper;
	
	@Override
	public VelocityContext convertType(HttpRequestContext<String> originSource) throws Exception {
		
		log.debug("### transformRequestBody : ");
		String body = originSource.getBody().toString();
		log.debug("{}",body); //실제정보 JSON
		
		Map<String, Object> bodyMap = mapper.readValue(new StringReader(body),new TypeReference<Map<String,Object>>() {});
		VelocityContext context = new VelocityContext(bodyMap);
		context.put("header",originSource.getHeaders());
	
		return context;
	}

}