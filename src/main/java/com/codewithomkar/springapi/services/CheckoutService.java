package com.codewithomkar.springapi.services;

import com.codewithomkar.springapi.dtos.CheckoutRequest;
import com.codewithomkar.springapi.dtos.CheckoutResponse;
import com.codewithomkar.springapi.entities.Order;
import com.codewithomkar.springapi.exceptions.CartEmptyException;
import com.codewithomkar.springapi.exceptions.CartNotFoundException;
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

    @Value("${websiteUrl}")
    private String websiteUrl;

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
            // Create a checkout session
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl + "/checkout-success?orderId=" + order.getId())
                    .setCancelUrl(websiteUrl + "/checkout-cancel");
            order.getItems().forEach(item -> {
                var lineItem = SessionCreateParams.LineItem.builder()
                        .setQuantity(Long.valueOf(item.getQuantity()))
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("usd")
                                        .setUnitAmountDecimal(
                                                item.getUnitPrice()
                                                        .multiply(BigDecimal.valueOf(100)))
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName(item.getProduct().getName())
                                                        .build()
                                        )
                                        .build()
                        ).build();
                builder.addLineItem(lineItem);
            });

            var session = Session.create(builder.build());
            cartService.clearCart(cartEntity.getId());
            return new CheckoutResponse(order.getId(), session.getUrl());
        } catch (StripeException ex) {
            System.out.println(ex.getMessage());
            orderRepository.delete(order);
            throw ex;
        }

    }
}
