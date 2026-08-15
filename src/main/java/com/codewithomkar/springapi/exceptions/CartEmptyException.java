package com.codewithomkar.springapi.exceptions;

public class CartEmptyException extends RuntimeException {
    public CartEmptyException() {
        super("Cart is empty: ");
    }
}
