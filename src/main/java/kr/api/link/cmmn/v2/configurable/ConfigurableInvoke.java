package kr.api.link.cmmn.v2.configurable;

import kr.api.link.cmmn.security.core.context.CryptoContext;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig;
import kr.api.link.cmmn.v2.configurable.model.ServiceDataRegistry;
import kr.api.link.cmmn.v2.service.CommonService;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ConfigurableInvoke {

	String serviceId;
	
	ServiceConfig serviceConfig;
	
	CommonService serviceImplement;
	
	ServiceDataRegistry serviceDataRegistry;
	
	CryptoContext cryptoContext;
	
}