package com.aftermidnight.brewer.repository.filter;

import java.util.List;

import com.aftermidnight.brewer.model.Grupo;

public class UsuarioFilter {

	private String nome;

	private String email;
	
	private List<Grupo> grupos;
	
	private Boolean ativo;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Grupo> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<Grupo> grupos) {
		this.grupos = grupos;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}	
	
	
	

	
}
