package com.retail.apigateway;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class JwtValidationFilter implements GlobalFilter, Ordered {

	// Must match the secret used by Customer-Service when issuing tokens.
	private static final String SECRET = "ecommerce-microservices-jwt-secret-key-0123456789abcdef";

	// Routes reachable without a token.
	private static final List<String> PUBLIC_PREFIXES = List.of(
			"/api/productservice/products",
			"/api/customerservice/customer/addCustomer",
			"/api/customerservice/customer/login");

	private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String path = exchange.getRequest().getURI().getPath();
		if (isPublic(path)) {
			return chain.filter(exchange);
		}

		String auth = exchange.getRequest().getHeaders().getFirst("Authorization");
		if (auth == null || !auth.startsWith("Bearer ")) {
			return unauthorized(exchange);
		}

		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(auth.substring(7));
		} catch (Exception e) {
			return unauthorized(exchange);
		}
		return chain.filter(exchange);
	}

	private boolean isPublic(String path) {
		return PUBLIC_PREFIXES.stream().anyMatch(path::startsWith);
	}

	private Mono<Void> unauthorized(ServerWebExchange exchange) {
		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
		return exchange.getResponse().setComplete();
	}

	@Override
	public int getOrder() {
		return -100;
	}
}
