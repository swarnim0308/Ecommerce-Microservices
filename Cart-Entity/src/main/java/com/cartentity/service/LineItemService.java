package com.cartentity.service;

import com.cartentity.domain.LineItem;

public interface LineItemService {

	LineItem searchLineItem(int id);

	LineItem addLineItem(LineItem item);

	void deleteLineItem(int itemid);

	LineItem updateLineItem(int itemid, LineItem item);

}
