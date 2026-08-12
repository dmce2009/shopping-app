package com.codewithomkar.springapi.mappers;

import com.codewithomkar.springapi.dtos.CartDto;
import com.codewithomkar.springapi.dtos.CartItemDto;
import com.codewithomkar.springapi.entities.Cart;
import com.codewithomkar.springapi.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    CartDto toCartDto(Cart cart);

    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    CartItemDto toDto(CartItem cartItem);
}
