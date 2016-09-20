package com.aftermidnight.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aftermidnight.brewer.controller.page.PageWrapper;
import com.aftermidnight.brewer.model.Cidade;
import com.aftermidnight.brewer.repository.Cidades;
import com.aftermidnight.brewer.repository.Estados;
import com.aftermidnight.brewer.repository.filter.CidadeFilter;
import com.aftermidnight.brewer.service.CadastroCidadeService;
import com.aftermidnight.brewer.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/cidade")
public class CidadeController {

	@Autowired
	private Cidades cidades;
	
	@Autowired
	private Estados estados;
	
	@Autowired 
	private CadastroCidadeService cadastroCidadeService;
	
	@RequestMapping("/nova")
	public ModelAndView novo(Cidade cidade) {
		ModelAndView mv = new ModelAndView("cidade/CadastroCidade");
		mv.addObject("estados", estados.findAll());
		
		return mv;
	}
	
	@Secured("ROLE_CADASTRAR_CIDADE")
	@RequestMapping(value = { "/novo" , "{\\d+}" }, method = RequestMethod.POST)
	@CacheEvict(value = "cidades" , key = "#cidade.estado.codigo", condition = "#cidade.temEstado()")//TODO: passar as anotações de cache do controler para o serviço
	public ModelAndView salvar(@Valid Cidade cidade, BindingResult resultadoValidacao, Model model, RedirectAttributes flashAttr) {
		
		if(resultadoValidacao.hasErrors()){
			return novo(cidade);	
		}	
		
		
		//TODO: verificar se cidade ja foi cadastrada. Fazer isso dentro da regra de negócio e colocar o try catch aqui
		cadastroCidadeService.salvar(cidade);
		
		flashAttr.addFlashAttribute("mensagem", "Cidade salva com sucesso");
		
		return new ModelAndView("redirect:/cidade/nova");
	}
	
	@GetMapping
	public ModelAndView pesquisar(CidadeFilter cidadeFilter, BindingResult result, 
			@PageableDefault(size=10) Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("cidade/PesquisaCidade");
		
		mv.addObject("estados", estados.findAll());
		
		
		PageWrapper<Cidade> paginaWrapper = new PageWrapper<>(cidades.filtrar(cidadeFilter, pageable) ,httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		
		return mv;
	}
	
	@Cacheable(value = "cidades", key = "#codigoEstado") //TODO: passar as anotações de cache do controler para o serviço
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Cidade> pesquisarPorCodigoEstado(
			@RequestParam(name = "estado", defaultValue = "-1") Long codigoEstado) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {	}
		return cidades.findByEstadoCodigo(codigoEstado);
	}
	
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Long codigo){
		Cidade cidade = cidades.buscarComEstado(codigo);
		ModelAndView mv = novo(cidade);
		mv.addObject(cidade);
		
		return mv;
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Cidade cidade) {
		try{
			cadastroCidadeService.excluir(cidade);
		} catch(ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	
		return ResponseEntity.ok().build();
	}
}
