package com.codewithomkar.springapi.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class ProductDTO {

    Long id;

    @NotBlank(message = "Name is required")
    @Size(message = "Name must be less than 255 characters")
    String name;

    String description;
    double price;
    Byte categoryId;
}
