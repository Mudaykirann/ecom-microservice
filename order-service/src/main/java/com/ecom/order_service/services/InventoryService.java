package com.ecom.order_service.services;

import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.ecom.order_service.client.InventoryClient;
import com.ecom.order_service.config.InventoryResponse;

@Service
public class InventoryService {

    private final InventoryClient inventoryClient;

    public InventoryService(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    @Retryable(
        includes = RuntimeException.class,
        maxRetries = 2,
        delay = 2000
    )
    public InventoryResponse getInventory(Long productId) {
        System.out.println("Calling Inventory Service for productID: " + productId);
        return inventoryClient.checkInventory(productId);
    }
}