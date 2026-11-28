package com.ecom.inventory_service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecom.inventory_service.client.ProductClient;
import com.ecom.inventory_service.model.Inventory;
import com.ecom.inventory_service.repository.InventoryRepository;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void reserveStockIncreasesReservedQuantityOnlyWhenAvailable() {
        Inventory inventory = new Inventory();
        inventory.setProductId(42L);
        inventory.setStockQuantity(10);
        inventory.setReservedQuantity(3);
        when(inventoryRepository.findByProductId(42L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(inventory)).thenReturn(inventory);

        Inventory result = inventoryService.reserveStock(42L, 2);

        assertEquals(10, result.getStockQuantity());
        assertEquals(5, result.getReservedQuantity());
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void reserveStockRejectsQuantityAboveAvailableStock() {
        Inventory inventory = new Inventory();
        inventory.setProductId(42L);
        inventory.setStockQuantity(5);
        inventory.setReservedQuantity(4);
        when(inventoryRepository.findByProductId(42L)).thenReturn(Optional.of(inventory));

        assertThrows(IllegalArgumentException.class, () -> inventoryService.reserveStock(42L, 2));

        verify(inventoryRepository, never()).save(inventory);
    }
}
