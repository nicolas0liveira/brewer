package com.aftermidnight.brewer.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aftermidnight.brewer.model.Cliente;
import com.aftermidnight.brewer.repository.helper.cliente.ClientesQueries;

@Repository
public interface Clientes extends Serializable, JpaRepository<Cliente, Long>, ClientesQueries {
	
	public Optional<Cliente> findByCpfOuCnpj(String cpfOuCnpj); //o framework já vai criar a consulta para buscar pelo atributo CPFOuCNPJ

	public List<Cliente> findByNomeStartingWithIgnoreCase(String nome);

}
