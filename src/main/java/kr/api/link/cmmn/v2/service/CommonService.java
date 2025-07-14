
package kr.api.link.cmmn.v2.service;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;

import kr.api.link.cmmn.v2.configurable.model.HttpRequestContext;
import kr.api.link.cmmn.v2.service.define.ServiceDefinition;

public interface CommonService extends ServiceDefinition<JsonNode,JsonNode> {
	
	@Override
	abstract ResponseEntity<JsonNode> service(HttpRequestContext<JsonNode> requestContext) throws Exception ;
	
}