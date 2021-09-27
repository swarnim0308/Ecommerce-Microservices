package com.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.order.domain.Order;
import com.order.domain.LineItem;
import com.order.exceptions.OrderNotFoundException;
import com.order.service.OrderService;
import com.order.service.LineItemService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class OrderController {

	@Autowired
	OrderService ser;

	@Autowired
	LineItemService ser2;

	@GetMapping("/hello")
	public String sayHello() {
		return "Hello from Order-Service";
	}
	
	@PostMapping("/order")
	public ResponseEntity<Order> saveOrder(@RequestBody Order order) {
		Order c = ser.addOrder(order);
		return new ResponseEntity<Order>(c, HttpStatus.CREATED);
	}

	@GetMapping("/order/{id}")
	public ResponseEntity<Order> getOrderById(@PathVariable("id") int orderid) throws OrderNotFoundException {
		Order c=ser.searchOrder(orderid);
		return new ResponseEntity<Order>(c,HttpStatus.OK);
	}

	@DeleteMapping("/order/{id}")
	public void emptyTheOrderLineItems(@PathVariable("id") int orderid) throws OrderNotFoundException {
		ser.emptyOrder(orderid);
	}

	@PutMapping("/order/{id}")
	public ResponseEntity<Order> updateTheOrder(@PathVariable("id") int orderid, @RequestBody List<LineItem> lineitems) throws OrderNotFoundException {
		Order c=ser.updateOrder(orderid, lineitems);
		return new ResponseEntity<Order>(c,HttpStatus.OK);
	}

	// for removing ORDER
	@DeleteMapping("/deleteorder/{id}")
	public void removeOrder(@PathVariable("id") int orderid) {
		ser.deleteOrder(orderid);
	}

}
