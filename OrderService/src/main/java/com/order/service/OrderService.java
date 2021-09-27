package com.order.service;

import java.util.List;

import com.order.domain.Order;
import com.order.domain.LineItem;
import com.order.exceptions.OrderNotFoundException;

public interface OrderService {

	public Order addOrder(Order order);

	public Order updateOrder(int orderid, List<LineItem> lineitems) throws OrderNotFoundException;

	Order searchOrder(int id) throws OrderNotFoundException;

	void deleteOrder(int orderid);
	
	public void emptyOrder(int orderid) throws OrderNotFoundException;

}
