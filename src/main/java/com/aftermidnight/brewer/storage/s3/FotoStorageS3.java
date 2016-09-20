package com.aftermidnight.brewer.storage.s3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.aftermidnight.brewer.storage.FotoStorage;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;

import net.coobird.thumbnailator.Thumbnails;

@Profile("prod")
@Component
public class FotoStorageS3 implements FotoStorage {
	
	private static final Logger logger = LoggerFactory.getLogger(FotoStorageS3.class);
	
	private static final String BUCKET = "aftermidnight-brewer";
	private static final String URLS3 = "https://s3-sa-east-1.amazonaws.com";
	private static final String URLS3_BUCKET = URLS3 + "/" + BUCKET;

	@Autowired
	private AmazonS3 amazonS3;
	
	public FotoStorageS3() {
		logger.info(">> Carregando FotoStorageS3 ...");
	}
	
	@Override
	public String salvar(MultipartFile... files) {
		String novoNome = null;
		
		if(files!= null && files.length > 0){
			MultipartFile arquivo = files[0];
			novoNome = renomearArquivo(arquivo.getOriginalFilename());
			
			logger.debug(String.format(">>> Renomeando arquivo de %s para %s ...", arquivo.getOriginalFilename(), novoNome));

			try {
				AccessControlList acl = new AccessControlList();
				acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
				
				enviarFoto(novoNome, arquivo, acl);
				enviarThumbnail(novoNome, arquivo, acl);
				
				logger.debug(String.format(">>> Arquivo enviado para S3: %s", URLS3_BUCKET + "/" + novoNome));
			} catch (IOException e) {
				throw new RuntimeException("Erro salvando arquivo S3", e);
			}
		}
		
		return novoNome;
	}
	

	@Override
	public byte[] recuperar(String nomeFoto) {
		InputStream is = amazonS3.getObject(BUCKET, nomeFoto).getObjectContent();
		try {
			return IOUtils.toByteArray(is);
		} catch (IOException e) {
			logger.error("Não conseguiu recuerar foto do S3", e);
		}
		return null;
	}
	
	@Override
	public byte[] recuperarThumbnail(String fotoCerveja) {
		return recuperar(THUMBNAIL_PREFIX + fotoCerveja);
	}
	
	@Override
	public void apagarFoto(String foto) {
		amazonS3.deleteObjects(new DeleteObjectsRequest(BUCKET).withKeys(foto, THUMBNAIL_PREFIX + foto));
	}
	
	
	@Override
	public String getUrl(String nomeFoto) {
		if(!StringUtils.isEmpty(nomeFoto)){
			return URLS3_BUCKET + "/" + nomeFoto;
		}
		return URLS3_BUCKET + "/";
	}
	
	@Override
	public String getUrlThumbnail(String nomeFoto) {
		return getUrl(THUMBNAIL_PREFIX + nomeFoto);
	}

	private void enviarFoto(String novoNome, MultipartFile arquivo, AccessControlList acl) throws IOException {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(arquivo.getContentType());
		metadata.setContentLength(arquivo.getSize());

		amazonS3.putObject(
					new PutObjectRequest(BUCKET, novoNome, arquivo.getInputStream(), metadata)
						.withAccessControlList(acl)
					);
	}
	
	private void enviarThumbnail(String novoNome, MultipartFile arquivo, AccessControlList acl) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Thumbnails.of(arquivo.getInputStream()).size(40, 68).toOutputStream(os);
		
		byte[] array = os.toByteArray();
		InputStream  is = new ByteArrayInputStream(array);
		
		ObjectMetadata thumbMetadata = new ObjectMetadata();
		thumbMetadata.setContentType(arquivo.getContentType());
		thumbMetadata.setContentLength(array.length);
		
		amazonS3.putObject(
				new PutObjectRequest(BUCKET, THUMBNAIL_PREFIX + novoNome, is, thumbMetadata)
					.withAccessControlList(acl)
				);
	}
	

}
