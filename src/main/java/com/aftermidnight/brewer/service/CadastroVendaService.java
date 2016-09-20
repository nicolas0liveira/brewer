package com.aftermidnight.brewer.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aftermidnight.brewer.model.StatusVenda;
import com.aftermidnight.brewer.model.Venda;
import com.aftermidnight.brewer.repository.Vendas;
import com.aftermidnight.brewer.service.event.venda.VendaEvent;
import com.aftermidnight.brewer.service.exception.AlteracaoVendaProibidaException;

@Service
public class CadastroVendaService {

	@Autowired 
	private Vendas vendas;
	
	@Autowired 
	private ApplicationEventPublisher publisher;
	
	@Transactional
	public Venda salvar(Venda venda){
			
		if(venda.isNova()){
			venda.setDataCriacao(LocalDateTime.now());
		} else {
			Venda vendaExistente = vendas.getOne(venda.getCodigo());
			venda.setDataCriacao(vendaExistente.getDataCriacao());
			
			if (vendaExistente.isSalvarProibido()) {
				throw new AlteracaoVendaProibidaException("Usuário tentando salvar uma venda proibida");
			}
		}
		
		if(venda.getDataEntrega() != null ){
			venda.setDataHoraEntrega(LocalDateTime.of(venda.getDataEntrega(), venda.getHorarioEntrega() != null ? venda.getHorarioEntrega() : LocalTime.NOON));
		}
		
		return vendas.saveAndFlush(venda); //para salvar e rotornar logo um id
	}

	
	@Transactional
	public void emitir(Venda venda) {
		venda.setStatus(StatusVenda.EMITIDA);
		publisher.publishEvent(new VendaEvent(venda));
		salvar(venda);
	}

	@Transactional
	@PreAuthorize(/*regra de quem pode chamar o metodo*/ "#venda.usuario == principal.usuario or hasRole('CANCELAR_VENDA')")
	public void cancelar(Venda venda) {
		Venda vendaExistente = vendas.getOne(venda.getCodigo());
		vendaExistente.setStatus(StatusVenda.CANCELADA);
		vendas.save(vendaExistente);
	}

}
