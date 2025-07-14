package kr.api.link.cmmn.v2.service.flow.isp;

@FunctionalInterface
public interface APICallable<I,O,E extends Exception> {
	
	// 4. 호출
	//String callApi(HttpMessageObject<JsonNode> originObject, Map<String, String> header, String targetReqMsg) throws TargetApiException;
	O call (I remoteSend) throws E;
	
}