package kr.api.link.cmmn.v2.service.flow.isp.combine;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;

import kr.api.link.cmmn.v2.configurable.model.ConfigurableContext;
import kr.api.link.cmmn.v2.configurable.model.HttpResponseContext;
import kr.api.link.cmmn.v2.service.support.ResponseConvertableChain;
import kr.api.link.cmmn.v2.service.support.util.Utils;

public abstract class AbstractResponseConverter implements ResponseConvertableChain {
	@Override
	public HttpResponseContext<String> transform(ConfigurableContext ctx, HttpResponseContext<String> remoteReponse) throws Exception {

		String responseJson = transformReponseBody(remoteReponse);
		
		BodyBuilder builder = ResponseEntity.ok();
		
		Map<String, String> headers = transformResponseHeader(remoteReponse);
		
		headers.forEach((k,v)->{
			if(!Utils.Http.isContentHeadersKey(k)) builder.header(k,v);
		});
		
		ResponseEntity<String> response = builder.body(responseJson);
		
		return HttpResponseContext.generate(remoteReponse.getInvoke(),response);
	}
	

	// 6. 응답 데이터 변환 *
	public abstract String transformReponseBody (HttpResponseContext<String> remoteResponse) throws Exception;
	// 6. 응답 헤더 변환 *
	public abstract Map<String,String> transformResponseHeader(HttpResponseContext<String> remoteResponse) throws Exception;

}