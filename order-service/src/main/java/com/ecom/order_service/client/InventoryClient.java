package com.ecom.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.ecom.order_service.client.config.InventoryFeignClientConfig;
import com.ecom.order_service.config.InventoryResponse;

@FeignClient(name = "ECOM-INVENTORY-SERVICE",
        configuration = InventoryFeignClientConfig.class)
public interface InventoryClient {

    @GetMapping("/api/inventory/{productId}")
    InventoryResponse checkInventory(@PathVariable("productId") Long productId);

    @PutMapping("/api/inventory/{productId}")
    InventoryResponse updateInventory(
            @PathVariable("productId") Long productId,
            @RequestBody InventoryResponse inventory);

    @PatchMapping("/api/inventory/{productId}/reserve")
    InventoryResponse reserveInventory(
            @PathVariable("productId") Long productId,
            @RequestParam("quantity") int quantity);

    @PatchMapping("/api/inventory/{productId}/commit")
    InventoryResponse commitReservation(
            @PathVariable("productId") Long productId,
            @RequestParam("quantity") int quantity);

    @PatchMapping("/api/inventory/{productId}/release")
    InventoryResponse releaseReservation(
            @PathVariable("productId") Long productId,
            @RequestParam("quantity") int quantity);
}
