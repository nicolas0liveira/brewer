package com.aftermidnight.brewer.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.aftermidnight.brewer.service.CadastroCervejaService;
import com.aftermidnight.brewer.storage.FotoStorage;

@Configuration
@ComponentScan(basePackageClasses = { CadastroCervejaService.class , FotoStorage.class })
public class ServiceConfig {

	
}
