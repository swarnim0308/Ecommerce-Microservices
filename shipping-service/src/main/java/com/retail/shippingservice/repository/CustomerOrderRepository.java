package com.retail.shippingservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retail.shippingservice.entity.CustomerOrder;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long>{
	List<CustomerOrder> findByCustomerID(Long customerID);
}
