package com.codewithomkar.springapi.exceptions;

public class CartNotFoundException extends  RuntimeException {
    public CartNotFoundException() {
        super("Cart not found: ");
    }
}
