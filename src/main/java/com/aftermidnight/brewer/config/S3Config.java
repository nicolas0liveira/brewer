package com.aftermidnight.brewer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

@Configuration
@PropertySource(value = { "file:///${USERPROFILE}/brewer-s3.properties" }, ignoreResourceNotFound = true)
@PropertySource(value = { "file:///${HOME}/brewer-s3.properties" }, ignoreResourceNotFound = true)
//http://stackoverflow.com/questions/2680837/java-net-unknownhostexception-on-file-method
public class S3Config {
	
	private static final Logger logger = LoggerFactory.getLogger(S3Config.class);

	@Autowired
	private Environment env;
	
	@Bean
	public AmazonS3 amazonS3(){
		AWSCredentials credenciais = new BasicAWSCredentials(env.getProperty("AWS_ACCESS_KEY_ID"), env.getProperty("AWS_SECRET_ACCESS_KEY"));
		logger.info(">> Carregando configurações Amazon S3 ... AWS_ACCESS_KEY_ID "+env.getProperty("AWS_ACCESS_KEY_ID"));
		AmazonS3 amazonS3 = new AmazonS3Client(credenciais , new ClientConfiguration());
		Region regiao = Region.getRegion(Regions.US_EAST_1);
		amazonS3.setRegion(regiao);
		return amazonS3;
	}
}
