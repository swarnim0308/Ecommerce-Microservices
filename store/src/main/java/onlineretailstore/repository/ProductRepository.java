package onlineretailstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import onlineretailstore.entity.Product;

public interface ProductRepository  extends JpaRepository<Product,Long> {
  public Optional<Product> findById(long productId);
}
