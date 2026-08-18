package com.codewithomkar.springapi.services;

import com.codewithomkar.springapi.entities.Order;

public interface PaymentGateway {
    CheckoutSession createCheckoutSession(Order order);
}
