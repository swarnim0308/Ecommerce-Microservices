package com.retail.shippingservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.retail.shippingservice.vo.InventoryVo;
import com.retail.shippingservice.vo.ProductInventoryVo;
import com.retail.shippingservice.vo.ProductVo;

@RestController
@RequestMapping("/products")
public class ProductController {
	
	@Autowired
	private EurekaClient client;
	@Autowired
	private RestTemplateBuilder templateBuilder;
	
	@GetMapping("/say")
	public String hello() {
		return "Hello from composite Service";
	}
	
	//This API is used by external client to create a product
	@PostMapping("/")
	public ResponseEntity<?> createProduct(@RequestBody ProductVo productVo){
		
		RestTemplate template = templateBuilder.build();
		
		InstanceInfo productInstanceInfo = client.getNextServerFromEureka("Product-Service", false);
		String productBaseUrl = productInstanceInfo.getHomePageUrl(); // http://localhost:9002/
		String productCreateUrl = productBaseUrl + "products";// http://localhost:9002/products
		System.err.println(productCreateUrl);
		
		ResponseEntity<ProductVo> productResponse = template.postForEntity(productCreateUrl, productVo, ProductVo.class);
		
		int quantity = productVo.getQuantity();
		long productID = productResponse.getBody().getProductId();
		
		InventoryVo inventoryVo = new InventoryVo(productID, quantity);
		
		InstanceInfo inventoryInstanceInfo = client.getNextServerFromEureka("Inventory-Service", false);
		String inventoryBaseUrl = inventoryInstanceInfo.getHomePageUrl(); //http://localhost:9003/
		String createInventoryUrl = inventoryBaseUrl+"api/inventory"; //http://localhost:9003/api/inventory
		System.err.println(createInventoryUrl);
		
		ResponseEntity<InventoryVo> inventoryResponse = template.postForEntity(createInventoryUrl, inventoryVo, InventoryVo.class);
		
		ProductVo newProduct = productResponse.getBody();
		
		InventoryVo newInventory = inventoryResponse.getBody();
		
		newProduct.setQuantity(newInventory.getQuantity()); 
		
		return new ResponseEntity<ProductInventoryVo>(new ProductInventoryVo(newProduct, newInventory), HttpStatus.CREATED);
	}
}

