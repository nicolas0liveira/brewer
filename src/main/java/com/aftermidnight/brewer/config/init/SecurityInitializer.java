package com.aftermidnight.brewer.config.init;

import java.util.EnumSet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.SessionTrackingMode;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;

public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {

	@Override
	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
//		servletContext.getSessionCookieConfig().setMaxAge(10 *60); //duração maxima da sessão
		
		servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE)); //para nao aparecer o JSESSIONID na url
		
		FilterRegistration.Dynamic characterEncodingFilter = servletContext.addFilter("encodingFilter",	new CharacterEncodingFilter());
		characterEncodingFilter.setInitParameter("encoding", "UTF-8");
		characterEncodingFilter.setInitParameter("forceEncoding", "true");
		characterEncodingFilter.addMappingForUrlPatterns(null, false, "/*");
	}
}
