package com.cart.service;

import com.cart.domain.CartEntity;
import com.cart.domain.LineItem;
import com.cart.exceptions.CartNotFoundException;
import com.cart.repo.CartEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartEntityServiceImpl implements CartEntityService {

    @Autowired
    CartEntityRepository repo;

    @Autowired
    LineItemService ser;

    @Override
    public CartEntity getItem(int id) {
        Optional<CartEntity> cart = repo.findById(id);
        if (cart.isEmpty()) {
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
        if (cart.isEmpty()) {
            throw new CartNotFoundException("Cart you are trying to find is not available");
        }
        List<LineItem> l = cart.get().getLineItems();
        for (LineItem i : l) {
            ser.deleteLineItem(i.getId());
        }
        cart.get().setLineItems(new ArrayList<LineItem>());
        repo.save(cart.get());
    }

    @Override
    public CartEntity updateCart(int cartid, List<LineItem> lineitems) {
        Optional<CartEntity> cart = repo.findById(cartid);
        if (cart.isEmpty()) {
            throw new CartNotFoundException("Cart not found with id" + cartid);
        }
        CartEntity c = cart.get();
        List<LineItem> l = c.getLineItems();
        if (l == null) {
            c.setLineItems(lineitems);
        } else {
            l.addAll(lineitems);
        }
        return repo.save(c);
    }

    @Override
    public void deleteCartId(int cartid) {
        repo.deleteById(cartid);
    }

    @Override
    public CartEntity updateCart(int cartid, CartEntity cart) {
        CartEntity theCart = this.getItem(cartid);
        List<LineItem> lineItems = theCart.getLineItems();
        if (lineItems == null)
            lineItems = new ArrayList<LineItem>();

        lineItems.addAll(cart.getLineItems());
        theCart.setLineItems(lineItems);
        repo.save(theCart);
        return theCart;
    }

}
