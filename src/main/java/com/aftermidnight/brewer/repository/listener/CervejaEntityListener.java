package com.aftermidnight.brewer.repository.listener;

import javax.persistence.PostLoad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.aftermidnight.brewer.model.Cerveja;
import com.aftermidnight.brewer.storage.FotoStorage;

public class CervejaEntityListener {

	@Autowired
	private FotoStorage fotoStorage;
	
	@PostLoad
	public void postLoad(final Cerveja cerveja){
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this); //para processar as anotações (@Autowired) do spring, pois essa entidade é gerenciada pelo jpa
		
		cerveja.setUrlFoto(fotoStorage.getUrl(cerveja.getFotoOuMock()));
		cerveja.setUrlThumbnailFoto(fotoStorage.getUrlThumbnail(cerveja.getFotoOuMock()));
		
	}
}
