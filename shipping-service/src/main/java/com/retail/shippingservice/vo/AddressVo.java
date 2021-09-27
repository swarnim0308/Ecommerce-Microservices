package com.retail.shippingservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AddressVo {
	private int addressId;
	private int doorNo;
	private String streetName;
	private String layout;
	private String city;
	private int pincode;
}
