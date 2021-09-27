package com.ms.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ms.customer.entity.Customer;
import com.ms.customer.exception.IdNotFoundException;
import com.ms.customer.service.CustomerServiceImpl;

@RestController
@RequestMapping("/customer")
public class Controller {

	@Autowired
	private CustomerServiceImpl customerService;
	@GetMapping("/hello")
	public String hello() {
		return "hello from customer-service";
	}
	// Post Mapping - Create Customer
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "/addCustomer")
	public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer) {
		return new ResponseEntity<Customer>(customerService.save(customer), HttpStatus.CREATED);
	}

	// Get Mapping - Search Customer
	@GetMapping(value = "/searchCustomer/{id}")
	public ResponseEntity<Customer> searchCustomerById(@PathVariable("id") int id) throws IdNotFoundException {
		Customer customer = customerService.findCustomerById(id);
		System.err.println(customer);
		return ResponseEntity.ok(customer);
	}

	// Delete Mapping
	@DeleteMapping(value = "/deleteCustomer/{id}")
	public ResponseEntity<String> deleteCustomerById(@PathVariable("id") int id) throws IdNotFoundException {
		customerService.deleteCustomer(id);
		return new ResponseEntity<>("Customer ID " + id + " is deleted successfully", HttpStatus.OK);
	}

	// Put Mapping - Update Customer
	@PutMapping(value = "/updateCustomer/{id}")
	public ResponseEntity<String> updateCustomerById(@RequestBody Customer customer, @PathVariable("id") int id) throws IdNotFoundException {
		customerService.updateCustomerById(customer, id);
		return new ResponseEntity<>("Updated Successfully with an Customer Id: " + id, HttpStatus.OK);
	}

}
