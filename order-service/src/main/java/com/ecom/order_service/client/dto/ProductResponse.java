package com.ecom.order_service.client.dto;

public record ProductResponse(
        Long productId,
        String productName,
        String productDescription,
        Integer productPrice) {
}
