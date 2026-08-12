package com.codewithomkar.springapi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "date_created", insertable = false, updatable = false)
    private LocalDate dateCreated;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.MERGE, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<CartItem> items = new LinkedHashSet<>();

    public BigDecimal getTotalPrice() {
        return items.stream().map(CartItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public CartItem getItem(Long productId) {
        return this.getItems().stream().filter(item -> item.getProduct().getId().equals(productId)).findFirst().orElse(null);

    }

    public void removeItem(Long productId) {
        CartItem cartItem = this.getItem(productId);
        if(cartItem != null) {
            items.remove(cartItem);
            cartItem.setCart(null);
        }

    }

    public CartItem addItem(Product product) {
        var cartItem = this.getItem(product.getId());
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cartItem.setCart(this);
            items.add(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        }
        return cartItem;
    }


    public void clear() {
        items.clear();
    }
}