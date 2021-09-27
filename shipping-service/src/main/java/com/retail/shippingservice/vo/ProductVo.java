package com.retail.shippingservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductVo {
	private Long productId;
	private String productName;
	private String productDescription;
	private double productPrice;
	private int quantity;
}
