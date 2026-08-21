package com.ms.inventory.service;

import jakarta.persistence.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ms.inventory.dao.InventoryRepository;
import com.ms.inventory.entity.InventoryEntity;
import com.ms.inventory.exception.IdNotFoundException;

@Service
public class InventoryServiceImpl implements InventoryService {

	@Autowired
	private InventoryRepository inventoryRepository;

	@Override
	public InventoryEntity createInventory(InventoryEntity entity) {
		return inventoryRepository.save(entity);
	}

	@Override
	public InventoryEntity updateInventory(int id, InventoryEntity entity) throws IdNotFoundException {
		if (inventoryRepository.findById(id).isPresent()) {
			entity.setInventoryId(id);
			entity.setProductId(entity.getProductId());
			entity.setQuantity(entity.getQuantity());
			return inventoryRepository.save(entity);
		} else {
			throw new IdNotFoundException("ID not Found");
		}
	}

	@Override
	public InventoryEntity getInventoryById(int id) throws IdNotFoundException {
		if (inventoryRepository.findById(id).isPresent()) {
			return inventoryRepository.findById(id).get();
		} else {
			throw new IdNotFoundException("ID not Found");
		}
	}

	@Override
	public InventoryEntity decrementStock(long productId, int quantity) throws IdNotFoundException {
		InventoryEntity inventory = inventoryRepository.findByProductId(productId)
				.orElseThrow(() -> new IdNotFoundException("No inventory for productId " + productId));
		int newQty = inventory.getQuantity() - quantity;
		if (newQty < 0) {
			throw new IdNotFoundException("Insufficient stock for productId " + productId);
		}
		inventory.setQuantity(newQty);
		return inventoryRepository.save(inventory);
	}

	@Override
	public void deleteInventoryById(int id) throws IdNotFoundException {
		if (inventoryRepository.findById(id).isPresent()) {
			inventoryRepository.deleteById(id);
		} else {
			throw new IdNotFoundException("ID not Found");
		}
	}

}
