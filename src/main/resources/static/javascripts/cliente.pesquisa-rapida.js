var Brewer = Brewer || {};

Brewer.PesquisaRapidaCliente = (function(){
	
	function PesquisaRapidaCliente(){
		this.pesquisaRapidaClientesModal = $('#pesquisaRapidaClientes');
		this.nomeInput = $('#nomeInputClienteModal');
		this.btnPesquisar = $('.js-pesquisa-rapida-clientes-btn');
		this.containerTabelaPesquisa = $('#containerTabelaPesquisaRapidaClientes');
		this.htmlTabelaPesquisa = $('#tabela-pesquisa-rapida-cliente').html();
		this.template = Handlebars.compile(this.htmlTabelaPesquisa);
		this.mensagemErro = $('.js-mensagem-erro');
		
		

	}
	
	PesquisaRapidaCliente.prototype.iniciar = function(){
		this.btnPesquisar.on('click', onPesquisaRapidaClicado.bind(this));
		this.pesquisaRapidaClientesModal.on('shown.bs.modal', onModalShow.bind(this));
	}
	
	function onModalShow(){
		this.nomeInput.focus();
	}
	
	function onPesquisaRapidaClicado(event){
		event.preventDefault();
		
		$.ajax({
			url: this.pesquisaRapidaClientesModal.find('form').attr('action'),
			method: 'GET',
			contentType: 'application/json',
			data:{
				nome: this.nomeInput.val()
			},
			success: onPesquisaConcluida.bind(this),
			error: onErroPesquisa.bind(this)
		});
	}
	
	function onPesquisaConcluida(resultado){
//		console.log("resultado ", resultado);
		this.mensagemErro.addClass('hidden');

		var html = this.template(resultado);
		this.containerTabelaPesquisa.html(html);
		
		var tabelaClientePesquisaRapida = new Brewer.TabelaPesquisaRapidaCliente(this.pesquisaRapidaClientesModal);
		tabelaClientePesquisaRapida.iniciar();
	
	}
	
	function onErroPesquisa(){
		this.mensagemErro.removeClass('hidden');
	}
	
	return PesquisaRapidaCliente;
	
}());


Brewer.TabelaPesquisaRapidaCliente = (function (){
	
	function TabelaPesquisaRapidaCliente(modal){
		this.modalCliente = modal;
		this.clientes = $('.js-cliente-pesquisa-rapida-linha');
	}
	
	TabelaPesquisaRapidaCliente.prototype.iniciar = function (){
		this.clientes.on('click', onClienteSelecionado.bind(this));
	}
	
	function onClienteSelecionado(event){
		this.modalCliente.modal('hide');

		var clienteSelecionado = $(event.currentTarget);
		
		$('#nomeCliente').val(clienteSelecionado.data('nome'));
		$('#codigoCliente').val(clienteSelecionado.data('codigo'));
		
	}
	
	return TabelaPesquisaRapidaCliente;
	
}());
	

$(function(){
	var pesqusiaRapidaCliente = new Brewer.PesquisaRapidaCliente();
	pesqusiaRapidaCliente.iniciar();
	
});

