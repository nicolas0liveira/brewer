//Caso alguém já tenha definido o Brewer como variavél vamos usar ela, caso contrário criamos uma nova
var Brewer = Brewer || {};


Brewer.MaskMoney = (function(){
	
	function MaskMoney(){
		this.decimal = $('.js-decimal');
		this.plain = $('.js-int');
	}
	
	MaskMoney.prototype.enable = function() {
		//https://github.com/normandesjr/jquery-mask-number
		
//		this.decimal.maskMoney({ decimal:',', thousands:'.' });
//		this.plain.maskMoney({ precision:0 , thousands:'.' });

		this.decimal.maskNumber({ decimal:',', thousands:'.' });
		this.plain.maskNumber({integer: true, thousands: '.'});
	}
	
	return MaskMoney;
	
}());



Brewer.MaskPhoneNumber = (function(){
	
	function MaskPhoneNumber(){
		this.inputPhoneNumber = $('.js-phone-number');
	}
	
	MaskPhoneNumber.prototype.enable = function(){
		var maskBehavior = function (val) {
			return val.replace(/\D/g, '').length === 11 ? '(00) 00000-0000' : '(00) 0000-00009';
		};
			
		var options = {
				onKeyPress: function(val, e, field, options) {
				field.mask(maskBehavior.apply({}, arguments), options);
			}
		};

		this.inputPhoneNumber.mask(maskBehavior, options);
	}
	
	return MaskPhoneNumber;
	
}());

Brewer.MaskCep = (function() {
	
	function MaskCep() {
		this.inputCep = $('.js-cep');
	}
	
	MaskCep.prototype.enable = function() {
		this.inputCep.mask('00.000-000');
	}
	
	return MaskCep;
	
}());


Brewer.MaskDatePicker = (function() {

	function MaskDatePicker() {
		this.inputDate = $('.js-datepicker');

		this.datepickerConfig = { 
				format: 'dd/mm/yyyy',
				orientation: 'bottom', 
				language: 'pt-BR',
				autoclose: true
		};
	}
	
	MaskDatePicker.prototype.enable = function() {
		this.inputDate.mask('00/00/0000');
		this.inputDate.attr('autocomplete', 'off')
		this.inputDate.datepicker(this.datepickerConfig);
	}
	
	return MaskDatePicker;

}());


Brewer.Security = (function(){
	
		function Security(){
			this.token = $('input[name=_csrf]').val();
			this.tokenHeader = $('input[name=_csrf_header]').val();
		}
		
		Security.prototype.enable = function(){
			//toda vez que uma requisição ajax for enviada esse cara é executado (ver: jquery.ajaxSend)
			$(document).ajaxSend( function(event, jqxhr, settings){
				jqxhr.setRequestHeader(this.tokenHeader, this.token);
			}.bind(this));
		}
		
		return Security;
}());

//função estática
numeral.language('pt-br');

Brewer.formatarMoeda = function(valor) {
	 return numeral(valor).format('0,0.00');
}

Brewer.removerFormatacaoMoeda = function(valorFormatado) {
	 return numeral().unformat(valorFormatado);
}

//http://stackoverflow.com/questions/4656843/jquery-get-querystring-from-url
Brewer.getParameterByName = function(url, name) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"), results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

$(function() {
	var maskMoney = new Brewer.MaskMoney();
	maskMoney.enable();
	
	var maskPhoneNumber = new Brewer.MaskPhoneNumber();
	maskPhoneNumber.enable();
	
	var maskCep = new Brewer.MaskCep();
	maskCep.enable();
	
	var maskDatePicker = new Brewer.MaskDatePicker();
	maskDatePicker.enable();
	
	var security = new Brewer.Security();
	security.enable();
});