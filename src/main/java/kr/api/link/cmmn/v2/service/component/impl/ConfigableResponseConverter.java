package kr.api.link.cmmn.v2.service.component.impl;

import java.util.Map;

import kr.api.link.cmmn.v2.configurable.model.HttpResponseContext;
import kr.api.link.cmmn.v2.service.flow.isp.combine.AbstractResponseConverter;

public class ConfigableResponseConverter extends AbstractResponseConverter{

	@Override
	public String transformReponseBody(HttpResponseContext<String> remoteSource) throws Exception {
		return remoteSource.getBody();
	}

	@Override
	public Map<String, String> transformResponseHeader(HttpResponseContext<String> remoteSource) throws Exception {
		return remoteSource.getResponseHeaders();
	}

}
