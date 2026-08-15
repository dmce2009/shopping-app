package com.codewithomkar.springapi.services;

import com.codewithomkar.springapi.dtos.CartDto;
import com.codewithomkar.springapi.dtos.CartItemDto;
import com.codewithomkar.springapi.dtos.UpdateCartItemRequest;
import com.codewithomkar.springapi.entities.Cart;
import com.codewithomkar.springapi.exceptions.CartNotFoundException;
import com.codewithomkar.springapi.exceptions.ProductNotFoundException;
import com.codewithomkar.springapi.mappers.CartMapper;
import com.codewithomkar.springapi.repositories.CartRepository;
import com.codewithomkar.springapi.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;

    private final CartMapper cartMapper;

    private final ProductRepository productRepository;

    public CartDto createCart() {
        Cart cart = new Cart();
        cartRepository.save(cart);
        return cartMapper.toCartDto(cart);
    }

    public CartItemDto addToCart(UUID cartId, Long productId) {

        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        var product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new ProductNotFoundException();
        }

        var cartItem = cart.addItem(product);
        cartRepository.save(cart);
        return cartMapper.toDto(cartItem);
    }

    public CartDto getCart(UUID cartId){
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        return cartMapper.toCartDto(cart);

    }

    public CartItemDto updateCart(UUID cartId, Long productId, UpdateCartItemRequest updateCartItemRequest) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        var cartItem = cart.getItem(productId);

        if (cartItem == null) {
            throw new ProductNotFoundException();
        }
        cartItem.setQuantity(updateCartItemRequest.getQuantity());
        cartRepository.save(cart);

        return cartMapper.toDto(cartItem);
    }

    public void removeCartItem(UUID cartId, Long productId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
           throw new CartNotFoundException();
        }
        cart.removeItem(productId);
        cartRepository.save(cart);
    }

    public void clearCart(UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        cart.clear();
        cartRepository.save(cart);
    }
}
