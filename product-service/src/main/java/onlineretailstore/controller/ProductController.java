package onlineretailstore.controller;

import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.RestController;

import onlineretailstore.dto.ProductDto;
import onlineretailstore.entity.Product;
import onlineretailstore.service.ProductService;


@RestController
@RefreshScope
public class ProductController {
	
	@Autowired
	private ProductService  productService;
	
	@GetMapping("/hello")
	public String sayHello() {
		return "Hello from Product-Service";
	}
	
     @GetMapping("/products")
     public List<Product> getAllProduct(){
    	 return productService.findAll();
     }
	@GetMapping("/products/{productId}")
	public Product findById(@PathVariable  Long productId)
	{
		return productService.findById(productId);
	}
	
	//localhost:9002/products
	@PostMapping("/products")
	public  ResponseEntity<Product>  addProduct(@RequestBody ProductDto productDto){
		System.err.println(productDto);
		System.err.println(productDto.getProduct().ToString());
		Product addProd = productService.addProduct(productDto.getProduct());
		return new ResponseEntity<Product>(addProd, HttpStatus.CREATED);
	
	}
	@PutMapping("/products/{productId}")
	public Product updateProduct(@PathVariable Long productId,@RequestBody Product product)
	{
		
		Optional<Product> upproduct = productService.searchProductById(productId);
		if(upproduct==null)
			return null;
		product.setProductId(upproduct.get().getProductId());
		return productService.updateProduct(product);
	}
	

	
	@DeleteMapping("/api/products/{productId}")
	public  void deleteProduct(@PathVariable Long productId)
	{

	  productService.deleteProductById(productId);
}
}
