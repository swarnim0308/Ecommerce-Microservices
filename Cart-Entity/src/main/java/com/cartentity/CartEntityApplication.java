package com.cartentity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class CartEntityApplication {

	public static void main(String[] args) {
		SpringApplication.run(CartEntityApplication.class, args);
		System.err.println("Cart service started at 9004");
	}

	

}
