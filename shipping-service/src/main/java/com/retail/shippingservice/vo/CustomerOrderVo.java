package com.retail.shippingservice.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrderVo {
	private CustomerVo customer;
	private List<OrderVo> orders;
}
