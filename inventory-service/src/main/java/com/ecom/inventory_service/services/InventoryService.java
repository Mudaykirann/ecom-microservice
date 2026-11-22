package com.ecom.inventory_service.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecom.inventory_service.model.Inventory;
import com.ecom.inventory_service.repository.InventoryRepository;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public String checkInventory(String productId) {
        System.out.println("Checking Inventory for ProductID: " + productId);
        return productId.equals("1") ? "IN STOCK" : "OUT OF STOCK";
    }

    public Inventory checkStock(Long productId) {
        Optional<Inventory> inv = inventoryRepository.findById(productId);
        return inv.get();
    }

    public String addProduct(Inventory inventory) {
        inventoryRepository.save(inventory);
        return "Product Added";
    }

    public String updateProduct(Inventory inventory) {
        if (inventory.getProductId() == null) {
            return "Product ID is required for update";
        }

        return inventoryRepository.findById(inventory.getProductId())
                .map(existing -> {
                    // Update relevant fields on `existing` before saving, e.g.:
                    existing.setQuantity(inventory.getQuantity());
                    existing.setProductId(inventory.getProductId());
                    inventoryRepository.save(inventory);
                    return "Product Updated";
                })
                .orElse("Product Not Found");
    }
}
