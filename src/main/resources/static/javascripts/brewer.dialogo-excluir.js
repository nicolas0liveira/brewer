Brewer = Brewer || {}

Brewer.DialogoExcluir = (function (){
	
	function DialogoExcluir(){
		this.paramExcluido = 'excluido';
		this.exclusaoBtn = $('.js-excluir-btn');
	}
	
	DialogoExcluir.prototype.iniciar = function(){
		this.exclusaoBtn.on('click', onExcluirClicado.bind(this));
		
		//search representa as querystrings da url. Se existe o parametro 'excluido'
		if(window.location.search.indexOf(this.paramExcluido) > -1){
			swal('Pronto!', 'Excluído com Secesso', 'success');
		}
	}
	
	
	function onExcluirClicado(evento) {
		event.preventDefault();
		var botaoClicado = $(evento.currentTarget);
		var url = botaoClicado.data('url');
		var objeto = botaoClicado.data('objeto');
		
		swal({
			title: 'Tem certeza?',
			text: 'Excluir "' + objeto + '" ? Você não poderá recuperar depois.',
			showCancelButton: true,
			confirmButtonColor: '#DD6B55',
			confirmButtonText: 'Sim, exclua agora!',
			closeOnConfirm: false,
			//showLoaderOnConfirm: true
			
		}, onExcluirConfirmado.bind(this, url));
	}

	
	function onExcluirConfirmado(url){
		//console.log('url', url);
		
		$.ajax({
			url: url,
			method: 'DELETE',
			success: onExcluidoSucesso.bind(this),
			error: onErrorAoExcluir.bind(this)
		});
	}
	
	function onExcluidoSucesso(){
		//window.location.reload();
		var urlAtual = window.location.href;
		var separador = urlAtual.indexOf('?') > -1 ? '&' : '?';
		
		var novaUrl = urlAtual.indexOf(this.paramExcluido) > -1 ? urlAtual : urlAtual + separador + this.paramExcluido;
		
		window.location = novaUrl;
	}
	
	function onErrorAoExcluir(e){
		swal('Oops!', e.responseText, 'error');
	}
	

	return DialogoExcluir;
	
	
	
}());


$(function(){
	var dialogo = new Brewer.DialogoExcluir();
	dialogo.iniciar();
	
});