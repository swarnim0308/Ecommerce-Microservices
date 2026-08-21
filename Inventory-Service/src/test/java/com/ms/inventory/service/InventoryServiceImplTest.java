package com.ms.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ms.inventory.dao.InventoryRepository;
import com.ms.inventory.entity.InventoryEntity;
import com.ms.inventory.exception.IdNotFoundException;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

	@Mock
	private InventoryRepository inventoryRepository;

	@InjectMocks
	private InventoryServiceImpl inventoryService;

	private InventoryEntity entity;

	@BeforeEach
	void setUp() {
		entity = new InventoryEntity();
		entity.setInventoryId(1);
		entity.setProductId(1L);
		entity.setQuantity(50);
	}

	@Test
	void decrementStock_reducesQuantity() throws Exception {
		when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(entity));
		when(inventoryRepository.save(any(InventoryEntity.class))).thenReturn(entity);

		InventoryEntity result = inventoryService.decrementStock(1L, 2);

		assertEquals(48, result.getQuantity());
	}

	@Test
	void decrementStock_throwsWhenNoInventory() {
		when(inventoryRepository.findByProductId(anyLong())).thenReturn(Optional.empty());

		assertThrows(IdNotFoundException.class, () -> inventoryService.decrementStock(1L, 2));
	}

	@Test
	void decrementStock_throwsWhenInsufficientStock() {
		when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(entity));

		assertThrows(IdNotFoundException.class, () -> inventoryService.decrementStock(1L, 60));
	}
}
