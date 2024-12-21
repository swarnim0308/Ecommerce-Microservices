package com.cart.exceptions;

import java.io.Serial;

public class ItemNotFoundException extends Throwable {
    @Serial
    private static final long serialVersionUID = 1L;

    public ItemNotFoundException(String message) {
        super(message);
    }
}
