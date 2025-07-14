package kr.api.link.cmmn.v2.service.support.combine;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import kr.api.link.cmmn.v2.service.flow.isp.combine.AbstractAPICaller;

public interface HttpAPICaller extends AbstractAPICaller<RequestEntity<String>, ResponseEntity<String>, RestClientException> {
	
	abstract ResponseEntity<String> call(RequestEntity<String> remoteSend) throws RestClientException ;
	
	abstract ResponseEntity<String> errorHandle(Exception exception, RequestEntity<String> param) throws Exception ;
	
}