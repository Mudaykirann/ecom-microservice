package com.ecom.inventory_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.inventory_service.model.Inventory;
import com.ecom.inventory_service.services.InventoryService;

import java.lang.Thread;
import java.util.concurrent.CompletableFuture;

@RestController 
@RequestMapping("/inventory")
public class InventoryController {
    
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{productId}")
    public CompletableFuture<Inventory> checkInventory(@PathVariable Long productId){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return CompletableFuture.supplyAsync(()-> inventoryService.checkStock(productId));
    }
    

    @PostMapping
    public String addProduct(@RequestBody Inventory inventory){
        return inventoryService.addProduct(inventory);
    }

    @PutMapping
    public String updateProduct(@RequestBody  Inventory inventory){
        return inventoryService.updateProduct(inventory);
    }
}
