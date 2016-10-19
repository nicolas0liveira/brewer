package com.aftermidnight.brewer;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

import com.aftermidnight.brewer.config.JPAConfig;
import com.aftermidnight.brewer.config.MailConfig;
import com.aftermidnight.brewer.config.ScheduleConfig;
import com.aftermidnight.brewer.config.SecurityConfig;
import com.aftermidnight.brewer.config.ServiceConfig;
import com.aftermidnight.brewer.config.WebConfig;

@SpringBootApplication
@EnableAutoConfiguration
public class BrewerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrewerApplication.class, args);
	}
	
	@Bean
	public LocaleResolver localeResolver() {
		return new FixedLocaleResolver(new Locale("pt", "BR"));
	}
	
	
//	protected Class<?>[] getRootConfigClasses() {
//		return new Class<?>[] { JPAConfig.class, ServiceConfig.class, SecurityConfig.class, ScheduleConfig.class };
//	}
//
//	protected Class<?>[] getServletConfigClasses() {
//		return new Class<?>[] { WebConfig.class, MailConfig.class };
//	}
	
}