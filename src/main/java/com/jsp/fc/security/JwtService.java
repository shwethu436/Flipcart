package com.jsp.fc.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
@Service
public class JwtService {
	@Value("${myapp.secret}")
	private String secret;
	@Value("${myapp.access.expiry}")
	private Long accessExpirationInSeconds;
	@Value("${myapp.refresh.expiry}")
	private Long refreshExpirationInSeconds;
	
	public String generateAccessToken(String userName) {
        return generateJwt(new HashMap<String,Object>(), userName,accessExpirationInSeconds*1000l);
	}
	
	public String generateRefreshToken(String userName) {
        return generateJwt(new HashMap<String,Object>(), userName,refreshExpirationInSeconds*1000l);
	}
	
	private String generateJwt(Map<String, Object> claims,String userName,long expiry) {
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(userName)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+ expiry))
				.signWith(getSignature(), SignatureAlgorithm.HS512)
				.compact();
     }
	
	private Key getSignature() {
		byte[] secreBytes= Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(secreBytes);
	}

}
