package com.ecom.order_service.services;

import java.util.concurrent.CompletableFuture;

import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.ecom.order_service.client.InventoryClient;
import com.ecom.order_service.config.InventoryResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@Service
public class InventoryService {

    private final InventoryClient inventoryClient;

    public InventoryService(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    // @Retryable(
    // includes = RuntimeException.class,
    // maxRetries = 2,
    // delay = 2000
    // )
    // @RateLimiter(
    // name = "inventoryServiceRateLimiter",
    // fallbackMethod = "getInventoryFallback"
    // )

    /*
     * @CircuitBreaker(name="inventoryServiceCircuitBreaker", fallbackMethod =
     * "getInventoryFallback")
     */
    // @TimeLimiter(name="inventoryServiceTimeLimiter", fallbackMethod =
    // "getInventoryFallback")

    public CompletableFuture<InventoryResponse> getInventory(Long productId) {
        System.out.println("Calling Inventory Service for productID: " + productId);
        return CompletableFuture.supplyAsync(() -> inventoryClient.checkInventory(productId));
    }

    public CompletableFuture<InventoryResponse> getInventoryFallback(Long productId, RequestNotPermitted ex) {
        System.out.println("Time limit hit for productID: " + productId + " - returning fallback");
        return CompletableFuture.completedFuture(new InventoryResponse(productId, 0, 0));
    }
}