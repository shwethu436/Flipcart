package com.jsp.fc.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	@Autowired
	private CustomUserDetailService customUserDetailService;
	@Autowired
	private JwtFilter jwtFilter;
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		 return http.csrf(csrf ->csrf.disable())
				 .authorizeHttpRequests(auth ->auth.requestMatchers("/**").permitAll()
				 .anyRequest().authenticated())
				 .sessionManagement(management ->
				 management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				 .authenticationProvider(authenticationProvider())
				 .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				 .build();
	}
	
	@Bean
	AuthenticationProvider authenticationProvider() {
	 DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
	 provider.setUserDetailsService(customUserDetailService);
	 provider.setPasswordEncoder(passwordEncoder());
	 return provider;
	}
	
	@Bean
	AuthenticationManager manager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

}
