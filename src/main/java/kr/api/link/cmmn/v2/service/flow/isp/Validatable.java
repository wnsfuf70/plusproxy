package kr.api.link.cmmn.v2.service.flow.isp;

@FunctionalInterface
public interface Validatable<P> {
	
	// 1. 요청 데이터 검증
	//boolean validateRequest(P requestJson, String serviceId) throws InvalidRequestException;
	
	// 7. 응답 데이터 검증
	//boolean validateResponse(P responseJson, String serviceId) throws InvalidResponseException;
	
	// 1. 요청 데이터 검증
	boolean validate(P validateParam) throws Exception;
		
}