package kr.api.link.cmmn.v2.service.component.api;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.Resource;
import kr.api.link.cmmn.v2.service.support.combine.HttpAPICaller;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RestTempleteHttpCaller implements HttpAPICaller {
	
	@Resource(name = "getRestTemplate")
	RestTemplate restTemplate;
	
	@Override
	public ResponseEntity<String> call(RequestEntity<String> remoteSend) throws RestClientException {
		return restTemplate.exchange(remoteSend,new ParameterizedTypeReference<String>() {});
	}
	
	@Override
	public ResponseEntity<String> errorHandle(Exception exception, RequestEntity<String> param) throws Exception {
		throw exception;
	}
	
	/*
	public <T> ResponseEntity<T> exchange(
			String url,
            HttpMethod method,
            HttpEntity<?> requestEntity,
            ParameterizedTypeReference<T> responseType) {
			WebClient webClient = WebClient.builder().build();
			
			Mono<ResponseEntity<T>> responseMono = webClient.method(method)
			.uri(url)
			.headers(headers -> headers.addAll(requestEntity.getHeaders()))
			.body(requestEntity.getBody() != null ? WebClient.BodyInserters.fromValue(requestEntity.getBody()) : WebClient.BodyInserters.empty())
			.retrieve()
			.toEntity(responseType);
			return responseMono.block();
	}
	*/
}