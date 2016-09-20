package com.aftermidnight.brewer.storage;

import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.aftermidnight.brewer.dto.FotoDTO;

public class FotoStorageRunnable implements Runnable {

	private MultipartFile[] files;
	private DeferredResult<FotoDTO> resultado;
	private FotoStorage fotoStorage;
	
	
	public FotoStorageRunnable(DeferredResult<FotoDTO> resultado, FotoStorage fotoStorage, MultipartFile... files) {
		this.files = files;
		this.resultado = resultado;
		this.fotoStorage = fotoStorage;
	
	}
	
	
	@Override
	public void run() {
		String nomeFoto = fotoStorage.salvar(files);
		String contentType = files[0].getContentType();
		
		resultado.setResult(new FotoDTO(nomeFoto,contentType, fotoStorage.getUrl(nomeFoto)));
	}

}
