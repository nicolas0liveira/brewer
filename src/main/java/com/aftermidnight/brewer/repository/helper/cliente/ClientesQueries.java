package com.aftermidnight.brewer.repository.helper.cliente;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.aftermidnight.brewer.model.Cliente;
import com.aftermidnight.brewer.repository.filter.ClienteFilter;

public interface ClientesQueries {
	
	public Page<Cliente> filtrar(ClienteFilter filtro, Pageable pageable);
	
	public Cliente buscarComCidadeEstado(Long codigo);
}

