package com.ecom.inventory_service.services;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import feign.FeignException;

import com.ecom.inventory_service.client.ProductClient;
import com.ecom.inventory_service.model.Inventory;
import com.ecom.inventory_service.repository.InventoryRepository;

import jakarta.transaction.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductClient productClient;

    public InventoryService(InventoryRepository inventoryRepository, ProductClient productClient) {
        this.inventoryRepository = inventoryRepository;
        this.productClient = productClient;
    }

    public String checkInventory(Long productId) {
        if (productId == null) {
            return "OUT OF STOCK";
        }
        return inventoryRepository.findByProductId(productId)
                .map(inv -> {
                    int ava = inv.getStockQuantity() - inv.getReservedQuantity();
                    return ava > 0 ? "IN STOCK" : "OUT OF STOCK";
                })
                .orElse("OUT OF STOCK");
    }

    public Inventory checkStock(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new NoSuchElementException("Inventory not found for productId: " + productId));
    }

    @Transactional
    public Inventory create(Inventory inventory) {
        if (inventory.getProductId() == null) {
            throw new IllegalArgumentException("Product id is required");
        }
        if (inventoryRepository.existsByProductId(inventory.getProductId())) {
            throw new IllegalArgumentException("Inventory already exists for product id: " + inventory.getProductId());
        }
        validateQuantities(inventory.getStockQuantity(), inventory.getReservedQuantity());
        try {
            productClient.getProductById(inventory.getProductId());
        } catch (FeignException.NotFound ex) {
            throw new NoSuchElementException("Product not found with id: " + inventory.getProductId());
        }
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public Inventory updateByProductId(Long productId, Inventory updated) {
        validateQuantities(updated.getStockQuantity(), updated.getReservedQuantity());

        Inventory existing = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new NoSuchElementException("Inventory not found for productId: " + productId));

        existing.setStockQuantity(updated.getStockQuantity());
        existing.setReservedQuantity(updated.getReservedQuantity());
        return inventoryRepository.save(existing);
    }

    @Transactional
    public Inventory reserveStock(Long productId, int quantity) {
        validateReservationQuantity(quantity);
        Inventory inventory = findInventory(productId);
        int available = inventory.getStockQuantity() - inventory.getReservedQuantity();
        if (available < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + available);
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public Inventory commitReservation(Long productId, int quantity) {
        validateReservationQuantity(quantity);
        Inventory inventory = findInventory(productId);
        validateReservedQuantity(inventory, quantity);

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventory.setStockQuantity(inventory.getStockQuantity() - quantity);
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public Inventory releaseReservation(Long productId, int quantity) {
        validateReservationQuantity(quantity);
        Inventory inventory = findInventory(productId);
        validateReservedQuantity(inventory, quantity);

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        return inventoryRepository.save(inventory);
    }

    public boolean delete(Long id) {
        if (inventoryRepository.existsById(id)) {
            inventoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void validateQuantities(int stockQuantity, int reservedQuantity) {
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        if (reservedQuantity < 0) {
            throw new IllegalArgumentException("Reserved quantity cannot be negative");
        }
        if (reservedQuantity > stockQuantity) {
            throw new IllegalArgumentException("Reserved quantity cannot exceed stock quantity");
        }
    }

    private Inventory findInventory(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new NoSuchElementException("Inventory not found for productId: " + productId));
    }

    private void validateReservationQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Reservation quantity must be greater than zero");
        }
    }

    private void validateReservedQuantity(Inventory inventory, int quantity) {
        if (inventory.getReservedQuantity() < quantity) {
            throw new IllegalArgumentException("Cannot adjust more reserved stock than currently reserved");
        }
    }
}
