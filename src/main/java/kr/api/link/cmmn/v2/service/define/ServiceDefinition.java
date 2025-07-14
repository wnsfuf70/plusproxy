package kr.api.link.cmmn.v2.service.define;

import org.springframework.http.ResponseEntity;

import kr.api.link.cmmn.v2.configurable.model.HttpRequestContext;

public interface ServiceDefinition<I,O> {
	
	ResponseEntity<O> service(HttpRequestContext<I> requestContext) throws Exception;
	
}