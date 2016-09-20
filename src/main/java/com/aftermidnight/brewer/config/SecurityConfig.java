package com.aftermidnight.brewer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.aftermidnight.brewer.security.AppUserDatailsService;

@EnableWebSecurity
@ComponentScan(basePackageClasses = {AppUserDatailsService.class})
@EnableGlobalMethodSecurity(prePostEnabled = true) //permite usar outras anotações como o @PreAuthorize, utilizado no cadastroVendasService
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//Apenas para testar 
//		auth.inMemoryAuthentication()
//			.withUser("admin").password("admin").roles("CADASTRO_CLIENTE");
		
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.antMatchers("/layout/**")
			.antMatchers("/images/**")
			.antMatchers("/javascripts/**")
			.antMatchers("/stylesheets/**");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/* Colocar primeiro as restrições: bloqueia primeiro, libera depois. */
		http
			.authorizeRequests()
				.antMatchers("/cidade/nova").hasAnyRole("CADASTRAR_CIDADE") /*a permissao deve posuir o prefixo ROLE_ no banco...*/
				.antMatchers("/usuario/**").hasAnyRole("CADASTRAR_USUARIO")
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.loginPage("/login")
				.permitAll()
				.and()
			.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.and()
			.exceptionHandling()
				.accessDeniedPage("/403")
				.and()
			.sessionManagement()
				.invalidSessionUrl("/login")
			
			;
		
		
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
}
