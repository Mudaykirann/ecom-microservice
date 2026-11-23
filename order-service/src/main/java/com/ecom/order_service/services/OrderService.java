package com.ecom.order_service.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import com.ecom.order_service.client.InventoryClient;
import com.ecom.order_service.config.InventoryResponse;
import java.lang.*;

@Service
public class OrderService {

    private final RestTemplate restTemplate;

    private final RestClient restClient;
    private final InventoryClient inventoryClient;

    public OrderService(RestTemplate restTemplate, RestClient restClient, InventoryClient inventoryClient) {
        this.restTemplate = restTemplate;
        this.restClient = restClient;
        this.inventoryClient = inventoryClient;
    }

    public String placeOrder(Long productId) {

        // String response = restTemplate.getForObject(
        // "http://localhost:8081/inventory/" + productId, String.class);

        // ResponseEntity<InventoryResponse> response = restClient.get()
        // .uri("http://localhost:8081/inventory/{productId}", productId)
        // .retrieve()
        // .onStatus(HttpStatusCode::is4xxClientError, ((req, res) -> {
        // throw new GenericExceptions(res.getStatusCode(), res.getHeaders());
        // }))
        // .toEntity(InventoryResponse.class);

        InventoryResponse inventoryResponse = inventoryClient.checkInventory(productId);

        updateInventory(inventoryResponse);
        return inventoryResponse.getQuantity() > 0 ? "Order Placed Successfully"
                : "Product is Out of Stock";

    }

    private void updateInventory(InventoryResponse inventory) {
        inventory.setQuantity(inventory.getQuantity() - 1);
        inventoryClient.updateInventory(inventory);

        // restClient.post().uri("http://localhost:8081/inventory")
        // .body(inventory)
        // .retrieve()
        // .toBodilessEntity();
    }
}
