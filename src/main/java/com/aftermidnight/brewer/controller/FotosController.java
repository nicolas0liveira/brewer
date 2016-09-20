package com.aftermidnight.brewer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.aftermidnight.brewer.dto.FotoDTO;
import com.aftermidnight.brewer.storage.FotoStorage;
import com.aftermidnight.brewer.storage.FotoStorageRunnable;

@RestController
@RequestMapping("/fotos")
public class FotosController {

	@Autowired
	private FotoStorage fotoStorage;
	
	//a mesma coisa que: @RequestMapping(method=ResposeMethod.POST)
	@PostMapping
	public DeferredResult<FotoDTO> upload(@RequestParam("files[]") MultipartFile... files ){
		//Resultado postergado, para liberar a thread que recebeu a requisição. Por se taratar de um método 'pesado'
		DeferredResult<FotoDTO> resultado =  new DeferredResult<>();
		
		Thread thread = new Thread(new FotoStorageRunnable(resultado, fotoStorage, files));
		thread.start();
		
		return resultado;
	}
	
	@RequestMapping(path = "/{nome:.*}", method = { RequestMethod.DELETE })
	public void apagarFoto(@PathVariable("nome") String nomeFoto){
		fotoStorage.apagarFoto(nomeFoto);
	}
	
	
	@RequestMapping(path = "/{nome:.*}", method = { RequestMethod.GET })
	public byte[] recuperar(@PathVariable("nome") String nomeFoto){
		return fotoStorage.recuperar(nomeFoto);
	}
	
}
