package kr.api.link.cmmn.v2.service.flow.isp.combine;

import kr.api.link.cmmn.v2.service.flow.isp.APICallable;
import kr.api.link.cmmn.v2.service.flow.isp.ErrorTransformable;

public interface AbstractAPICaller<I,O,E extends Exception> extends APICallable<I,O,E>, ErrorTransformable<I,O> {
	
	// 4. 호출
	//String callApi(HttpMessageObject<JsonNode> originObject, Map<String, String> header, String targetReqMsg) throws TargetApiException;
	public abstract O call (I remoteSend) throws E;
	
	public abstract O errorHandle(Exception exception, I param) throws Exception;
	
}