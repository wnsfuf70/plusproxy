package kr.api.link.cmmn.v2.configurable.init;

import org.springframework.core.env.ConfigurableEnvironment;

import kr.api.link.cmmn.v2.app.ApplicationInfo;
import kr.api.link.cmmn.v2.configurable.ConfigurableInvoke;

public interface LoadTransistor {

	void loadServiceConfig(ConfigurableEnvironment configurableEnvironment, ApplicationInfo applicationInfo) throws Exception ;
	
	void loadServiceDataResitry(ConfigurableInvoke configurableInvoke) throws Exception ;
	
}