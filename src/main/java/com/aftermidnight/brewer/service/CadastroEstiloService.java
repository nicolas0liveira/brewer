package com.aftermidnight.brewer.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aftermidnight.brewer.model.Estilo;
import com.aftermidnight.brewer.repository.Estilos;
import com.aftermidnight.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.aftermidnight.brewer.service.exception.NomeEstiloJaCadastradoException;

@Service
public class CadastroEstiloService {

	
	@Autowired
	private Estilos estilos;
	
	@Transactional
	public Estilo salvar (Estilo estilo){
		
		Optional<Estilo> estiloExistente = estilos.findByNomeIgnoreCase(estilo.getNome());
		
		if(estilo.isNovo() && estiloExistente.isPresent()){
			throw new NomeEstiloJaCadastradoException("Nome de estilo já cadastrado");
		}
				
		return estilos.saveAndFlush(estilo);

	}

	@Transactional
	public void excluir(Estilo estilo){
		try {
			estilos.delete(estilo.getCodigo());
			estilos.flush();
		} catch(PersistenceException e){
			throw new ImpossivelExcluirEntidadeException("Impossível excluir estilo. Já foi utilizado em alguma cerveja, por exemplo.");
		}
	}
	
}
