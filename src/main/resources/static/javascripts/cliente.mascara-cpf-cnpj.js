var Brewer = Brewer || {}


Brewer.MascaraCpfCnpj = (function() {
	
	
	function MascaraCpfCnpj() {
		this.radioTipoPessoa = $('.js-radio-tipo-pessoa');
		this.labelCpfCnpj = $('[for=cpfOuCnpj]');
		this.inputCpfCnpj = $('#cpfOuCnpj');
	}
	
	
	MascaraCpfCnpj.prototype.enable = function(){
		this.radioTipoPessoa.on('change', onTipoPessoaAlterado.bind(this));
		this.tipoPessoaSelecionada = this.radioTipoPessoa.filter(':checked')[0];
		
		if(this.tipoPessoaSelecionada){
			aplicarMascara.call(this, $(this.tipoPessoaSelecionada));
		}
		
	}
	
	function onTipoPessoaAlterado(evento) {
		var tipoPessoaSelecionada = $(evento.currentTarget);
		aplicarMascara.call(this, tipoPessoaSelecionada);
		this.inputCpfCnpj.val('');		
	}
	
	
	function aplicarMascara(tipoPessoaSelecionada){
		this.labelCpfCnpj.text(tipoPessoaSelecionada.data('documento'));
		this.inputCpfCnpj.mask(tipoPessoaSelecionada.data('mascara'));
		this.inputCpfCnpj.removeAttr('disabled');
	}
	
	return MascaraCpfCnpj;

}());



$(function(){
	
	var MascaraCpfCnpj = new Brewer.MascaraCpfCnpj();
	MascaraCpfCnpj.enable();
	
});