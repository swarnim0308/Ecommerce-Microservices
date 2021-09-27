package com.cartentity.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cartentity.domain.CartEntity;
import com.cartentity.domain.LineItem;
import com.cartentity.exceptions.CartNotFoundException;
import com.cartentity.repo.CartEntityRepository;

@Transactional
@Service
public class CartEntityServiceImpl implements CartEntityService {

	@Autowired
	CartEntityRepository repo;
	
	@Autowired
	LineItemService ser;
	
	@Override
	public CartEntity getItem(int id) {
		Optional<CartEntity> cart = repo.findById(id);
		if(!cart.isPresent())
		{
			throw new CartNotFoundException("Cart you are trying to find is not available");
		}
		return cart.get();
	}

	@Override
	public CartEntity addCart(CartEntity cart) {
		return repo.save(cart);
	}

	@Override
	public void emptyCart(int cartid) {
		Optional<CartEntity> cart = repo.findById(cartid);
//		if(!cart.isPresent())
//		{
//			throw new CartNotFoundException("Cart you are trying to find is not available")
//		}
		List<LineItem> l=cart.get().getLineitem();
		for(LineItem i:l)
		{
			ser.deleteLineItem(i.getItemid());
		}
		cart.get().setLineitem(new ArrayList<LineItem>());
		repo.save(cart.get());
	}

	@Override
	public CartEntity updateCart(int cartid,  List<LineItem> lineitems) {
		Optional<CartEntity> cart = repo.findById(cartid);
		CartEntity c=cart.get();
		List<LineItem> l=c.getLineitem();
		if(l==null)
		{
			c.setLineitem(lineitems);
		}
		else
		{
			for(LineItem i:lineitems)
			{
				l.add(i);
			}
		}
		return repo.save(c);
	}

	@Override
	public void deleteCartId(int cartid) {
		repo.deleteById(cartid);
	}

	@Override
	public CartEntity updateCart(int cartid, CartEntity cart) {
		CartEntity theCart =  this.getItem(cartid);
		List<LineItem> lineItems = theCart.getLineitem();
		if(lineItems == null)
			lineItems = new ArrayList<LineItem>();
		
		lineItems.addAll(cart.getLineitem());
		theCart.setLineitem(lineItems);
		repo.save(theCart);
		return theCart;
	}

}
