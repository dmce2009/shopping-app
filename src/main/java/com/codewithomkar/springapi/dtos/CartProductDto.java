package com.codewithomkar.springapi.dtos;

import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class CartProductDto  {
    private Long id;
    private String name;
    private BigDecimal price;
}