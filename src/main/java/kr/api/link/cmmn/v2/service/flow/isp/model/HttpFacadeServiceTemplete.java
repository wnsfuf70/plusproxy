package kr.api.link.cmmn.v2.service.flow.isp.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.JsonNode;

import kr.api.link.cmmn.v2.configurable.model.HttpResponseContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HttpFacadeServiceTemplete extends FacadeServiceTemplete<RequestEntity<JsonNode>, RequestEntity<String>, ResponseEntity<String>, RestClientException, HttpResponseContext<JsonNode>> {
	
	public HttpFacadeServiceTemplete(HttpFacadeInterfaceHolder interfaceHolder) {
        super(interfaceHolder);
        this.interfaceHolder = interfaceHolder;
    }

	public HttpFacadeInterfaceHolder interfaceHolder;
	
	public HttpFacadeInterfaceHolder getInterfaceHolder() {
		return interfaceHolder;
	}

	public void setInterfaceHolder(HttpFacadeInterfaceHolder interfaceHolder) {
		this.interfaceHolder = interfaceHolder;
	}
	
}