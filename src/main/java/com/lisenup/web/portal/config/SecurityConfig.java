package com.lisenup.web.portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	// @formatter:off
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
					.antMatchers("bootstrap-3.3.7-dist/**", "assets/**", "favicon.ico", "/**").permitAll()
					.antMatchers("/user/**").hasRole("USER")
					.and()
				.formLogin().loginPage("/login").failureUrl("/login-error");
	}
	// @formatter:on

	// @formatter:off
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.inMemoryAuthentication()
				.withUser("user1").password("password1").roles("USER");
	}
	// @formatter:on

}
