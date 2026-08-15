package com.codewithomkar.springapi.dtos;


import lombok.Data;

@Data
public class CheckoutResponse {
    private Long orderId;
    private String url;

    public CheckoutResponse(Long orderId,  String url) {
        this.orderId = orderId;
        this.url = url;
    }
}