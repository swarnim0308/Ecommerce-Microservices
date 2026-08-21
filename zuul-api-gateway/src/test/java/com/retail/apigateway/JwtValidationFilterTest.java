package com.retail.apigateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

class JwtValidationFilterTest {

	private static final String SECRET = "ecommerce-microservices-jwt-secret-key-0123456789abcdef";

	private JwtValidationFilter filter;
	private SecretKey key;

	@BeforeEach
	void setUp() {
		filter = new JwtValidationFilter();
		key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
	}

	private String validToken() {
		return Jwts.builder()
				.setSubject("7")
				.claim("email", "user@test.com")
				.claim("role", "CUSTOMER")
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 3600000))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}

	@Test
	void publicPath_allowsThrough() {
		ServerWebExchange exchange = MockServerWebExchange.from(
				MockServerHttpRequest.get("/api/productservice/products"));
		GatewayFilterChain chain = mock(GatewayFilterChain.class);
		when(chain.filter(exchange)).thenReturn(Mono.empty());

		filter.filter(exchange, chain).block();

		verify(chain).filter(exchange);
	}

	@Test
	void missingAuthHeader_returns401() {
		ServerWebExchange exchange = MockServerWebExchange.from(
				MockServerHttpRequest.get("/api/cartservice/api/cart/7"));
		GatewayFilterChain chain = mock(GatewayFilterChain.class);

		filter.filter(exchange, chain).block();

		assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
		verify(chain, never()).filter(exchange);
	}

	@Test
	void invalidToken_returns401() {
		ServerWebExchange exchange = MockServerWebExchange.from(
				MockServerHttpRequest.get("/api/cartservice/api/cart/7")
					.header(HttpHeaders.AUTHORIZATION, "Bearer not.a.valid.token"));
		GatewayFilterChain chain = mock(GatewayFilterChain.class);

		filter.filter(exchange, chain).block();

		assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
		verify(chain, never()).filter(exchange);
	}

	@Test
	void validToken_allowsThrough() {
		ServerWebExchange exchange = MockServerWebExchange.from(
				MockServerHttpRequest.get("/api/cartservice/api/cart/7")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken()));
		GatewayFilterChain chain = mock(GatewayFilterChain.class);
		when(chain.filter(exchange)).thenReturn(Mono.empty());

		filter.filter(exchange, chain).block();

		verify(chain).filter(exchange);
	}
}
