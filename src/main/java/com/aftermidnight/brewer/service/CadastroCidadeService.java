package com.aftermidnight.brewer.service;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aftermidnight.brewer.model.Cidade;
import com.aftermidnight.brewer.repository.Cidades;
import com.aftermidnight.brewer.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class CadastroCidadeService {

	
	@Autowired
	private Cidades cidades;
	
	
	@Transactional
	public void salvar (Cidade cidade){
		cidades.save(cidade);
	}


	@Transactional
	public void excluir(Cidade cidade){
		try {
			cidades.delete(cidade.getCodigo());
			cidades.flush();
		} catch(PersistenceException e){
			throw new ImpossivelExcluirEntidadeException("Impossível excluir esta cidade. Já foi utilizado em algum cliente, por exemplo.");
		}
	}
	
}
