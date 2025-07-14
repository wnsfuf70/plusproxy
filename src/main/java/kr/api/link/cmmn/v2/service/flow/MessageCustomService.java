package kr.api.link.cmmn.v2.service.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.annotation.Resource;
import kr.api.link.cmmn.v2.configurable.model.HttpRequestContext;
import kr.api.link.cmmn.v2.service.CommonService;
import kr.api.link.cmmn.v2.service.component.api.RestTempleteHttpCaller;
import kr.api.link.cmmn.v2.service.component.convert.type.ResponseEntityToVelocityContext;
import kr.api.link.cmmn.v2.service.component.schema.JsonRequestSchemaValidator;
import kr.api.link.cmmn.v2.service.component.schema.JsonResponseSchemaValidator;
import kr.api.link.cmmn.v2.service.flow.isp.combine.AbstractAPICaller;
import kr.api.link.cmmn.v2.service.flow.isp.model.HttpFacadeServiceTemplete;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MessageCustomService implements CommonService {

	@Resource(name="getHttpFacadeServiceTemplete")
	HttpFacadeServiceTemplete facade;
	
    private final ResponseEntityToVelocityContext responseEntityToVelocityContext;
	
	@Resource(type = RestTempleteHttpCaller.class)
	public AbstractAPICaller<RequestEntity<String>, ResponseEntity<String>, RestClientException> caller;
	
	@Autowired
	public JsonRequestSchemaValidator requestSchemaValidator;
	
	@Autowired
	public JsonResponseSchemaValidator responseSchemaValidator;

    MessageCustomService(ResponseEntityToVelocityContext responseEntityToVelocityContext) {
        this.responseEntityToVelocityContext = responseEntityToVelocityContext;
    }
	
	@Override
	public ResponseEntity<JsonNode> service(HttpRequestContext<JsonNode> httpRequestContext) throws Exception {

		log.debug("### MessageCustomService service");
		if(facade.getInterfaceHolder().getRequestConvertor()==null) {
			throw new IllegalStateException("MESSAGE_CUSTOM 요청 메세지 변환기가 등록되지 않았습니다.");
		}
		
		if(facade.getInterfaceHolder().getResponseConvertor()==null) {
			throw new IllegalStateException("MESSAGE_CUSTOM 응답 메세지 변환기가 등록되지 않았습니다.");
		}
		
		return facade.facadeProcess(httpRequestContext,httpRequestContext.getEntity());
	}

	
}