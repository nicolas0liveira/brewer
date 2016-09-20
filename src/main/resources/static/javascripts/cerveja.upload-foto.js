;var Brewer = Brewer || {}

Brewer.UploadFoto = (function() {
	
	function UploadFoto() {

		this.inputNomeFoto = $('input[name=foto]');
		this.inputContentType = $('input[name=contentType]');
		this.novaFoto = $('input[name=novaFoto]');
		this.inputUrlFoto = $('input[name=urlFoto]');
		
		this.htmlTemplate = $("#fotoCerveja").html();
		this.template = Handlebars.compile(this.htmlTemplate);
		
		this.containerFotoCerveja = $('.js-container-foto-cerveja');
		this.uploadDrop = $('#upload-drop');
		
		this.btnRemoverFoto = $('.js-btn-remove-foto');
		
		this.imgLoading = $('.js-img-loading');
		
		
	}
	
	UploadFoto.prototype.iniciar = function () {
		var settings = {
			type: 'json',
			filelimit: 1,
			allow: '*.(jpg|jpeg|png)',
			action: this.containerFotoCerveja.data('url-foto'),
			complete: onUploadComplete.bind(this),
			beforeSend: adicionarCsrfToken,
			loadstart: onLoadStart.bind(this)
		}
		
		UIkit.uploadSelect( $('#upload-select') ,settings);
		UIkit.uploadDrop( this.uploadDrop ,settings);
		
		//para manter a foto apos uma validação, por exemplo.
		
		if ( this.inputNomeFoto.val() ) {
			renderizarFoto.call(this, {nome:this.inputNomeFoto.val(), contentType: this.inputContentType.val(), url: this.inputUrlFoto.val()} );
		}
		
	}
	
	function onLoadStart(){
		this.imgLoading.removeClass('hidden');
	}
	
	function onUploadComplete(resposta){
		this.novaFoto.val('true');
		this.inputUrlFoto.val(resposta.url);
		this.imgLoading.addClass('hidden');
		renderizarFoto.call(this, resposta);
	}
	
	function renderizarFoto(resposta){
		this.inputNomeFoto.val(resposta.nome);
		this.inputContentType.val(resposta.contentType);
		
		this.uploadDrop.addClass('hidden');

		var htmlFotoCerveja = this.template({url: resposta.url});
		this.containerFotoCerveja.append(htmlFotoCerveja);
		
		this.resposta = resposta;
		
//		this.btnRemoverFoto.data('url-foto', resposta.url);
//		this.btnRemoverFoto.on('click', onRemoverFoto.bind(this));
		$('.js-btn-remove-foto').on('click', onRemoverFoto.bind(this));
		
		
	}
	
	function onRemoverFoto(){
		$('.js-foto-cerveja').remove();
		this.uploadDrop.removeClass('hidden');
		this.inputNomeFoto.val('');
		this.inputContentType.val('');
		this.inputUrlFoto.val('');
		
		$.ajax({
            url: this.containerFotoCerveja.data('url-foto')+'/'+ this.resposta.nome,
            method: 'DELETE'
        });
	}

	function adicionarCsrfToken(xhr){
		var token = $('input[name=_csrf]').val();
		var tokenHeader = $('input[name=_csrf_header]').val();
		xhr.setRequestHeader(tokenHeader, token);
	}
	
	
	return UploadFoto;
	
})();




$(function() {
	var uploadFoto = new Brewer.UploadFoto();
	uploadFoto.iniciar();
});