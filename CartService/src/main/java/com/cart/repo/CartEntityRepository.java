package com.cart.repo;

import com.cart.domain.CartEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartEntityRepository extends MongoRepository<CartEntity,Integer> {

}
