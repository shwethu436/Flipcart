package com.jsp.fc.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
@Component
public class CookieManager {
	@Value("${myapp.domain}")
	private String domain;
	
	public Cookie configure(Cookie cookie, int expirationInSec) {
		
		cookie.setDomain(domain);
		cookie.setSecure(false);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(expirationInSec);
		return cookie;
	}
	
	public Cookie invalidateCookie(Cookie cookie) {//to remove the cookie
		cookie.setPath("/");
		cookie.setMaxAge(0);
		return cookie;
	}

}
