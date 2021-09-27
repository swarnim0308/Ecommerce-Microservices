package com.ms.inventory.service;

import com.ms.inventory.entity.InventoryEntity;
import com.ms.inventory.exception.IdNotFoundException;

public interface InventoryService {

	InventoryEntity createInventory(InventoryEntity entity);

	InventoryEntity updateInventory(int id, InventoryEntity entity) throws IdNotFoundException;

	InventoryEntity getInventoryById(int id) throws IdNotFoundException;

	void deleteInventoryById(int id) throws IdNotFoundException;

}
