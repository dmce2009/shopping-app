package com.codewithomkar.springapi.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddItemtoCartRequest {
    @NotBlank(message = "productId is required")
    private Long productId;
}
