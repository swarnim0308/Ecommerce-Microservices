package com.cartentity.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cartentity.domain.LineItem;
import com.cartentity.repo.LineItemRepository;

@Transactional
@Service
public class LineItemServiceImpl implements LineItemService {

	@Autowired
	LineItemRepository repo;

	@Override
	public LineItem searchLineItem(int id) {
		Optional<LineItem> item = repo.findById(id);
//		if(!item.isPresent())
//		{
//			throw new itemNotFoundException("item you are trying to find is not available")
//		}
		return item.get();
	}

	@Override
	public LineItem addLineItem(LineItem item) {
		return repo.save(item);
	}

	@Override
	public void deleteLineItem(int itemid) {
		Optional<LineItem> item = repo.findById(itemid);
//		if(!item.isPresent())
//		{
//			throw new itemNotFoundException("item you are trying to find is not available")
//		}
		repo.deleteById(itemid);
	}

	@Override
	public LineItem updateLineItem(int itemid, LineItem item) {
		return repo.save(item);
	}

}

