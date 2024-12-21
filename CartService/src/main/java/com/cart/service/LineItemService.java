package com.cart.service;

import com.cart.domain.LineItem;

public interface LineItemService {

	LineItem searchLineItem(int id);

	LineItem addLineItem(LineItem item);

	void deleteLineItem(String itemid);

	LineItem updateLineItem(int itemid, LineItem item);

}
