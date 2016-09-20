package com.aftermidnight.brewer.repository.helper.cidade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.aftermidnight.brewer.model.Cidade;
import com.aftermidnight.brewer.repository.filter.CidadeFilter;
public interface CidadesQueries {

	public Page<Cidade> filtrar(CidadeFilter filtro, Pageable pageable);
	
	public Cidade buscarComEstado(Long codigo);
}
