package kr.api.link.cmmn.v2.service.support;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import kr.api.link.cmmn.v2.service.flow.isp.APICallable;

public interface DefaultAPICall extends APICallable<RequestEntity<String>, ResponseEntity<String>, RestClientException> {
	
	abstract ResponseEntity<String> call(RequestEntity<String> remoteSend) throws RestClientException ;
	
}