Brewer = Brewer || {};

Brewer.Autocomplete = ( function () {
	
	function Autocomplete() {
		this.skuOuNomeInput = $('.js-sku-nome-cerveja-input');
		var htmlTemplateAutocomplete =  $('#template-autocomplete-cerveja').html();
		this.template = Handlebars.compile(htmlTemplateAutocomplete);
		
		//para enviar o eventos
		this.emitter = $({});
		this.on = this.emitter.on.bind(this.emitter);

	}

	Autocomplete.prototype.iniciar = function () {
		
		var options = {
				url: function (skuOuNome) {
					return this.skuOuNomeInput.data('url') + '?skuOuNome=' + skuOuNome;
				}.bind(this),
				getValue: 'nome', 
				minCharNumber: 3,
				requestDelay: 300, // milisegundos de delay antes de ir ao servidor
				ajaxSettings: {
					contentType: 'application/json'
				},
				template: {
					type:'custom',
					method: preencheTemplate.bind(this)
				},
				list: {//para adicionar hookers
					onChooseEvent: onItemSelecionado.bind(this)
				}
		};
		
				
		this.skuOuNomeInput.easyAutocomplete(options);
		
	}
	
	
	function onItemSelecionado(){
//		console.log('selecionou: ', this.skuOuNomeInput.getSelectedItemData());
//		this.emitter.trigger('nome-evento',dados-para-enviar)
		this.emitter.trigger('item-selecionado' , this.skuOuNomeInput.getSelectedItemData());
		this.skuOuNomeInput.val('');
		this.skuOuNomeInput.focus();
	}
	
	function preencheTemplate(nome, cerveja){
		cerveja.valorFormatado = Brewer.formatarMoeda(cerveja.valor);
		//retorna um html que vai ser renderizado
		return this.template(cerveja);
	}
	
	return Autocomplete;
	
}());