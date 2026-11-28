package com.ecom.order_service.dto;

import java.math.BigDecimal;
import java.util.List;

import com.ecom.order_service.model.OrderItem;
import com.ecom.order_service.model.OrderStatus;

public record OrderResponse(
        Long id,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItem> items) {
}
