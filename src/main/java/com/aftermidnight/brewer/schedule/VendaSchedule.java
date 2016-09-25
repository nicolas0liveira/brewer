package com.aftermidnight.brewer.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aftermidnight.brewer.repository.Vendas;

@Component
public class VendaSchedule {

	@Autowired
	private Vendas vendas;
	
	/*second, minute, hour, day of month, month, day(s) of week*/
	@Scheduled(cron="0 0 0 1 * ?")
	public void cancelaVendasAntigas(){
		System.out.println(String.format("TODO: Cancelar vendas antigas. Total de vendas %d ", vendas.count() ));
		
	}
}
