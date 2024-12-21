package com.cart.service;

import com.cart.domain.CartEntity;
import com.cart.domain.LineItem;

import java.util.List;

public interface CartEntityService {
	public CartEntity getItem(int cartid);

	public CartEntity addCart(CartEntity cart);

	public void emptyCart(int cartid);

	public CartEntity updateCart(int cartid, List<LineItem> lineitems);

	public void deleteCartId(int cartid);

	public CartEntity updateCart(int cartid, CartEntity cart);

}
