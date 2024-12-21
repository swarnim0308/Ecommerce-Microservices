package com.cart.service;

import com.cart.domain.LineItem;
import com.cart.exceptions.ItemNotFoundException;
import com.cart.repo.LineItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class LineItemServiceImpl implements LineItemService {

	@Autowired
	LineItemRepository repo;

	@Override
	public LineItem searchLineItem(int id) {
		Optional<LineItem> item = repo.findById(id);
		if(item.isEmpty())
		{
            try {
                throw new ItemNotFoundException("item you are trying to find is not available");
            } catch (ItemNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
		return item.get();
	}

	@Override
	public LineItem addLineItem(LineItem item) {
		return repo.save(item);
	}

	@Override
	public void deleteLineItem(String itemid) {
		Optional<LineItem> item = repo.findById(Integer.valueOf(itemid));
		if(item.isEmpty())
		{
            try {
                throw new ItemNotFoundException("item you are trying to find is not available");
            } catch (ItemNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
		repo.deleteById(Integer.valueOf(itemid));
	}

	@Override
	public LineItem updateLineItem(int itemid, LineItem item) {
		return repo.save(item);
	}

}

