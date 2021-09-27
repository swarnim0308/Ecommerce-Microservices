package com.retail.shippingservice.vo;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CustomerVo {
	private int customerId;
	private String customerName;
	private String customerEmail;
	private AddressVo customerBillingAddress;
	private AddressVo customerShippingAddress;
}
