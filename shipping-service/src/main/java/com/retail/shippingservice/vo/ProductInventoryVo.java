package com.retail.shippingservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductInventoryVo {
	private ProductVo product;
	private InventoryVo inventory;
}
