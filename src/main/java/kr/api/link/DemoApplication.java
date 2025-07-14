package kr.api.link;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import kr.api.link.cmmn.v2.app.ApplicationBootEventSource;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DemoApplication {
	
	public static void main(String[] args) {
		
		SpringApplication app = new SpringApplication(DemoApplication.class);
		
		app.setWebApplicationType(WebApplicationType.SERVLET);
		
		ApplicationBootEventSource.applyEventListener(app);
		
		app.run(args);
		
	}
	
}