package com.ms.customer.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	// Must match the secret used by the API gateway for validation.
	private static final String SECRET = "ecommerce-microservices-jwt-secret-key-0123456789abcdef";
	private static final long EXPIRATION_MS = 86400000L; // 24 hours

	private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

	public String generateToken(int customerId, String email, String role) {
		return Jwts.builder()
				.setSubject(String.valueOf(customerId))
				.claim("email", email)
				.claim("role", role)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}
}
