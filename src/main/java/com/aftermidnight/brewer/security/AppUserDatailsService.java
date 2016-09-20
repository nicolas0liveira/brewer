package com.aftermidnight.brewer.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.aftermidnight.brewer.model.Usuario;
import com.aftermidnight.brewer.repository.Usuarios;

@Service
public class AppUserDatailsService implements UserDetailsService {
	
	@Autowired
	private Usuarios usuarios;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//na tela de login tem um atributo username, é ele que recebemos como parâmetro aqui
		
		Optional<Usuario> optionalUsuario = usuarios.porEmailEAtivo(username);
		Usuario usuario = optionalUsuario.orElseThrow( () -> new UsernameNotFoundException("Usuário ou senha incorretos"));
//		return new User(usuario.getEmail(), usuario.getSenha(), getPermissoes(usuario));
		return new UsuarioSistema(usuario, getPermissoes(usuario));
	}

	private Collection<? extends GrantedAuthority> getPermissoes(Usuario usuario) {
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		
		//lista de permisoes do usuario
		List<String> permissoes = usuarios.permissoes(usuario);
		permissoes.forEach(p -> authorities.add(new SimpleGrantedAuthority(p.toUpperCase())));
		
		return authorities;
	}


}
