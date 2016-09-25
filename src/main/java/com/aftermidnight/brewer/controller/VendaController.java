package com.aftermidnight.brewer.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aftermidnight.brewer.controller.page.PageWrapper;
import com.aftermidnight.brewer.controller.validator.VendaValidator;
import com.aftermidnight.brewer.dto.VendaMes;
import com.aftermidnight.brewer.dto.VendaMesOrigem;
import com.aftermidnight.brewer.mail.Mailer;
import com.aftermidnight.brewer.model.Cerveja;
import com.aftermidnight.brewer.model.ItemVenda;
import com.aftermidnight.brewer.model.StatusVenda;
import com.aftermidnight.brewer.model.TipoPessoa;
import com.aftermidnight.brewer.model.Venda;
import com.aftermidnight.brewer.repository.Cervejas;
import com.aftermidnight.brewer.repository.Vendas;
import com.aftermidnight.brewer.repository.filter.VendaFilter;
import com.aftermidnight.brewer.security.UsuarioSistema;
import com.aftermidnight.brewer.service.CadastroVendaService;
import com.aftermidnight.brewer.session.TabelasItensSession;

@Controller
@RequestMapping("/venda")
public class VendaController {
	
	@Autowired 
	private Cervejas cervejas;
	
	@Autowired
	private Vendas vendas;
	
	@Autowired
	private TabelasItensSession tabelaItensSession;
	
	@Autowired 
	private CadastroVendaService cadastroVendaService;
	
	@Autowired
	private VendaValidator vendaValidator;
	
	@Autowired
	private Mailer mailer;
	
	@InitBinder("venda") //inicializa o validador e, toda vez que encontrar um @Valid o spring vai validar o elemento ou mesmo chamando direto o metodo vendaValidator.validate
	public void inicializarValidador(WebDataBinder binder){
		binder.setValidator(vendaValidator);
	}
	
	@GetMapping("/nova")
	public ModelAndView nova(Venda venda) {
		ModelAndView mv = new ModelAndView("venda/CadastroVenda");
		
		setUuid(venda);
		
		mv.addObject("itens", venda.getItens());
		mv.addObject("valorFrete", venda.getValorFrete());
		mv.addObject("valorDesconto", venda.getValorDesconto());
		mv.addObject("valorTotalItens", tabelaItensSession.getValorTotal(venda.getUuid()));
		
		return mv;
	}
	
	@PostMapping(value = { "/nova", "{\\d+}" }, params = "salvar") //esse param é um truque para fazer funcionar o botao submit dropdown, pois o mesmo botao tem 3 funcionalidades
	public ModelAndView salvar(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioLogadoSistema) {
		validarVenda(venda, result);
		if(result.hasErrors()){
			return nova(venda);
		}
		venda.setUsuario(usuarioLogadoSistema.getUsuario());
		cadastroVendaService.salvar(venda);
		attributes.addFlashAttribute("mensagem", "venda salva com sucesso");
		ModelAndView mv = new ModelAndView("redirect:/venda/nova");
		return mv;
	}
	
	
	@PostMapping(value = "/nova", params = "emitir")
	public ModelAndView emitir(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioLogadoSistema) {
		validarVenda(venda, result);
		if(result.hasErrors()){
			return nova(venda);
		}
		venda.setUsuario(usuarioLogadoSistema.getUsuario());
		cadastroVendaService.emitir(venda);
		attributes.addFlashAttribute("mensagem", "venda emitida com sucesso");
		ModelAndView mv = new ModelAndView("redirect:/venda/nova");
		return mv;
	}

	@PostMapping(value = "/nova", params = "enviarEmail")
	public ModelAndView emitirEnviarEmail(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioLogadoSistema) {
		validarVenda(venda, result);
		
		if(result.hasErrors()){
			return nova(venda);
		}
		
		venda.setUsuario(usuarioLogadoSistema.getUsuario());
		
		venda = cadastroVendaService.emitir(venda);
		
		mailer.enviar(venda);
		
		attributes.addFlashAttribute("mensagem", String.format("Venda n° %d salva e e-mail enviado", venda.getCodigo()));
		ModelAndView mv = new ModelAndView("redirect:/venda/nova");
		return mv;
	}
	
	@PostMapping("/item")
	public ModelAndView adicionarItem(Long codigoCerveja, String uuid){
		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItensSession.adicionarItem(uuid, cerveja, 1);

		return tabelaItensVenda(uuid);
	}
	
	@PutMapping("/item/{codigoCerveja}")
	public ModelAndView alterarQuantidadeItem(@PathVariable Long codigoCerveja, Integer quantidade, String uuid){
		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItensSession.alterarQuantidadeItens(uuid, cerveja, quantidade);
		
		return tabelaItensVenda(uuid);
	}

	@DeleteMapping("item/{uuid}/{codigoCerveja}")
	public ModelAndView excluirItem(@PathVariable("codigoCerveja") Cerveja cerveja, @PathVariable("uuid")  String uuid){
		tabelaItensSession.excluirItem(uuid, cerveja);
		
		return tabelaItensVenda(uuid);
	}


	@GetMapping
	public ModelAndView pesquisar(VendaFilter vendaFilter,  @PageableDefault(size = 10) Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("/venda/PesquisaVenda");
		mv.addObject("todosStatus", StatusVenda.values());
		mv.addObject("tiposPessoa", TipoPessoa.values());
		
		PageWrapper<Venda> paginaWrapper = new PageWrapper<>(vendas.filtrar(vendaFilter, pageable) ,httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		
		return mv;
	}
	
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Long codigo){
		Venda venda = vendas.buscarComItens(codigo);
		
		
		setUuid(venda);
		for(ItemVenda item : venda.getItens()) {
			tabelaItensSession.adicionarItem(venda.getUuid(), item.getCerveja(), item.getQuantidade());
		}
		
		ModelAndView mv = nova(venda);
		mv.addObject(venda);
		
		return mv;
	}

	
	
	@PostMapping(value = "/nova", params = "cancelar")
	public ModelAndView cancelarVenda(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioLogadoSistema) {
		
		try {
			cadastroVendaService.cancelar(venda);
		} catch (AccessDeniedException e) {
			return new ModelAndView("/403");
		}
		
		attributes.addFlashAttribute("mensagem", String.format("Venda n° %s cancelada", venda.getCodigo()));
		return new ModelAndView("redirect:/venda/"+venda.getCodigo());
	}
	
	@GetMapping("/totalPorMes")
	public @ResponseBody List<VendaMes> listarTotalVendaPorMes() {
		return vendas.totalPorMes();
	}
	
	@GetMapping("/totalPorMesOrigem")
	public @ResponseBody List<VendaMesOrigem> listarTotalVendaPorOrigem() {
		return vendas.totalPorMesOrigem();
	}
	
	private void setUuid(Venda venda) {
		if(StringUtils.isEmpty(venda.getUuid())){
			venda.setUuid(UUID.randomUUID().toString());
		}
	}

	private ModelAndView tabelaItensVenda(String uuid) {
		ModelAndView mv = new ModelAndView("venda/TabelaItensVenda");
		mv.addObject("itens", tabelaItensSession.getItens(uuid));
		mv.addObject("valorTotal", tabelaItensSession.getValorTotal(uuid));
		return mv;
	}
	
	private void validarVenda(Venda venda, BindingResult result) {
		venda.adicionarItens(tabelaItensSession.getItens(venda.getUuid()));
		venda.calcularValorTotal();

		vendaValidator.validate(venda, result);
	}
}