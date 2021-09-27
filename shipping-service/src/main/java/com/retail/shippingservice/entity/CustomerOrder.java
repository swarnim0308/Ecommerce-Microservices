package com.retail.shippingservice.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerOrder {
	@Id
	private Long orderID;
	private Long customerID;
	
}
