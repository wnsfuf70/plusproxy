package kr.api.link.cmmn.v2.configurable.init;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

import kr.api.link.cmmn.v2.app.ApplicationInfo;
import kr.api.link.cmmn.v2.configurable.ConfigurableInvoke;

@Component("LoadDatabaseTransistor")
public class LoadDatabaseTransistor implements LoadTransistor {

	@Override
	public void loadServiceConfig(ConfigurableEnvironment configurableEnvironment, ApplicationInfo applicationInfo)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadServiceDataResitry(ConfigurableInvoke configurableInvoke) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
