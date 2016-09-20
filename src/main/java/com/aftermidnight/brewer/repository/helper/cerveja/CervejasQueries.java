package com.aftermidnight.brewer.repository.helper.cerveja;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.aftermidnight.brewer.dto.CervejaDTO;
import com.aftermidnight.brewer.dto.ValorItensEstoque;
import com.aftermidnight.brewer.model.Cerveja;
import com.aftermidnight.brewer.repository.filter.CervejaFilter;

public interface CervejasQueries {
	
	public Page<Cerveja> filtrar(CervejaFilter filtro, Pageable pageable);
	
	public List<CervejaDTO> porSkuOuNome(String skuOuNome);

	public ValorItensEstoque valorItensEstoque();
	
}

