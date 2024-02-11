package com.flipkart.electronics_shopping.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;

@Component
public class CookieManager {

	@Value("${myapp.domain}")
	private String domain;

	public Cookie configure(Cookie cookie,long expirationInSeconds) {
		cookie.setDomain(domain);
		cookie.setSecure(false);
		cookie.setHttpOnly(true);
		cookie.setMaxAge((int)expirationInSeconds);
		cookie.setPath("/");

		return cookie;
	}

	public Cookie  invalidate(Cookie cookie) {//invalidate the cookie from browser when logout 
		cookie.setMaxAge(0);
		cookie.setPath("/");
		return cookie;
	}
}
