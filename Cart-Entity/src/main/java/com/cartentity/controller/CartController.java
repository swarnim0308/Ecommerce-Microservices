package com.cartentity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cartentity.domain.CartEntity;
import com.cartentity.service.CartEntityService;
import com.cartentity.service.LineItemService;

import lombok.extern.slf4j.Slf4j;

@RefreshScope
@Slf4j
@RestController
@RequestMapping("/api")
public class CartController {

	@Autowired
	CartEntityService ser;

	@Autowired
	LineItemService ser2;
	
	@GetMapping("/hello")
	public String sayHello() {
		return "Hello from cart Service";
	}
	@PostMapping("/cart")
	public ResponseEntity<CartEntity> saveCart(@RequestBody CartEntity cart) {
		CartEntity c = ser.addCart(cart);
		log.info("Saving Cart Details");
		return new ResponseEntity<CartEntity>(c, HttpStatus.CREATED);
	}

	@GetMapping("/cart/{id}")
	public CartEntity getCartById(@PathVariable("id") int cartid) {
		log.info("Getting Cart Details");
		return ser.getItem(cartid);
	}

	@DeleteMapping("/cart/{id}")
	public void emptyTheCartLineItems(@PathVariable("id") int cartid) {
		log.info("Emptying Cart Line Item Details");
		ser.emptyCart(cartid);
	}

	@PutMapping("/cart/{id}")
	public CartEntity updateTheCart(@PathVariable("id") int cartid, @RequestBody CartEntity cart) {
		log.info("Updating Cart Details");
		return ser.updateCart(cartid, cart);
	}

	// for removing CART
	@DeleteMapping("/deletecart/{id}")
	public void removeCart(@PathVariable("id") int cartid) {
		log.info("Remove Cart");
		ser.deleteCartId(cartid);
	}

}
