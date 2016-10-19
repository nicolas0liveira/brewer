package com.aftermidnight.brewer.storage.local;

import static java.nio.file.FileSystems.getDefault;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.aftermidnight.brewer.storage.FotoStorage;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

@Profile("localfotostorage")
@Component
public class FotoStorageLocal implements FotoStorage {
	
	private static final Logger logger = LoggerFactory.getLogger(FotoStorageLocal.class);
	
	private Path local;
	
	
	public FotoStorageLocal() {
		this( getDefault().getPath(StringUtils.isEmpty(System.getenv("HOME"))/*linux*/ ? System.getenv("USERPROFILE")/*win*/ : System.getenv("HOME"), "brewerfotos" ) );
		logger.info(">>>> Carregando FotoStorageLocal ...");
		criarPastas();
	}
	
	public FotoStorageLocal(Path localPath) {
		this.local = localPath;
		criarPastas();
	}

	
	@Override
	public String salvar(MultipartFile... files){
		System.out.println(">>> salvando foto ...");
		String novoNome = null;
		if( files != null && files.length > 0){
			MultipartFile arquivo  = files[0];
			novoNome = renomearArquivo(arquivo.getOriginalFilename());
			try {
				arquivo.transferTo( new File(this.local.toAbsolutePath().toString() + getDefault().getSeparator() + novoNome) );
			} catch (IllegalStateException | IOException e) {
				throw new RuntimeException("Erro ao salvar a foto", e);
			}
		}
		
		try {
			Thumbnails.of(this.local.resolve(novoNome).toString()).size(40, 68).toFiles(Rename.PREFIX_DOT_THUMBNAIL);
		} catch (IOException e) {
			throw new RuntimeException("Erro ao gerar thumbnail", e);
		}
		
		return novoNome;
	}

	@Override
	public byte[] recuperar(String nomeFoto){
		try {
			return Files.readAllBytes(this.local.resolve(nomeFoto));
		} catch (IOException e) {
			throw new RuntimeException("Erro lendo a foto", e);
		}
	}
	
	@Override
	public byte[] recuperarThumbnail(String fotoCerveja) {
		return recuperar(THUMBNAIL_PREFIX + fotoCerveja);
	}

	@Override
	public void apagarFoto(String nomeFoto) {
		try {
			Files.deleteIfExists(this.local.resolve(nomeFoto));
			Files.deleteIfExists(this.local.resolve(THUMBNAIL_PREFIX + nomeFoto));
		} catch (Exception e) {
			logger.warn( String.format("Erro ao apagar foto %s.", this.local.resolve(nomeFoto)), e);
		}
	}
	
	
	
	private void criarPastas(){
		try {
			Files.createDirectories(this.local);

			if(logger.isDebugEnabled()){
				logger.debug("Pastas para salvar fotos criadas!");
				logger.debug("Pasta Default: " + this.local.toAbsolutePath());
			}
		} catch (IOException e) {
			throw new RuntimeException("Erro criando pasta para salvar foto", e);
		}
		
	}
	

	@Override
	public String getUrl(String nomeFoto) {
		return "http://127.0.0.1:8080/brewer/fotos/"+nomeFoto;
	}

	@Override
	public String getUrlThumbnail(String fotoOuMock) {
		return getUrl(THUMBNAIL_PREFIX + fotoOuMock);
	}	

//	public static void main(String[] args) {
//		System.out.println(StringUtils.isEmpty(System.getenv("HOME")) ? System.getenv("USERPROFILE") : System.getenv("HOME"));
//	}
}
