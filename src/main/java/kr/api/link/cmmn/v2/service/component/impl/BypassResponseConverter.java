package kr.api.link.cmmn.v2.service.component.impl;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.api.link.cmmn.v2.configurable.model.HttpResponseContext;
import kr.api.link.cmmn.v2.service.flow.isp.combine.AbstractResponseConverter;

public class BypassResponseConverter extends AbstractResponseConverter {

	ObjectMapper mapper;
	
	public BypassResponseConverter(ObjectMapper mapper) {
		this.mapper = mapper;
	}
	
	@Override
	public String transformReponseBody(HttpResponseContext<String> remoteSource) throws Exception {
		return remoteSource.getBody();
	}
	
	@Override
	public Map<String, String> transformResponseHeader(HttpResponseContext<String> remoteSource) throws Exception {
		return remoteSource.getResponseHeaders();
	}

}
