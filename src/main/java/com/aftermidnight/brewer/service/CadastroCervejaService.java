package com.aftermidnight.brewer.service;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aftermidnight.brewer.model.Cerveja;
import com.aftermidnight.brewer.repository.Cervejas;
import com.aftermidnight.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.aftermidnight.brewer.storage.FotoStorage;

@Service
public class CadastroCervejaService {

	@Autowired
	private Cervejas cervejas;
	
	@Autowired 
	private FotoStorage fotoStorage;
	
	@Transactional
	public void salvar (Cerveja cerveja){
		cervejas.save(cerveja);
		
	}
	
	
	@Transactional
	public void excluir(Cerveja cerveja){
		try {
			String foto = cerveja.getFoto();
			cervejas.delete(cerveja.getCodigo());
			cervejas.flush();
			fotoStorage.apagarFoto(foto);
		} catch(PersistenceException e){
			throw new ImpossivelExcluirEntidadeException("Impossível excluir cerveja. Já foi utilizada em alguma venda, por exemplo.");
		}
	}
}
