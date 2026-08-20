package onlineretailstore;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import onlineretailstore.entity.Product;
import onlineretailstore.repository.ProductRepository;

@Configuration
public class DataSeeder {

	@Bean
	CommandLineRunner seedProducts(ProductRepository repository) {
		return args -> {
			if (repository.count() > 0) {
				System.err.println("[seed] Products already present, skipping.");
				return;
			}
			repository.save(new Product(null, "Wireless Mouse", "Ergonomic 2.4GHz wireless mouse", 1999.0));
			repository.save(new Product(null, "Mechanical Keyboard", "RGB mechanical keyboard with blue switches", 7999.0));
			repository.save(new Product(null, "27in Monitor", "QHD 144Hz IPS gaming monitor", 24999.0));
			repository.save(new Product(null, "USB-C Hub", "7-in-1 USB-C multiport adapter", 2999.0));
			System.err.println("[seed] 4 products seeded.");
		};
	}
}
