package com.cart.repo;

import com.cart.domain.LineItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineItemRepository extends MongoRepository<LineItem,Integer> {

}
