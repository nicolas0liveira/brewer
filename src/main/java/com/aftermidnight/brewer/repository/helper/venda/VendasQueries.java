package com.aftermidnight.brewer.repository.helper.venda;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.aftermidnight.brewer.dto.VendaMes;
import com.aftermidnight.brewer.dto.VendaMesOrigem;
import com.aftermidnight.brewer.model.Venda;
import com.aftermidnight.brewer.repository.filter.VendaFilter;

public interface VendasQueries {

	public Page<Venda> filtrar(VendaFilter filtro, Pageable pageable);
	
	public Venda buscarComItens(Long codigo);
	
	public BigDecimal valorTotalNoAno();

	public BigDecimal valorTotalNoMes();

	public BigDecimal valorTicketMedio();

	public Long totalClientes();
	
	public List<VendaMes> totalPorMes();
	
	public List<VendaMesOrigem> totalPorMesOrigem();
}
