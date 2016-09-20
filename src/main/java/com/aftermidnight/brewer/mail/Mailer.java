package com.aftermidnight.brewer.mail;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.aftermidnight.brewer.model.Cerveja;
import com.aftermidnight.brewer.model.ItemVenda;
import com.aftermidnight.brewer.model.Venda;
import com.aftermidnight.brewer.storage.FotoStorage;

@Component
public class Mailer {

	private static Logger logger = LoggerFactory.getLogger(Mailer.class);
	
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TemplateEngine thymeleaf;
	
	@Autowired 
	private FotoStorage fotoStorage;

	
	@Async
	public void enviar(Venda venda){
		Context context = new Context(new Locale("pt", "BR"));
		context.setVariable("venda", venda);
		context.setVariable("logo", "logo");
		
		
		Map<String,String> fotos = new HashMap<>();
		boolean adicionarMockCerveja = false;
		for(ItemVenda item : venda.getItens()){
			Cerveja cerveja = item.getCerveja();
			
			if(cerveja.temFoto()){
				String cid = "foto-"+cerveja.getCodigo();
				context.setVariable(cid, cid);
				fotos.put(cid, cerveja.getFoto() + "|" + cerveja.getContentType());
			} else {
				adicionarMockCerveja = true;
				context.setVariable("mockCerveja", "mockCerveja");
			}
		}
		
		String email = thymeleaf.process("mail/ResumoVenda", context);
		
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			helper.setFrom("nk.oliveira@gmail.com");
			helper.addTo(venda.getCliente().getEmail());
			helper.setSubject(String.format("Brewer - Venda n° %d realizada", venda.getCodigo()));
			helper.setText(email, true);
			
			helper.addInline("logo", new ClassPathResource("static/images/logo-gray.png"));
			
			if(adicionarMockCerveja){
				helper.addInline("mockCerveja", new ClassPathResource("static/images/cerveja-mock.png"));
			}
			
			
			for (String cid: fotos.keySet()){
				String[] fotoEContentType = fotos.get(cid).split("\\|");//duas barras para nao achar que é o regex |(ou) 
				String nomefoto = fotoEContentType[0];
				String contentType = fotoEContentType[1];
				byte[] foto = fotoStorage.recuperarThumbnail(nomefoto);
				helper.addInline(cid, new ByteArrayResource(foto), contentType);
				
			}
			
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			logger.error("Erro ao enviar e-mail", e);
		}
		
		
		
		
	}
	
	
	@Async
	public void enviarEmailTeste(Venda venda){
		SimpleMailMessage mensagem = new SimpleMailMessage();
		mensagem.setFrom("nk.oliveira@gmail.com");
		mensagem.setTo(venda.getCliente().getEmail());
		mensagem.setSubject("Venda Efetuada");
		mensagem.setText("Obrigado, sua venda foi efetuada com sucesso!");
		
		mailSender.send(mensagem);
		
	}
}
