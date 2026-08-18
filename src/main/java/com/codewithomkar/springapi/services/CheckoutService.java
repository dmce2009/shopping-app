package com.codewithomkar.springapi.services;

import com.codewithomkar.springapi.dtos.CheckoutRequest;
import com.codewithomkar.springapi.dtos.CheckoutResponse;
import com.codewithomkar.springapi.entities.Order;
import com.codewithomkar.springapi.exceptions.CartEmptyException;
import com.codewithomkar.springapi.exceptions.CartNotFoundException;
import com.codewithomkar.springapi.exceptions.PaymentException;
import com.codewithomkar.springapi.repositories.CartRepository;
import com.codewithomkar.springapi.repositories.OrderRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final CartService cartService;
    private final PaymentGateway paymentGateway;

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request) throws StripeException {
        var cartEntity = cartRepository.getCartWithItems(request.getCartId()).orElse(null);
        if (cartEntity == null) {
            throw new CartNotFoundException();
        }

        if (cartEntity.isEmpty()) {
            throw new CartEmptyException();
        }

        var order = Order.fromCart(cartEntity, authService.getCurrentUser());
        orderRepository.save(order);

        try {

            var session = paymentGateway.createCheckoutSession(order);
            cartService.clearCart(cartEntity.getId());
            return new CheckoutResponse(order.getId(), session.getCheckoutUrl());
        } catch (PaymentException ex) {
            orderRepository.delete(order);
            throw ex;
        }

    }
}
