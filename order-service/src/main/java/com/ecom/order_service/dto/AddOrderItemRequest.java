package com.ecom.order_service.dto;

public record AddOrderItemRequest(Long productId, int quantity) {
}
