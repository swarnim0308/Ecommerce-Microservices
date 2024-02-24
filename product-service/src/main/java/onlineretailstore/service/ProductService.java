package onlineretailstore.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import onlineretailstore.entity.Product;
import onlineretailstore.exception.ProductNotFound;
import onlineretailstore.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
	public ProductRepository productRepository;

	public Product findById(Long productId) {
		return productRepository.findById(productId).orElseThrow(() -> new ProductNotFound("Product Not found"));
	}

	public List<Product> findAll() {
		return productRepository.findAll();
	}

	public Product addProduct(Product product) {
		Product addproduct = productRepository.save(product);
		return addproduct;
	}

	public Product updateProduct(Product product) {
		Product updateproduct = productRepository.save(product);
		return updateproduct;
	}

	public void deleteProductById(Long productId) {
		Product deleteProduct = productRepository.getOne(productId);
		if (deleteProduct == null) {
			throw new ProductNotFound("Oops Error!! Product with id" + productId + " not found");
		}
		this.productRepository.deleteById(productId);
	}

	public Optional<Product> searchProductById(long productId) {
		return productRepository.findById(productId);
	}

}