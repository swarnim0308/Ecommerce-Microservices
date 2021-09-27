package com.cartentity.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cartentity.domain.CartEntity;

@Repository
public interface CartEntityRepository extends JpaRepository<CartEntity,Integer> {

}
