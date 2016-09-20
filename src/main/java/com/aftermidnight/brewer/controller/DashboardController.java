package com.aftermidnight.brewer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.aftermidnight.brewer.dto.ValorItensEstoque;
import com.aftermidnight.brewer.repository.Cervejas;
import com.aftermidnight.brewer.repository.Vendas;

@Controller
public class DashboardController {
	
	@Autowired
	private Vendas vendas;
	
	@Autowired
	private Cervejas cervejas;
	

	@GetMapping("/")
	public ModelAndView dashboard() {
		ModelAndView mv = new ModelAndView("Dashboard");
		
		ValorItensEstoque valorItensEstoque = cervejas.valorItensEstoque();
		
		mv.addObject("valorEstoque", valorItensEstoque.getValor());
		mv.addObject("totalItensEstoque", valorItensEstoque.getTotalItens());
		mv.addObject("totalClientes", vendas.totalClientes());
		
		mv.addObject("vendasNoAno", vendas.valorTotalNoAno());
		mv.addObject("vendasNoMes", vendas.valorTotalNoMes());
		mv.addObject("ticketMedio", vendas.valorTicketMedio());
		
		return mv;
	}
	
}