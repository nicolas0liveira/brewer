/*representa a tabela de itens, nesse caso o precisa que o Brewer já exista*/
Brewer.TabelaItens = ( function (){
	 
	
	function TabelaItens(autocomplete){
		 this.autocomplete = autocomplete;
		 this.tabelaCervejasContainer = $('.js-tabela-cervejas-container');
		 this.uuid = $('#uuid').val();
		 
		//para enviar o eventos
		this.emitter = $({});
		this.on = this.emitter.on.bind(this.emitter);
	}
	 
	TabelaItens.prototype.iniciar = function () {
		 this.autocomplete.on('item-selecionado', onItemSelecionado.bind(this));
		 
		 bindQuantidadeItem.call(this);
		 bindTabelaItem.call(this);
	}
	
	TabelaItens.prototype.valorTotal = function() {
		return this.tabelaCervejasContainer.data('valor');
	}
	
	function onItemSelecionado(evento, item){
		console.log('item notificado pelo autocomplete', item);
		
		var resposta = $.ajax({
			url: 'item',
			method: 'POST',
			data: {
				codigoCerveja: item.codigo,
				uuid: this.uuid
			}		
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}

	
	function onItemAtualizadoNoServidor(data){
		this.tabelaCervejasContainer.html(data);
		bindQuantidadeItem.call(this);
		
		var tabelaItem = bindTabelaItem.call(this);
		
		var valorTotal = tabelaItem.data('valor-total');
		this.emitter.trigger('tabela-itens-atualizada', valorTotal);
	}
	
	function onQuantidadeItemAlterado(evento){
		var input = $(evento.target);
		var quantidade = input.val();
		
		if(quantidade <=0) input.val(1);
		
		
		var codigoCerveja = input.data('codigo-cerveja');
		
		var resposta = $.ajax({
			url: 'item/' + codigoCerveja,
			method: 'PUT',
			data: {
				quantidade: quantidade,
				uuid: this.uuid
			}
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	function onDoubleClick(evento){
//		console.log('evento',evento);
		var item = $(evento.currentTarget); //quem escutou o evento
		item.toggleClass('solicitando-exclusao');
		console.log('item',item);
		
		
		$(this).toggleClass('solicitando-exclusao');
	}
	
	function onExclusaoItem(evento){
		var codigoCerveja = $(evento.target).data('codigo-cerveja');
//		console.log('codigo cerveja para excluir', codigoCerveja);
		
		var resposta = $.ajax({
			url: 'item/' + this.uuid + "/"+ codigoCerveja,
			method: 'DELETE'
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	function bindQuantidadeItem(){
		var qtdItemInput = $('.js-tabela-cerveja-qtd-item');
		qtdItemInput.on('change', onQuantidadeItemAlterado.bind(this));
		qtdItemInput.maskNumber({integer: true});
	}
	
	function bindTabelaItem(){
		var tabelaItem = $('.js-tabela-item');
		tabelaItem.on('dblclick', onDoubleClick.bind(this));
		$('.js-exclusao-item-btn').on('click', onExclusaoItem.bind(this));
		return tabelaItem;
	}
	
	 return TabelaItens;
	 
}());