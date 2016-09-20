package com.aftermidnight.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aftermidnight.brewer.controller.page.PageWrapper;
import com.aftermidnight.brewer.model.Cliente;
import com.aftermidnight.brewer.model.TipoPessoa;
import com.aftermidnight.brewer.repository.Clientes;
import com.aftermidnight.brewer.repository.Estados;
import com.aftermidnight.brewer.repository.filter.ClienteFilter;
import com.aftermidnight.brewer.service.CadastroClienteService;
import com.aftermidnight.brewer.service.exception.CpfOuCnpjClienteJaCadastradoException;
import com.aftermidnight.brewer.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

	@Autowired
	private Clientes clientes;
	
	@Autowired
	private Estados estados;
	
	@Autowired 
	private CadastroClienteService cadastroClienteService;
	
	@RequestMapping("/novo")
	public ModelAndView novo(Cliente cliente) {
		ModelAndView mv = new ModelAndView("cliente/CadastroCliente");
		mv.addObject("tiposPessoa", TipoPessoa.values());
		mv.addObject("estados", estados.findAll());
		
		return mv;
	}
	
	@RequestMapping(value = { "/novo", "{\\d+}" }, method = RequestMethod.POST)
	public ModelAndView salvar(@Valid Cliente cliente, BindingResult resultadoValidacao, Model model, RedirectAttributes flashAttr) {
		
		if(resultadoValidacao.hasErrors()){
			return novo(cliente);	
		}	
		
		try{
			cadastroClienteService.salvar(cliente);
		}catch (CpfOuCnpjClienteJaCadastradoException e) {
			resultadoValidacao.rejectValue("cpfOuCnpj", e.getMessage(), e.getMessage());
			return novo(cliente);	
		}
		
		flashAttr.addFlashAttribute("mensagem", "Cliente salvo com sucesso");
		
		return new ModelAndView("redirect:/cliente/novo");
	}
	
	@GetMapping
	public ModelAndView pesquisar(ClienteFilter clienteFilter, BindingResult result, 
			@PageableDefault(size=20) Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("cliente/PesquisaCliente");
		
		mv.addObject("tiposPessoa", TipoPessoa.values());
		
		PageWrapper<Cliente> paginaWrapper = new PageWrapper<>(clientes.filtrar(clienteFilter, pageable) ,httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		
		return mv;
	}
	
	
	@RequestMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody List<Cliente> pesquisar(String nome) {
		validarTamanhoDoNome(nome);
		return clientes.findByNomeStartingWithIgnoreCase(nome);
	}
	
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Long codigo){
		Cliente cliente = clientes.buscarComCidadeEstado(codigo);
		ModelAndView mv = novo(cliente);
		mv.addObject(cliente);
		
		return mv;
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Cliente cliente) {
		try{
			cadastroClienteService.excluir(cliente);
		} catch(ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	
		return ResponseEntity.ok().build();
	}

	private void validarTamanhoDoNome(String nome) {
		if(StringUtils.isEmpty(nome) || nome.length() < 3){
			throw new IllegalArgumentException();
		}
	}
	
	//trata apenas o IllegalArgumentException para esse controller!
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Void> tratarIllegalArgumentException(IllegalArgumentException e){
		return ResponseEntity.badRequest().build();
	}
	
	
	
}
