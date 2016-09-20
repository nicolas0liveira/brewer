package com.aftermidnight.brewer.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aftermidnight.brewer.model.Usuario;
import com.aftermidnight.brewer.repository.Usuarios;
import com.aftermidnight.brewer.service.exception.EmailUsuarioJaCadastradoException;
import com.aftermidnight.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.aftermidnight.brewer.service.exception.SenhaObrigatoriaNovoUsuarioException;

@Service
public class CadastroUsuarioService {

	
	@Autowired
	private Usuarios usuarios;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
		
	@Transactional
	public void salvar (Usuario usuario){
		
		Optional<Usuario> usuarioOptional = usuarios.findByEmailIgnoreCase(usuario.getEmail());
		
		if(usuarioOptional.isPresent() && !usuarioOptional.get().equals(usuario)){
			throw new EmailUsuarioJaCadastradoException("Email já cadastrado");
		}
		
		if(usuario.isNovo() && StringUtils.isEmpty(usuario.getSenha())){
			throw new SenhaObrigatoriaNovoUsuarioException("A senha é obrigatória para novo usuário");
		}
		
		if(usuario.isNovo() || !StringUtils.isEmpty(usuario.getSenha())){
			usuario.setSenha(this.passwordEncoder.encode(usuario.getSenha()));
		} else if(StringUtils.isEmpty(usuario.getSenha())){ //edição de usuario
			usuario.setSenha(usuarioOptional.get().getSenha());
		}
		usuario.setConfirmacaoSenha(usuario.getSenha()); //nesse momento o beanvalidator ja verificou se as senhas estão iguais, portanto se chegou aqui significa que estão iguais

		if(usuario.isEdicao() && usuario.getAtivo() == null){
			usuario.setAtivo(usuarioOptional.get().getAtivo());
		}
		
		
		usuarios.save(usuario);
		
	}

	@Transactional
	public void alterarStatus(Long[] codigos, StatusUsuario status) {
		status.executar(codigos, usuarios);
			
	}

	@Transactional
	public void excluir(Usuario usuario){
		try {
			usuarios.delete(usuario.getCodigo());
			usuarios.flush();
		} catch(PersistenceException e){
			throw new ImpossivelExcluirEntidadeException("Impossível excluir usuário. Já efetuou alguma venda, por exemplo.");
		}
	}
	
}
