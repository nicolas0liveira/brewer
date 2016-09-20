package com.aftermidnight.brewer.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.aftermidnight.brewer.mail.Mailer;

@Configuration
@ComponentScan( basePackageClasses = Mailer.class )
@PropertySource({ "classpath:env/mail-${ambiente:local}.properties" })//é possivel configurar em um arquivo local tb, e carrega-lo por meio dessa anotacao. lembrete: ${<variavel>:<valorDefault>}
@PropertySource( value = { "file:///${USERPROFILE}/brewer-mail.properties" }, ignoreResourceNotFound = true) //é possivel também carregar de um arquivo externo
@PropertySource( value = { "file:///${HOME}/brewer-mail.properties" }, ignoreResourceNotFound = true) //é possivel também carregar de um arquivo externo
public class MailConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(MailConfig.class);
	
	@Autowired
	private Environment env;

	@Bean
	public JavaMailSender mailSender(){
		//configurando para usar o sendgrid: https://app.sendgrid.com/
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.sendgrid.net");
		mailSender.setPort(587);
		mailSender.setUsername(env.getProperty("email.username"));
		mailSender.setPassword(env.getProperty("SENDGRID_PASSWORD"));
		
		logger.info(" >> Carregando configurações de email... mail.username: "+ env.getProperty("email.username"));
//		System.out.println(">>> mail.password: "+ env.getProperty("email.password"));
		
		
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", true);
		props.put("mail.smtp.starttls.enable", true);
		props.put("mail.debug", false);
		props.put("mail.smtp.connectiontimeout", 10000);
		props.put("mail.smtp.ssl.trust", "smtp.sendgrid.net");
		
		mailSender.setJavaMailProperties(props);
		
		
		
		
		return mailSender;
	}
}
