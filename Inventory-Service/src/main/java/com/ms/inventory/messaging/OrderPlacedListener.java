package com.ms.inventory.messaging;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ms.inventory.service.InventoryService;

@Component
public class OrderPlacedListener {

	@Autowired
	private InventoryService inventoryService;

	@RabbitListener(queues = "${app.rabbitmq.queue:inventory.order.placed}")
	public void onOrderPlaced(List<OrderLineItemEvent> lineItems) {
		System.err.println("[rabbit] received order.placed with " + lineItems.size() + " line item(s)");
		for (OrderLineItemEvent line : lineItems) {
			try {
				inventoryService.decrementStock(line.getProductId(), line.getQuantity());
				System.err.println("[rabbit] decremented stock for productId " + line.getProductId()
						+ " by " + line.getQuantity());
			} catch (Exception e) {
				System.err.println("[rabbit] failed to decrement productId " + line.getProductId() + ": " + e.getMessage());
			}
		}
	}
}
