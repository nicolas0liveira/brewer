package com.aftermidnight.brewer.controller.page;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Order;
import org.springframework.web.util.UriComponentsBuilder;

public class PageWrapper<T> {

	private Page<T> page;
	
	private UriComponentsBuilder uriBuilder;

	public PageWrapper(Page<T> page, HttpServletRequest httpServletRequest) {
		super();
		this.page = page;
//		uriBuilder = ServletUriComponentsBuilder.fromRequest(httpServletRequest);
		
		//Precisa fazer isso devido a um bug não corrigido: https://jira.spring.io/browse/SPR-10172
		String httpUrl = httpServletRequest.getRequestURL().append(
				httpServletRequest.getQueryString() != null ?  "?" + httpServletRequest.getQueryString() : ""
				).toString().replaceAll("\\+", "%20").replaceAll("excluido", "");
		
		this.uriBuilder = UriComponentsBuilder.fromHttpUrl(httpUrl);
	}

	
	public List<T> getConteudo(){
		return page.getContent();
	}
	
	public int getAnterior(){
		return getAtual() - 1;
	}
	
	public int getAtual(){
		return page.getNumber();
	}
	
	public int getProxima(){
		return getAtual() + 1;
	}
	
	public int getTotal(){
		return page.getTotalPages();
	}
	
	public boolean isVazia(){
		return page.getContent().isEmpty();
	}
	
	public boolean hasConteudo(){
		return page.hasContent();
	}
	
	public boolean hasAnterior(){
		return page.hasPrevious();
	}
	
	public boolean hasProxima(){
		return page.hasPrevious();
	}
	
	public boolean isPrimeira(){
		return page.isFirst();
	}
	
	public boolean isUltima(){
		return page.isLast();
	}
	
	public String urlParaPagina(int pagina) {
		return uriBuilder.replaceQueryParam("page", pagina).build(true).encode().toUriString();
		
		//.build(true).encode() serve para decodificar os caracteres especiais codificados na url...
	}
	
	public String urlOrdenada(String propriedade){
		//é preciso criar um novo uribuilder com base no anterior pois senão ele gera a uri com problema: 
		// ao clicar na paginação ele acaba ordenando tb, pois ele já concatenou a ordenação no param sort, na hora de montar o param page.
		UriComponentsBuilder uriBuilderOrder = UriComponentsBuilder
				.fromUriString(uriBuilder.build(true).encode().toUriString());
		
		String propriedadeComSort = String.format("%s,%s", propriedade, inverterDirecao(propriedade));
		
		return uriBuilderOrder.replaceQueryParam("sort", propriedadeComSort).build(true).encode().toUriString();

	}
	
	public String inverterDirecao(String propriedade){
		String direcao = "asc";
		
		Order order = page.getSort() != null ? page.getSort().getOrderFor(propriedade) : null;
		if(order != null){
			direcao = order.isAscending() ? "desc" : "asc";
		}
		
		return direcao;
	}
	
	public boolean isAscendente(String prop){
		Order order = page.getSort() != null ? page.getSort().getOrderFor(prop) : null;
		if(order == null) return false; 
		
		return  order.isAscending();
		
	}
	
	public boolean isOrdenadaPor(String prop){
		Order order = page.getSort() != null ? page.getSort().getOrderFor(prop) : null;
		if(order == null) return false;
		
		return true;
	}
	
}

