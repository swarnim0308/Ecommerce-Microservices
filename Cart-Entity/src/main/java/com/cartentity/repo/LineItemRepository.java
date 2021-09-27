package com.cartentity.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cartentity.domain.LineItem;

@Repository
public interface LineItemRepository extends JpaRepository<LineItem,Integer> {

}
