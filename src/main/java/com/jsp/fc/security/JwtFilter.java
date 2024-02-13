package com.jsp.fc.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jsp.fc.entity.AccessToken;
import com.jsp.fc.repository.AccessTokenRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Component
@AllArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
     private AccessTokenRepository accessTokenRepo;
     private CustomUserDetailService customUserDetailService;
     private JwtService jwtService;
     
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Cookie[] cookies = request.getCookies();
		String at=null;
		String rt=null;
		if(cookies!=null) {
		for(Cookie cookie:cookies) {
			if(cookie.getName().equals("At"))
				at=cookie.getValue();
			if(cookie.getName().equals("RT"))
				rt=cookie.getValue();
		}
		String userName=null;
		if(at==null||rt==null) throw new RuntimeException("user not logged in");
		Optional<AccessToken> accessToken=accessTokenRepo.findByTokenAndIsBlocked(at,false);
		if (accessToken==null) throw new RuntimeException();
		else {
			log.info("authenticating the token");
			userName=jwtService.extractUserName(at);
			if(userName==null) throw new RuntimeException("failed to authenticate");
			UserDetails userDetails=customUserDetailService.loadUserByUsername(userName);
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName,null,userDetails.getAuthorities());
			token.setDetails(new WebAuthenticationDetails(request));
			SecurityContextHolder.getContext().setAuthentication(token);
			log.info("authenticated successfully");
		}
		filterChain.doFilter(request, response);
	}
}
}
