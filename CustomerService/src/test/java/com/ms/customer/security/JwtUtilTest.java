package com.ms.customer.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtUtilTest {

	private static final String SECRET = "ecommerce-microservices-jwt-secret-key-0123456789abcdef";

	private JwtUtil jwtUtil;
	private SecretKey key;

	@BeforeEach
	void setUp() {
		jwtUtil = new JwtUtil();
		key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
	}

	@Test
	void generateToken_isValidAndRoundTrips() {
		String token = jwtUtil.generateToken(7, "user@test.com", "CUSTOMER");

		assertNotNull(token);
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
				.parseClaimsJws(token).getBody();

		assertEquals("7", claims.getSubject());
		assertEquals("user@test.com", claims.get("email", String.class));
		assertEquals("CUSTOMER", claims.get("role", String.class));
	}

	@Test
	void generateToken_expiresInFuture() {
		String token = jwtUtil.generateToken(7, "user@test.com", "CUSTOMER");
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
				.parseClaimsJws(token).getBody();

		assertTrue(claims.getExpiration().after(new Date()));
	}
}
