package com.codewithomkar.springapi.mappers;



import com.codewithomkar.springapi.dtos.OrderDto;
import com.codewithomkar.springapi.entities.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);
}
