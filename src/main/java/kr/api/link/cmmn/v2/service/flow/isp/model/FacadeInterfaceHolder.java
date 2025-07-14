package kr.api.link.cmmn.v2.service.flow.isp.model;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;

import kr.api.link.cmmn.v2.configurable.model.HttpRequestContext;
import kr.api.link.cmmn.v2.service.flow.isp.APICallable;
import kr.api.link.cmmn.v2.service.flow.isp.Convertable;
import kr.api.link.cmmn.v2.service.flow.isp.ErrorTransformable;
import kr.api.link.cmmn.v2.service.flow.isp.Validatable;
import lombok.Data;

@Data
public abstract class FacadeInterfaceHolder <I,O,R,E extends Exception , FV> {
	
	public Validatable<JsonNode> businessValidator;

	public Validatable<HttpRequestContext<JsonNode>> requestMessageValidator;
	
	public Convertable<I,O> requestConvertor;
	
	public APICallable<O,R,E> apiCaller;
	
	public ErrorTransformable<O,R> apiCallErrorTransfomer;

	public Convertable<R, FV> responseConvertor;
	
	public Validatable<FV> responseMessageValidator;

	public Convertable<FV, ResponseEntity<JsonNode>> responseFinaltransformer;
	
}