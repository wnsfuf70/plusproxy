package kr.api.link.cmmn.v2.service.flow.isp.model;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;

import kr.api.link.cmmn.excp.UndefinedServerException;
import kr.api.link.cmmn.v2.configurable.model.HttpRequestContext;
import kr.api.link.cmmn.v2.service.flow.isp.APICallable;
import kr.api.link.cmmn.v2.service.flow.isp.Convertable;
import kr.api.link.cmmn.v2.service.flow.isp.ErrorTransformable;
import kr.api.link.cmmn.v2.service.flow.isp.Validatable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class FacadeServiceTemplete<I,O,R,E extends Exception,FV> {
	
	protected FacadeInterfaceHolder<I,O,R,E,FV> interfaceHolder;

    public FacadeServiceTemplete(FacadeInterfaceHolder<I,O,R,E,FV> interfaceHolder) {
        this.interfaceHolder = interfaceHolder;
    }
	
	public ResponseEntity<JsonNode> facadeProcess (HttpRequestContext<JsonNode> context , I request) throws Exception {

		log.debug("{}",interfaceHolder);
		
		//Validatable<JsonNode> businessValidator = interfaceHolder.getBusinessValidator();
		
		Validatable<HttpRequestContext<JsonNode>> requestMessageValidator = interfaceHolder.getRequestMessageValidator();

		Convertable<I, O> requestConvertor = interfaceHolder.getRequestConvertor();

		APICallable<O, R, E> apiCaller = interfaceHolder.getApiCaller();

		ErrorTransformable<O, R> apiCallErrorTransfomer = interfaceHolder.getApiCallErrorTransfomer();

		Convertable<R, FV> responseConvertor = interfaceHolder.getResponseConvertor();
		
		Validatable<FV> responseMessageValidator = interfaceHolder.getResponseMessageValidator();

		Convertable<FV, ResponseEntity<JsonNode>> responseFinaltransformer = interfaceHolder.getResponseFinaltransformer();

		if (requestMessageValidator != null) {
			try {
				requestMessageValidator.validate(context);
			}
			catch(Exception e) {
				throw e;
			}
		}

		O transformRequest = null;
		if (requestConvertor != null) {
			log.debug("[OK] requestConvertor ready");
			transformRequest = requestConvertor.transform(context,request);
		}
		else {
			log.debug("[OK] requestConvertor null");
		}

		R callResponse = null;
		
		if (apiCaller != null) {
			try {
				//암호화
				log.debug("[OK] call ready");
				callResponse = apiCaller.call(transformRequest);
				log.info("[OK] call onComplete");
				//복호화
			} 
			catch (Exception e) {
				try {
					if (apiCallErrorTransfomer == null) {
						throw e;
					}
					log.debug("[OK] call back errorHandle");
					callResponse = apiCallErrorTransfomer.errorHandle(e, transformRequest);
				} 
				catch (Exception e1) {
					log.error("[err] call problem...");
					throw e1;
				}
			}
		} 
		else {
			if (apiCallErrorTransfomer != null) {
				callResponse = apiCallErrorTransfomer.errorHandle(new UndefinedServerException("No Implements... api caller..."), transformRequest);
			}
		}
		
		FV validParam = null;
		try {
			if (responseConvertor != null) {
				validParam = responseConvertor.transform(context,callResponse);
			}
		}
		catch(Exception e) {
			throw e;
		}
		
		if (responseMessageValidator != null) {
			try {
				responseMessageValidator.validate(validParam);
			}
			catch(Exception e) {
				throw e;
			}
		}

		ResponseEntity<JsonNode> responseResult = null;
		if(responseFinaltransformer!=null) {
			try {
				responseResult = responseFinaltransformer.transform(context,validParam);
			}
			catch(Exception e) {
				throw e;
			}
		}
		
		return responseResult;
	}
	
}