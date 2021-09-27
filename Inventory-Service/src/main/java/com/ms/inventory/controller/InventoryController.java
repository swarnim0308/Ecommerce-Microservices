package com.ms.inventory.controller;

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

import com.ms.inventory.entity.InventoryEntity;
import com.ms.inventory.exception.IdNotFoundException;
import com.ms.inventory.service.InventoryServiceImpl;

@RestController
@RefreshScope
@RequestMapping("/api")
public class InventoryController {
	
	@Autowired
	private InventoryServiceImpl service;
	
	@GetMapping("/hello")
	public String sayHello() {
		return "Hello from Inventory-Service";
	}
	
	@PostMapping("/inventory") //  /api/inventory
	public ResponseEntity<?> saveInventory(@RequestBody InventoryEntity entity){
		System.err.println(entity);
		return new ResponseEntity<InventoryEntity>(service.createInventory(entity),HttpStatus.CREATED);
	}
	
	@GetMapping("/inventory/{id}")
	public ResponseEntity<?> getInventoryById(@PathVariable("id") int id) throws IdNotFoundException{
		return new ResponseEntity<InventoryEntity>(service.getInventoryById(id),HttpStatus.OK);
	}
	
	@PutMapping("/inventory/{id}")
	public ResponseEntity<String> updateInventoryById(@PathVariable("id") int id, @RequestBody InventoryEntity entity) throws IdNotFoundException{
		service.updateInventory(id, entity);
		return new ResponseEntity<String>("Updated with an Id: "+id,HttpStatus.OK);
	}
	
	@DeleteMapping("/inventory/{id}")
	public ResponseEntity<?> deleteInventoryById(@PathVariable("id") int id) throws IdNotFoundException{
		service.deleteInventoryById(id);
		return new ResponseEntity<String>("Deleted successfully with an id: "+id,HttpStatus.OK);
	}

}
