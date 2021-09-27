package com.order.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.order.domain.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {

}
