package kr.api.link.cmmn.v2.service.component.convert.type;

import java.io.StringReader;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.api.link.cmmn.v2.configurable.ConfigurableInvoke;
import kr.api.link.cmmn.v2.configurable.model.HttpResponseContext;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig.Convert;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig.Convert.Templete;
import kr.api.link.cmmn.v2.service.flow.isp.TypeConvertable;
import kr.api.link.cmmn.v2.service.support.util.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ResponseEntityToVelocityContext implements TypeConvertable<HttpResponseContext<String>,VelocityContext> {
	
	@Autowired
	ObjectMapper mapper;
	
	@Override
	public VelocityContext convertType(HttpResponseContext<String> remoteSource) throws Exception {

		log.debug("### transformResponseBody ");
		log.debug("remoteResponse : {}", remoteSource);
		
		ConfigurableInvoke invoke = remoteSource.getInvoke();
		ServiceConfig serviceConfig = invoke.getServiceConfig();
		Convert convert = serviceConfig.getConvert();
		
		Templete resOrigin = convert.getResOrigin();
		MediaType resOriginType = resOrigin.getMediaType();
		
		String body = remoteSource.getBody();
		
		Map<String,Object> bodyMap = null;
		
		if(MediaType.APPLICATION_XML.isCompatibleWith(resOriginType)) {
			bodyMap = Utils.Xml.parseXmlToMap(body.getBytes());
		}
		else if(MediaType.APPLICATION_JSON.isCompatibleWith(resOriginType)) {
			bodyMap = mapper.convertValue(new StringReader(body),new TypeReference<Map<String,Object>>() {});
		}
		
		VelocityContext context = new VelocityContext(bodyMap);
		log.debug("{}",ToStringBuilder.reflectionToString(context));
		context.put("header", remoteSource.getReadOnlyOriginHeader().toSingleValueMap());
		
		return context;
	}

}