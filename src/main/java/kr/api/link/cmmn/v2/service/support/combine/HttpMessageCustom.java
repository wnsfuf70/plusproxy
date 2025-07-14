package kr.api.link.cmmn.v2.service.support.combine;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;

import kr.api.link.cmmn.v2.configurable.model.ConfigurableContext;
import kr.api.link.cmmn.v2.configurable.model.HttpRequestContext;
import kr.api.link.cmmn.v2.configurable.model.HttpResponseContext;
import kr.api.link.cmmn.v2.service.support.http.HttpRequestConverter;
import kr.api.link.cmmn.v2.service.support.http.HttpResponseConverter;

public abstract class HttpMessageCustom {
	
	public HttpMessageCustom(HttpRequestConverter httpRequestConverter,HttpResponseConverter httpResponseConverter) {
		this.httpRequestConverter = httpRequestConverter;
		this.httpResponseConverter = httpResponseConverter;
	}
	
	private HttpRequestConverter httpRequestConverter;
	private HttpResponseConverter httpResponseConverter;
	
	public abstract RequestEntity<String> transformRequest(ConfigurableContext ctx, HttpRequestContext<JsonNode> originSource) throws Exception;
	public abstract HttpResponseContext<JsonNode> transformResponse(ConfigurableContext ctx, ResponseEntity<String> originSource) throws Exception;
	
	public HttpRequestConverter getDefaultRequestConverter() {
		return httpRequestConverter;
	}
	
	public HttpResponseConverter getDefaultResponseConverter() {
		return httpResponseConverter;
	}
	
}
