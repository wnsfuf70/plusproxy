package kr.api.link.cmmn.v2.configurable.init;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import kr.api.link.cmmn.v2.app.ApplicationInfo;
import kr.api.link.cmmn.v2.app.GlobalPreference;
import kr.api.link.cmmn.v2.configurable.ConfigurableInvoke;
import kr.api.link.cmmn.v2.configurable.ConfigurableServiceFactory;
import kr.api.link.cmmn.v2.configurable.model.ServiceConfig;
import kr.api.link.cmmn.v2.configurable.model.ServiceDataRegistry;
import kr.api.link.cmmn.v2.service.CommonService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InterfaceApiLoader {

	@Autowired
	ConfigurableServiceFactory serviceFactory;
	
	@Resource(name = "LoadTransistor")
	LoadTransistor loader;
	
	public void load(ConfigurableApplicationContext ctx, ConfigurableEnvironment configurableEnvironment) {
		
		ApplicationInfo applicationInfo = GlobalPreference.getApplicationInfo();
		
		try {
			loader.loadServiceConfig(configurableEnvironment,applicationInfo);
		} 
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
		
		Map<String, ServiceConfig> serviceConfigMap = applicationInfo.getServiceConfigMap();
		Map<String, ? super CommonService> serviceImplementsMap = applicationInfo.getServiceImplementsMap();
		Map<String, ServiceDataRegistry> serviceDataResitryMap = applicationInfo.getServiceDataResitryMap();
		
		serviceConfigMap.forEach((serviceId , serviceConfig)-> {
			ConfigurableInvoke configurableInvoke = serviceFactory.createConfigurableInvoke(serviceId, serviceConfig);
			
			try {
				loader.loadServiceDataResitry(configurableInvoke);
				log.debug("{}",configurableInvoke.getServiceDataRegistry());
				ServiceDataRegistry serviceDataResitry = configurableInvoke.getServiceDataRegistry();
				serviceDataResitryMap.put(serviceId,serviceDataResitry);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			CommonService commonService = configurableInvoke.getServiceImplement();
			serviceImplementsMap.put(serviceId,commonService);
		});
		
	}
	
}