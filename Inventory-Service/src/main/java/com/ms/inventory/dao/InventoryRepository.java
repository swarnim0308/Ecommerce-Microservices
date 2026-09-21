package com.ms.inventory.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ms.inventory.entity.InventoryEntity;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, Integer> {

	Optional<InventoryEntity> findByProductId(long productId);

}
