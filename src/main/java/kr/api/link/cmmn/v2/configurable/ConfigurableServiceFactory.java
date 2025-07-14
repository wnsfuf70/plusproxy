package kr.api.link.cmmn.v2.configurable;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

import kr.api.link.cmmn.v2.configurable.model.ServiceConfig;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig.Logic;
import kr.api.link.cmmn.v2.configurable.model.ServiceRunType;
import kr.api.link.cmmn.v2.service.CommonService;
import kr.api.link.cmmn.v2.service.flow.BasicByPassService;
import kr.api.link.cmmn.v2.service.flow.ConfigurableService;
import kr.api.link.cmmn.v2.service.flow.MessageCustomService;
import kr.api.link.cmmn.v2.service.flow.isp.model.HttpFacadeInterfaceHolder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ConfigurableServiceFactory implements BeanFactoryAware {
	
	private BeanFactory beanFactory;
	
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
	
	public ConfigurableInvoke createConfigurableInvoke(String serviceId, ServiceConfig serviceConfig) {
		
		ConfigurableInvoke invoke = ConfigurableInvoke.builder()
		.serviceId(serviceId)
		.serviceConfig(serviceConfig)
		.serviceImplement(getInvokeCommonService(beanFactory,serviceId,serviceConfig))
		.build();
		
		return invoke; 
	}
	
	private CommonService getInvokeCommonService(BeanFactory beanFactory , String serviceId , ServiceConfig serviceConfig) throws BeansException {
		
		String componentName = null;
		
		Logic logic = serviceConfig.getLogic();
		
		ServiceRunType serviceRunType = logic.getServiceRunType();
		
		if(serviceRunType.compareTo(ServiceRunType.ENDPOINT)==0) {
			List<String> componentNames = logic.getComponentEntry().getName();
			if(componentNames!=null && componentNames.size()>0) {
				componentName = componentNames.get(0);
				return beanFactory.getBean(componentName,CommonService.class);
			}
			else {
				return beanFactory.getBean(serviceId+logic.getVersion()+"Service",CommonService.class);
			}
		}
		else if(serviceRunType.compareTo(ServiceRunType.HTTP_MESSAGE_CUSTOM)==0) {
			//throw new UnsupportedOperationException("현재 지원하지 않는 기능 : MESSAGE_CUSTOM ...");
			MessageCustomService bean = beanFactory.getBean(MessageCustomService.class);
			HttpFacadeInterfaceHolder interfaceHolder = new HttpFacadeInterfaceHolder();
			
			log.debug("### ### hashCode : {}",bean.getClass().hashCode());
			/*
			List<String> componentNames = logic.getComponentEntry().getName();
			if(componentNames!=null && componentNames.size()>0) {
				for(String name : componentNames) {
					if(beanFactory.containsBean(name)) {
						Object obj = beanFactory.getBean(name);
						
						if(obj instanceof HttpAPICaller) {
							HttpAPICaller call = (HttpAPICaller)obj;
							interfaceHolder.setApiCaller(call);
							interfaceHolder.setApiCallErrorTransfomer(call);
						}
						else {
							if(obj instanceof HttpAPICall) {
								HttpAPICall call = (HttpAPICall)obj;
								interfaceHolder.setApiCaller(call);
							}
							if(obj instanceof HttpErrorTransformer) {
								HttpErrorTransformer error = (HttpErrorTransformer)obj;
								interfaceHolder.setApiCallErrorTransfomer(error);
							}
						}
						
						if(obj instanceof HttpMessageCustom) {
							HttpMessageCustom combineFuntion = (HttpMessageCustom)obj;
							interfaceHolder.setRequestConvertor(combineFuntion.getDefaultRequestConverter());
							interfaceHolder.setResponseConvertor(combineFuntion.getDefaultResponseConverter());
						}
						else {
							if(obj instanceof HttpRequestConverter) {
								HttpRequestConverter reqConverter = (HttpRequestConverter)obj;
								interfaceHolder.setRequestConvertor(reqConverter);
							}
							if(obj instanceof HttpResponseConverter) {
								HttpResponseConverter resConverter = (HttpResponseConverter)obj;
								interfaceHolder.setResponseConvertor(resConverter);
							}
						}
						
					}
				}
			}
			*/
			
			return bean;
		}
		else {
			CommonService facadeCommonService = getDefaultConfigableCommonService(beanFactory,serviceConfig);
			return facadeCommonService;
		}
		
	}
	
	public static CommonService getDefaultConfigableCommonService (BeanFactory beanFactory , ServiceConfig serviceConfig) 
			throws BeansException {
		
		Logic logic = serviceConfig.getLogic();

		ServiceRunType logicType = logic.getServiceRunType();
		
		CommonService service = null;
		
		if(logicType.compareTo(ServiceRunType.BYPASS_PROXY)==0) {
			service = beanFactory.getBean(BasicByPassService.class);
		}
		else if(logicType.compareTo(ServiceRunType.HTTP_ALL_CONFIG)==0) {
			service = beanFactory.getBean(ConfigurableService.class);
		}
		
		return service;
		
	}

	/*
	FacadeInterfaceHolder<String, String, Exception> interfaceHolder = facadeCommonService.getInterfaceHolder();
	// 설정에 존재하는 인터페이스 주입.
	
	ComponentEntry componentEntry = logic.getComponentEntry();
	CallerTypeHolder apicaller = componentEntry.getApicaller();
	MessageTypeHolder convertor = componentEntry.getConvertor();
	MessageTypeHolder validator = componentEntry.getValidator();
	
	String caller = apicaller.getCall();
	if(!isEmpty(caller) && beanFactory.containsBean(caller)) {
		Object callerImpl = beanFactory.getBean(caller);
		if(callerImpl instanceof DefaultAPICall) {
			interfaceHolder.setApiCallable((DefaultAPICall)callerImpl);
		}
	}
	
	String errorTrans = apicaller.getError();
	if(!isEmpty(errorTrans) && beanFactory.containsBean(errorTrans)) {
		Object errorTransImpl = beanFactory.getBean(errorTrans);
		if(errorTransImpl instanceof DefaultErrorTransformer) {
			interfaceHolder.setApiCallErrorTransformable(((DefaultErrorTransformer)errorTransImpl));
		}
	}
	
	String reqConvertor = convertor.getReq();
	if(!isEmpty(reqConvertor) && beanFactory.containsBean(reqConvertor)) {
		Object reqConvertorImpl = beanFactory.getBean(reqConvertor);
		if(reqConvertorImpl instanceof DefaultRequestConvertor) {
			interfaceHolder.setRequestConvertable((DefaultRequestConvertor)reqConvertorImpl);
		}
	}
	
	String resConvertor = convertor.getRes();
	if(!isEmpty(resConvertor) && beanFactory.containsBean(resConvertor)) {
		Object resConvertorImpl = beanFactory.getBean(resConvertor);
		if(resConvertorImpl instanceof DefaultResponseConvertor) {
			interfaceHolder.setResponseConvertable((DefaultResponseConvertor)resConvertorImpl);
		}
	}
	
	String reqValidator = validator.getReq();
	if(!isEmpty(reqValidator) && beanFactory.containsBean(reqValidator)) {
		Object reqValidatorImpl = beanFactory.getBean(reqValidator);
		if(reqValidatorImpl instanceof DefaultValidator) {
			interfaceHolder.setRequestMessageValidator((DefaultValidator)reqValidatorImpl);
		}
	}
	
	String resValidator = validator.getRes();
	if(!isEmpty(resValidator) && beanFactory.containsBean(resValidator)) {
		Object resValidatorImpl = beanFactory.getBean(resValidator);
		if(resValidatorImpl instanceof DefaultValidator) {
			interfaceHolder.setResponseMessageValidator((DefaultValidator)resValidatorImpl);
		}
	}
	*/
	
}