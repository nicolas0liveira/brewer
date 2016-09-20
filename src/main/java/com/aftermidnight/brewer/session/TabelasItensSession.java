package com.aftermidnight.brewer.session;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.aftermidnight.brewer.model.Cerveja;
import com.aftermidnight.brewer.model.ItemVenda;

@SessionScope
@Component
public class TabelasItensSession {
	
	private 
	Set<TabelaItensVenda> tabelas = new HashSet<>();

	public void adicionarItem(String uuid, Cerveja cerveja, int quantidade) {
		TabelaItensVenda tabela = buscarTabelaItensVendaPorUuid(uuid);
		tabela.adicionarItem(cerveja, quantidade);
		
		tabelas.add(tabela);
	}

	public void alterarQuantidadeItens(String uuid, Cerveja cerveja, Integer quantidade) {
		TabelaItensVenda tabela = buscarTabelaItensVendaPorUuid(uuid);
		tabela.alterarQuantidadeItens(cerveja, quantidade);
		
	}

	public void excluirItem(String uuid, Cerveja cerveja) {
		TabelaItensVenda tabela = buscarTabelaItensVendaPorUuid(uuid);
		tabela.excluirItem(cerveja);
		
	}

	public List<ItemVenda> getItens(String uuid) {
		TabelaItensVenda tabela = buscarTabelaItensVendaPorUuid(uuid);
		return tabela.getItens();
	}
	
	public BigDecimal getValorTotal(String uuid) {
		return buscarTabelaItensVendaPorUuid(uuid).getValorTotal();
	}
	
	private TabelaItensVenda buscarTabelaItensVendaPorUuid(String uuid){
		return tabelas.stream()
		.filter(t -> t.getUuid().equals(uuid))
		.findAny()
		.orElse(new TabelaItensVenda(uuid));
	}


	
}
