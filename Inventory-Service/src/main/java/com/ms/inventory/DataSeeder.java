package com.ms.inventory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ms.inventory.dao.InventoryRepository;
import com.ms.inventory.entity.InventoryEntity;

@Configuration
public class DataSeeder {

	@Bean
	CommandLineRunner seedInventory(InventoryRepository repository) {
		return args -> {
			if (repository.count() > 0) {
				System.err.println("[seed] Inventory already present, skipping.");
				return;
			}
			repository.save(new InventoryEntity(0, 1L, 50));
			repository.save(new InventoryEntity(0, 2L, 30));
			repository.save(new InventoryEntity(0, 3L, 15));
			repository.save(new InventoryEntity(0, 4L, 100));
			System.err.println("[seed] 4 inventory records seeded.");
		};
	}
}
