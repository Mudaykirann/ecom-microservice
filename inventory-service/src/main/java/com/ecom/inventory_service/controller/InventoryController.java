package com.ecom.inventory_service.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.inventory_service.model.Inventory;
import com.ecom.inventory_service.services.InventoryService;

import java.lang.Thread;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{productId}")
    public CompletableFuture<Inventory> checkInventory(@PathVariable Long productId) {
        return CompletableFuture.supplyAsync(() -> inventoryService.checkStock(productId));
    }

    @PostMapping
    public Inventory create(@RequestBody Inventory inventory) {
        return inventoryService.create(inventory);
    }

    @PutMapping("/{productId}")
    public Inventory update(@PathVariable Long productId, @RequestBody Inventory inventory) {
        return inventoryService.updateByProductId(productId, inventory);
    }

    @PatchMapping("/{productId}/reserve")
    public Inventory reserve(
            @PathVariable Long productId,
            @RequestParam("quantity") int quantity) {
        return inventoryService.reserveStock(productId, quantity);
    }

    @PatchMapping("/{productId}/commit")
    public Inventory commitReservation(
            @PathVariable Long productId,
            @RequestParam("quantity") int quantity) {
        return inventoryService.commitReservation(productId, quantity);
    }

    @PatchMapping("/{productId}/release")
    public Inventory releaseReservation(
            @PathVariable Long productId,
            @RequestParam("quantity") int quantity) {
        return inventoryService.releaseReservation(productId, quantity);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return inventoryService.delete(id);
    }
}
