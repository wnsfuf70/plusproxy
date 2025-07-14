package kr.api.link.cmmn.v2.service.flow.isp.combine;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;

import kr.api.link.cmmn.v2.configurable.model.ConfigurableContext;
import kr.api.link.cmmn.v2.configurable.model.HttpRequestContext;
import kr.api.link.cmmn.v2.service.support.RequestConvertableChain;
import kr.api.link.cmmn.v2.service.support.util.Utils;

public abstract class AbstractRequestConverter implements RequestConvertableChain {
	
	@Override
	public HttpRequestContext<String> transform(ConfigurableContext ctx, HttpRequestContext<String> originSource) throws Exception {
		URI transformURI = transformURI(originSource);
		String transformRequestBody = transformRequestBody(originSource);
		Map<String, String> transformRequestHeader = transformRequestHeader(originSource);
		MediaType transformContentType = transformContentType(originSource);
		HttpMethod transformMethod = transformMethod(originSource);
		RequestEntity<String> requestEntity = Utils.Http.makeRequestEntity(transformURI, transformMethod, transformRequestHeader, transformRequestBody, transformContentType);
		return HttpRequestContext.generate(originSource.getInvoke(),requestEntity,originSource.getQueryStringMap());
	}
	
	// 2. 요청 데이터 변환 *
	public abstract String transformRequestBody(HttpRequestContext<String> originSource) throws Exception;
	
	// 2. 요청 헤더 변환 *
	public abstract Map<String,String> transformRequestHeader(HttpRequestContext<String> originSource) throws Exception;
	
	//본문.. 변환될 타입
	public abstract MediaType transformContentType(HttpRequestContext<String> originSource) throws Exception;
	
	//http.. 변환될 url
	public abstract URI transformURI(HttpRequestContext<String> originSource) throws Exception;
	
	//http.. 변환될 method
	public abstract HttpMethod transformMethod(HttpRequestContext<String> originSource) throws Exception;

}