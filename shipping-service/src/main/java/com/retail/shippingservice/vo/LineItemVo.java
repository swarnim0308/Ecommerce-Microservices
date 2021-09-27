package com.retail.shippingservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LineItemVo {
	private int itemid;
	private int productId;
	private String productName;
	private int quantity;
}
