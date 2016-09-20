package com.aftermidnight.brewer.model;

import com.aftermidnight.brewer.model.validation.group.CnpjGroup;
import com.aftermidnight.brewer.model.validation.group.CpfGroup;

public enum TipoPessoa {

	FISICA("Física", "CPF", "000.000.000-00", CpfGroup.class) {
		@Override
		public String formatar(String cpfOuCnpj) {
			return cpfOuCnpj.replaceAll("(\\d{3})(\\d{3})(\\d{3})", "$1.$2.$3-");
		}
	},
	JURIDICA("Jurídica", "CNPJ", "00.000.000/0000-00", CnpjGroup.class) {
		@Override
		public String formatar(String cpfOuCnpj) {
			return cpfOuCnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})", "$1.$2.$3/$4-");
		}
	};
	
	
	TipoPessoa() {}
	
	private TipoPessoa(String descricao, String documento, String mascara, Class<?> grupoValidacao) {
		this.descricao = descricao;
		this.documento = documento;
		this.mascara = mascara;
		this.grupoValidacao = grupoValidacao;
	}

	private String descricao;
	private String documento;
	private String mascara;
	private Class<?> grupoValidacao;
	
	
	public abstract String formatar(String cpfOuCnpj);
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getMascara() {
		return mascara;
	}

	public void setMascara(String mascara) {
		this.mascara = mascara;
	}

	public Class<?> getGrupoValidacao() {
		return grupoValidacao;
	}

	public void setGrupoValidacao(Class<?> grupo) {
		this.grupoValidacao = grupo;
	}

	
	public static String removerFormatacao(String cpfOuCnpj){
		return cpfOuCnpj.replaceAll("[^0-9]", "");
	}
		
}
