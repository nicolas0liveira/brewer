package com.aftermidnight.brewer.repository.listener;

import javax.persistence.PostLoad;

import com.aftermidnight.brewer.model.Cerveja;
import com.aftermidnight.brewer.storage.FotoStorage;

public class CervejaEntityListener {

//	@Autowired
//	private FotoStorage fotoStorage;
	
	@PostLoad
	public void postLoad(final Cerveja cerveja){
//		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this); //para processar as anotações (@Autowired) do spring, pois essa entidade é gerenciada pelo jpa
		//não funciona no spring boot
		
		cerveja.setUrlFoto(FotoStorage.URL + cerveja.getFotoOuMock());
		cerveja.setUrlThumbnailFoto(FotoStorage.URL + FotoStorage.THUMBNAIL_PREFIX + cerveja.getFotoOuMock());
		
	}
}
