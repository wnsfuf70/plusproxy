package kr.api.link.cmmn.v2.filter;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.api.link.cmmn.v2.app.ApplicationInfo;
import kr.api.link.cmmn.v2.app.GlobalPreference;

@Component
public class PathAcceptFilter extends OncePerRequestFilter implements ApplicationContextAware {
	
	ApplicationContext ctx;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		
		String requestURI = request.getRequestURI();
		 
		if(!acceptMappingPath(requestURI)) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.setContentType(MediaType.TEXT_PLAIN.toString());
			response.setCharacterEncoding("utf-8");
			response.getWriter().write("404 NotFound Resource : "+ requestURI);
			return;
		}
		else {
			filterChain.doFilter(request, response);
		}
		
	}

	boolean acceptMappingPath(String requestURI) {
		
		if(requestURI.startsWith("/desc")) {
			return true;
		}
		
		if(requestURI.startsWith("/actuator")) {
			return true;
		}
		
		ApplicationInfo applicationInfo = GlobalPreference.getApplicationInfo();
		
		Map<String, String> pathAndServiceIdMapping = applicationInfo.getPathAndServiceMapping();
		
		return pathAndServiceIdMapping.containsKey(requestURI) ? true : false;
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
	}
	
}
