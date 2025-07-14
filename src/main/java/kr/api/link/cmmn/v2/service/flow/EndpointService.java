package kr.api.link.cmmn.v2.service.flow;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;

import kr.api.link.cmmn.v2.configurable.model.HttpRequestContext;
import kr.api.link.cmmn.v2.service.CommonService;

public abstract class EndpointService implements CommonService {
	
	public abstract ResponseEntity<JsonNode> service(HttpRequestContext<JsonNode> requestContext) throws Exception;

}