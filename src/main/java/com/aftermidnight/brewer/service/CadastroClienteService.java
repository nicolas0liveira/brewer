package com.aftermidnight.brewer.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aftermidnight.brewer.model.Cliente;
import com.aftermidnight.brewer.repository.Clientes;
import com.aftermidnight.brewer.service.exception.CpfOuCnpjClienteJaCadastradoException;
import com.aftermidnight.brewer.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class CadastroClienteService {

	@Autowired
	private Clientes clientes;
	
	@Transactional
	public void salvar (Cliente cliente){
		Optional<Cliente> clienteExistente = clientes.findByCpfOuCnpj(cliente.getCpfOuCnpjSemFormatacao());
		
		if(cliente.isNovo() && clienteExistente.isPresent()){
			throw new CpfOuCnpjClienteJaCadastradoException("CPF ou CNPJ já cadastrado");
		}
		
		clientes.save(cliente);
	}

	@Transactional
	public void excluir(Cliente cliente){
		try {
			clientes.delete(cliente.getCodigo());
			clientes.flush();
		} catch(PersistenceException e){
			throw new ImpossivelExcluirEntidadeException("Impossível excluir o cliente. Já foi utilizado em alguma venda, por exemplo.");
		}
	}
	
}
