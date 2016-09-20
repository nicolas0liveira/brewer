package com.aftermidnight.brewer.service.event.venda;

import com.aftermidnight.brewer.model.Venda;

public class VendaEvent {
	
	private Venda venda;

	public VendaEvent(Venda venda) {
		this.venda = venda;
	}

	public Venda getVenda() {
		return venda;
	}

	public void setVenda(Venda venda) {
		this.venda = venda;
	}
	
	
	

}
