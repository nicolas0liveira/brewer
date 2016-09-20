package com.aftermidnight.brewer.storage;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public interface FotoStorage {
	
	public final String THUMBNAIL_PREFIX = "thumbnail.";

	public String salvar(MultipartFile... files);
	
	public byte[] recuperar(String nomeFoto);

	public byte[] recuperarThumbnail(String fotoCerveja);

	public void apagarFoto(String foto);

	public String getUrl(String nomeFoto);

	public String getUrlThumbnail(String fotoOuMock);
	
	
	default String renomearArquivo(String nomeOriginal){
		return UUID.randomUUID().toString() +"_"+ nomeOriginal;
	}
}