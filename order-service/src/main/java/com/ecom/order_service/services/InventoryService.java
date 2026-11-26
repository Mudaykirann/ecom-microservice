package com.ecom.order_service.services;

import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.ecom.order_service.client.InventoryClient;
import com.ecom.order_service.config.InventoryResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@Service
public class InventoryService {

    private final InventoryClient inventoryClient;

    public InventoryService(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    // @Retryable(
    //     includes = RuntimeException.class,
    //     maxRetries = 2,
    //     delay = 2000
    // )
    // @RateLimiter(
    //     name = "inventoryServiceRateLimiter",
    //     fallbackMethod = "getInventoryFallback"
    // )

    @CircuitBreaker(name="inventoryServiceCircuitBreaker", fallbackMethod = "getInventoryFallback")
    public InventoryResponse getInventory(Long productId) {
        System.out.println("Calling Inventory Service for productID: " + productId);
        return inventoryClient.checkInventory(productId);
    }

    public InventoryResponse getInventoryFallback(Long productId, RequestNotPermitted ex) {
        System.out.println("Rate limit hit for productID: " + productId + " - returning fallback");
        return new InventoryResponse(productId,0);
    }
}