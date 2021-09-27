package com.cartentity.service;

import java.util.List;

import com.cartentity.domain.CartEntity;
import com.cartentity.domain.LineItem;

public interface CartEntityService {
	public CartEntity getItem(int cartid);

	public CartEntity addCart(CartEntity cart);

	public void emptyCart(int cartid);

	public CartEntity updateCart(int cartid, List<LineItem> lineitems);

	public void deleteCartId(int cartid);

	public CartEntity updateCart(int cartid, CartEntity cart);

}
