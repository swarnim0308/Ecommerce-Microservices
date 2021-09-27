package com.order.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.order.domain.LineItem;

@Repository
public interface LineItemRepository extends JpaRepository<LineItem,Integer> {

}
