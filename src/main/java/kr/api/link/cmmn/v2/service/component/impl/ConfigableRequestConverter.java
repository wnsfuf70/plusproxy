package kr.api.link.cmmn.v2.service.component.impl;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import kr.api.link.cmmn.v2.configurable.ConfigurableInvoke;
import kr.api.link.cmmn.v2.configurable.model.HttpRequestContext;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig.Backend;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig.Backend.Header;
import kr.api.link.cmmn.v2.service.flow.isp.combine.AbstractRequestConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigableRequestConverter extends AbstractRequestConverter {
	
	@Override
	public MediaType transformContentType(HttpRequestContext<String> originSource) throws Exception {
		ServiceConfig serviceConfig = originSource.getInvoke().getServiceConfig();
		MediaType mediaType = serviceConfig.getConvert().getReqVm().getMediaType();
		log.debug("{}",mediaType);
		return mediaType;
	}

	@Override
	public String transformRequestBody(HttpRequestContext<String> originSource) throws Exception {
		String transData = originSource.getBody().toString();
		return transData;
	}

	@Override
	public Map<String, String> transformRequestHeader(HttpRequestContext<String> originSource) throws Exception {
		
		ConfigurableInvoke invoke = originSource.getInvoke();
		
		ServiceConfig serviceConfig = invoke.getServiceConfig();
		Header header = serviceConfig.getBackend().getHeader();
		
		if(header!=null) {
			Map<String, String> staticProp = header.getStaticProp();
			if(staticProp !=null &&  staticProp.size()>0) {
				originSource.getHeaders().putAll(staticProp);
			}
		}
		
		return originSource.getHeaders();
	}

	@Override
	public URI transformURI(HttpRequestContext<String> originSource) throws Exception {
		
		ConfigurableInvoke invoke = originSource.getInvoke();
		
		Backend backend = invoke.getServiceConfig().getBackend();
		String address = backend.getAddress();
		
		return URI.create(address);
	}

	@Override
	public HttpMethod transformMethod(HttpRequestContext<String> originSource) throws Exception {

		ConfigurableInvoke invoke = originSource.getInvoke();
		
		Backend backend = invoke.getServiceConfig().getBackend();
		HttpMethod mehtod = backend.getMehtod();
		
		return mehtod;
	}

}