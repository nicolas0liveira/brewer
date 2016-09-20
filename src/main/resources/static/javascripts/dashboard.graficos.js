var Brewer = Brewer || {};

Brewer.GraficoVendaPorMes = (function() {
	
	function GraficoVendaPorMes() {
		this.ctx = $('#graficoVendasPorMes')[0].getContext('2d');
	}
	
	GraficoVendaPorMes.prototype.iniciar = function() {
		$.ajax({
			url: 'venda/totalPorMes',
			method: 'GET',
			success: onDadosRecebidos.bind(this)
		});
	}
	
	function onDadosRecebidos(vendaMes) {
		var meses = [];
		var valores = [];
		
		vendaMes.forEach(function(o){
			meses.unshift(o.mes); //unshift insere no início da lista
			valores.unshift(o.total);
		});
		
		var graficoVendasPorMes = new Chart(this.ctx, {
		    type: 'line',
		    data: {
		    	labels: meses,
		    	datasets: [{
		    		label: 'Vendas por mês',
		    		backgroundColor: "rgba(26,179,148,0.5)",
	                pointBorderColor: "rgba(26,179,148,1)",
	                pointBackgroundColor: "#fff",
	                data: valores
		    	}]
		    },
		});
	}
	
	return GraficoVendaPorMes;
	
}());


Brewer.GraficoVendaPorMesOrigem = (function() {
	
	function GraficoVendaPorMesOrigem() {
		this.ctx = $('#graficoVendasPorMesOrigem')[0].getContext('2d');
	}
	
	GraficoVendaPorMesOrigem.prototype.iniciar = function() {
		$.ajax({
			url: 'venda/totalPorMesOrigem',
			method: 'GET',
			success: onDadosRecebidos.bind(this)
		});
	}
	
	function onDadosRecebidos(vendaMesOrigem) {
		var meses = [];
		var totalNacionais = [];
		var totalInternacionais = [];
		
		vendaMesOrigem.forEach(function(o){
			meses.unshift(o.mes); //unshift insere no início da lista
			totalNacionais.unshift(o.totalNacional);
			totalInternacionais.unshift(o.totalInternacional);
		});
		
		var graficoVendasPorMesOrigem = new Chart(this.ctx, {
		    type: 'bar',
		    data: {
		    	labels: meses,
		    	datasets: [
		    	  {
		    		label: 'Nacional',
		    		backgroundColor: "rgba(26,179,148,0.5)",
	                data: totalNacionais
		    	  } ,
		    	  
		    	  {
			    	label: 'Internacional',
			    	backgroundColor: "rgba(255, 206, 86, 0.2)",
		            data: totalInternacionais
		    	  }
		    	
		    	]
		    }
		});
	}
	
	return GraficoVendaPorMesOrigem;
	
}());

$(function() {
	var graficoVendaPorMes = new Brewer.GraficoVendaPorMes();
	graficoVendaPorMes.iniciar();
	
	var graficoVendaPorMesOrigem = new Brewer.GraficoVendaPorMesOrigem();
	graficoVendaPorMesOrigem.iniciar();
});