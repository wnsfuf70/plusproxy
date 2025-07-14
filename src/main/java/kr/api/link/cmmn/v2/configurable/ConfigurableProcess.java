package kr.api.link.cmmn.v2.configurable;

import org.springframework.stereotype.Component;

import kr.api.link.cmmn.security.core.context.CryptoContextFactory;
import kr.api.link.cmmn.v2.app.GlobalPreference;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig;
import kr.api.link.cmmn.v2.configurable.model.ServiceDataRegistry;
import kr.api.link.cmmn.v2.service.CommonService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ConfigurableProcess {

	public ConfigurableInvoke invokeService(String path) throws Exception {
		
		String serviceId = GlobalPreference.getServiceIdByPath(path);

		log.debug("startService : {}", serviceId);
		
		CommonService service = GlobalPreference.getCommonServiceByServiceId(serviceId);
		
		ServiceConfig config = GlobalPreference.getServiceConfigByServiceId(serviceId);
		
		ServiceDataRegistry dataRegistry = GlobalPreference.getServiceDataResitryByServiceId(serviceId);
		
		if(service==null) {
			throw new IllegalStateException("[no binding] serviceId by mapping ServiceImpl... CommonService");
		}
		
		log.debug("commonService : {}", service.getClass().getCanonicalName());
		return ConfigurableInvoke.builder()
				.serviceId(serviceId)
				.serviceConfig(config)
				.cryptoContext(CryptoContextFactory.fromServiceConfig(config))
				.serviceDataRegistry(dataRegistry)
				.serviceImplement(service)
				.build();
		
	}
	
}