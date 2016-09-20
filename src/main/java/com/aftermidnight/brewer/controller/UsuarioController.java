package com.aftermidnight.brewer.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aftermidnight.brewer.controller.page.PageWrapper;
import com.aftermidnight.brewer.model.Usuario;
import com.aftermidnight.brewer.repository.Grupos;
import com.aftermidnight.brewer.repository.Usuarios;
import com.aftermidnight.brewer.repository.filter.UsuarioFilter;
import com.aftermidnight.brewer.service.CadastroUsuarioService;
import com.aftermidnight.brewer.service.StatusUsuario;
import com.aftermidnight.brewer.service.exception.EmailUsuarioJaCadastradoException;
import com.aftermidnight.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.aftermidnight.brewer.service.exception.SenhaObrigatoriaNovoUsuarioException;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {
	
	@Autowired
	private Usuarios usuarios;
	
	@Autowired
	private Grupos grupos;
	
	
	@Autowired 
	private CadastroUsuarioService cadastroUsuarioService;

	@RequestMapping("/novo")
	public ModelAndView novo(Usuario usuario) {
		ModelAndView mv = new ModelAndView("usuario/CadastroUsuario");
		mv.addObject("grupos", grupos.findAll());
		
		return mv;
	}
	
	@RequestMapping(value = { "/novo", "{\\d+}" }, method = RequestMethod.POST)
	public ModelAndView salvar(@Valid Usuario usuario, BindingResult resultadoValidacao, Model model, RedirectAttributes flashAttr) {
		ModelAndView mv = new ModelAndView("redirect:/usuario/novo");
		
		if(resultadoValidacao.hasErrors()){
			return novo(usuario);
		}
			
		try {
			cadastroUsuarioService.salvar(usuario);
		} catch (EmailUsuarioJaCadastradoException e) {
			resultadoValidacao.rejectValue("email", e.getMessage(), e.getMessage());
			return novo(usuario);
		}  catch (SenhaObrigatoriaNovoUsuarioException e) {
			resultadoValidacao.rejectValue("senha", e.getMessage(), e.getMessage());
			return novo(usuario);
		}
			
		flashAttr.addFlashAttribute("mensagem", "Usuário salvo com sucesso");
		return mv;
	}
	
	
	@GetMapping
	public ModelAndView pesquisar(UsuarioFilter usuarioFilter, BindingResult result, 
			@PageableDefault(size=10) Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("usuario/PesquisaUsuario");
		mv.addObject("grupos", grupos.findAll());
		PageWrapper<Usuario> paginaWrapper = new PageWrapper<>(usuarios.filtrar(usuarioFilter, pageable) ,httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		
		return mv;
	}
	
	
	@PutMapping("/status")
	@ResponseStatus(HttpStatus.OK)
	public void atualizarStatus( @RequestParam("codigos[]") Long[] codigos, @RequestParam("status")  StatusUsuario status){	
		cadastroUsuarioService.alterarStatus(codigos, status);
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Long codigo) {
		Usuario usuario = usuarios.buscarComGrupos(codigo);
		ModelAndView mv = novo(usuario);
		mv.addObject(usuario);
		return mv;
	}
	
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Usuario usuario){
		try{
			cadastroUsuarioService.excluir(usuario);
		} catch(ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	
		return ResponseEntity.ok().build();
	}
}
