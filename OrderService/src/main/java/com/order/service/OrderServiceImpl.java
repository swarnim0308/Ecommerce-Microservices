package com.order.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.order.domain.LineItem;
import com.order.domain.Order;
import com.order.exceptions.OrderNotFoundException;
import com.order.repo.LineItemRepository;
import com.order.repo.OrderRepository;

@Transactional
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	OrderRepository repo;

	@Autowired
	LineItemService ser;
	
	@Autowired
	LineItemRepository lineItemRepo;

	@Override
	public Order searchOrder(int id) throws OrderNotFoundException {
		Optional<Order> order = repo.findById(id);
		if (!order.isPresent()) {
			throw new OrderNotFoundException("Order Not Found");
		}
		return order.get();
	}

	@Override
	public Order addOrder(Order order) {
		Order theOrder = repo.save(new Order());
		theOrder.setLineitem(order.getLineitem());
		repo.save(theOrder);
		return theOrder;
	}

	@Override
	public void emptyOrder(int orderid) throws OrderNotFoundException {
		Optional<Order> order = repo.findById(orderid);
		if (!order.isPresent()) {
			throw new OrderNotFoundException("Order Not Found");
		}
		List<LineItem> l = order.get().getLineitem();
		for (LineItem i : l) {
			ser.deleteLineItem(i.getItemid());
		}
		order.get().setLineitem(new ArrayList<LineItem>());
		repo.save(order.get());
	}

	@Override
	public Order updateOrder(int orderid, List<LineItem> lineitems) throws OrderNotFoundException {
		Optional<Order> order = repo.findById(orderid);
		if (!order.isPresent()) {
			throw new OrderNotFoundException("Order Not Found");
		}
		Order c = order.get();
		List<LineItem> l = c.getLineitem();
		if (l == null) {
			c.setLineitem(lineitems);
		} else {
			for (LineItem i : lineitems) {
				l.add(i);
			}
		}
		return repo.save(c);
	}

	@Override
	public void deleteOrder(int orderid) {
		Optional<Order> order = repo.findById(orderid);
		if (!order.isPresent()) {
			throw new OrderNotFoundException("Order Not Found");
		}
		repo.deleteById(orderid);
	}

}
