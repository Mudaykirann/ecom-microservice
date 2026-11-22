package com.ecom.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.ecom.order_service.client.config.InventoryFeignClientConfig;
import com.ecom.order_service.config.InventoryResponse;

@FeignClient(name = "ECOM-INVENTORY-SERVICE",
        configuration = InventoryFeignClientConfig.class)
public interface InventoryClient {

    @GetMapping("/inventory/{productId}")
    InventoryResponse checkInventory(@PathVariable Long productId);

    @PostMapping("/inventory")
    public String updateInventory(@RequestBody InventoryResponse inventory);
}
