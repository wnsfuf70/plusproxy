package kr.api.link.cmmn.v2.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;

@Configuration
public class FilterConfiguration {
	
	@Bean
	public FilterRegistrationBean<Filter> filterBean() {
		FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<Filter>(new PathAcceptFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
		return registrationBean;
	}
	
}