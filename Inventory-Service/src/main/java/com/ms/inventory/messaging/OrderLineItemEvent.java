package com.ms.inventory.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineItemEvent {
	private int productId;
	private int quantity;
}
