package kr.api.link.cmmn.v2.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import kr.api.link.cmmn.v2.configurable.init.InterfaceApiLoader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ApplicationBootEventSource {
	
	public static void applyEventListener(SpringApplication application) {
		if(application!=null) {
			application.addListeners(applicationEnvironmentPreparedEvent,applicationStartedEvent);
		}
	}
	
	private static final ApplicationListener<ApplicationEnvironmentPreparedEvent> applicationEnvironmentPreparedEvent = new ApplicationListener<ApplicationEnvironmentPreparedEvent>() {
		
		@Override
		public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
			
			ConfigurableEnvironment environment = event.getEnvironment();
			log.info("{}",environment);
			String applicationName = environment.getProperty("spring.application.name");
			log.info("applicationName : {}",applicationName);
			
			if(applicationName==null) {
				throw new IllegalStateException("spring.application.name set... property");
			}
			
			ApplicationInfo appInfo = GlobalPreference.getApplicationInfo();
			appInfo.setApplicationName(applicationName);
		}
		
	};
	
	private static final ApplicationListener<ApplicationStartedEvent> applicationStartedEvent = new ApplicationListener<ApplicationStartedEvent>() {
		
		@Override
		public void onApplicationEvent(ApplicationStartedEvent event) {
			ConfigurableApplicationContext applicationContext = event.getApplicationContext();
			InterfaceApiLoader loader = event.getApplicationContext().getBean(InterfaceApiLoader.class);
			loader.load(applicationContext,applicationContext.getEnvironment());
		}
		
	};
	
}
