package com.retail.shippingservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InventoryVo {
	private int inventoryId;
	private long productId;
	private int quantity;
	

	public InventoryVo(long productID, int quantity) {
		this.productId = productID;
		this.quantity = quantity;
	}

}
