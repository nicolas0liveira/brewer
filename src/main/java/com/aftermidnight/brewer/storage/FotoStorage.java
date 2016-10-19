package com.aftermidnight.brewer.storage;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public interface FotoStorage {
	
	public final static String THUMBNAIL_PREFIX = "thumbnail.";
	public final static String URL = "http://localhost:8080/fotos/";

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