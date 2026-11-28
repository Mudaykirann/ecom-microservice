package com.ecom.order_service.client.config;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.ecom.order_service.config.InventoryResponse;

import feign.RequestTemplate;
import feign.codec.Encoder;
import tools.jackson.databind.ObjectMapper;

// Update this import to match where InventoryResponse is located in your project:
// import com.ecom.order_service.dto.InventoryResponse;

public class CustomInventoryEncoder implements Encoder {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Encoder fallbackEncoder = new Encoder.Default();

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) {
        try {
            if (object instanceof InventoryResponse inventory) {
                Map<String, Object> map = new HashMap<>();
                map.put("productId", inventory.getProductId());
                map.put("stockQuantity", inventory.getStockQuantity());
                map.put("reservedQuantity", inventory.getReservedQuantity());

                String json = objectMapper.writeValueAsString(map);
                template.body(json);
                template.header("Content-Type", "application/json");
                return;
            }
            fallbackEncoder.encode(object, bodyType, template);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode request body", e);
        }
    }
}