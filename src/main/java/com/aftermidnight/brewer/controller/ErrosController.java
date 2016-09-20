package com.aftermidnight.brewer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrosController {

	@GetMapping("/403")
	public String acessoNegado(){
		return "403";
	}
	
	@GetMapping("/404")
	public String paginaNaoEncontrada(){
		return "404";
	}
	
	@GetMapping("/500")
	public String erroNoServidor(){
		return "500";
	}
	
	
}
