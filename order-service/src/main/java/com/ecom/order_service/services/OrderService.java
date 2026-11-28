package com.ecom.order_service.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.ecom.order_service.client.InventoryClient;
import com.ecom.order_service.client.ProductClient;
import com.ecom.order_service.dto.AddOrderItemRequest;
import com.ecom.order_service.dto.OrderResponse;
import com.ecom.order_service.model.Order;
import com.ecom.order_service.model.OrderItem;
import com.ecom.order_service.model.OrderStatus;
import com.ecom.order_service.repository.OrderItemRepository;
import com.ecom.order_service.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final ProductClient productClient;
    private final OrderItemRepository orderItemRepository;

    public OrderService(InventoryClient inventoryClient, ProductClient productClient,
            OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.inventoryClient = inventoryClient;
        this.productClient = productClient;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public String placeOrder(Long productId) {
        try {
            inventoryClient.reserveInventory(productId, 1);
        } catch (feign.FeignException.BadRequest ex) {
            return "Product is Out of Stock";
        }

        inventoryClient.commitReservation(productId, 1);
        return "Order Placed Successfully";
    }

    public Order createOrder() {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(BigDecimal.ZERO);
        return orderRepository.save(order);
    }

    @Transactional
    public OrderItem addOrderItem(Long orderId, AddOrderItemRequest request) {
        if (request.productId() == null) {
            throw new IllegalArgumentException("Product id is required");
        }
        if (request.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot add items to an order with status: " + order.getStatus());
        }

        var product = productClient.getProductById(request.productId());
        if (product.productPrice() == null || product.productPrice() < 0) {
            throw new IllegalStateException("Product has an invalid price: " + request.productId());
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderId);
        orderItem.setProductId(request.productId());
        orderItem.setQuantity(request.quantity());
        orderItem.setPriceAtPurchase(BigDecimal.valueOf(product.productPrice()));
        OrderItem saved = orderItemRepository.save(orderItem);
        recalculateTotalAmount(orderId);
        inventoryClient.reserveInventory(request.productId(), request.quantity());

        return saved;
    }

    @Transactional
    public String updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + orderId));

        if (newStatus == null || (newStatus != OrderStatus.COMPLETED && newStatus != OrderStatus.FAILED)) {
            throw new IllegalArgumentException("A pending order can only be completed or failed");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot change order status from " + order.getStatus());
        }

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        if (newStatus == OrderStatus.COMPLETED && items.isEmpty()) {
            throw new IllegalStateException("Cannot complete an order without items");
        }

        Map<Long, Integer> quantitiesByProduct = new HashMap<>();
        for (OrderItem item : items) {
            quantitiesByProduct.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }

        for (Map.Entry<Long, Integer> entry : quantitiesByProduct.entrySet()) {
            if (newStatus == OrderStatus.COMPLETED) {
                inventoryClient.commitReservation(entry.getKey(), entry.getValue());
            } else {
                inventoryClient.releaseReservation(entry.getKey(), entry.getValue());
            }
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
        return "Status has been changed to " + newStatus;
    }

    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + orderId));
        return new OrderResponse(order.getId(), order.getStatus(), order.getTotalAmount(),
                orderItemRepository.findByOrderId(orderId));
    }

    public void recalculateTotalAmount(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + orderId));
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        BigDecimal total = items.stream()
                .map(i -> i.getPriceAtPurchase().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);
        orderRepository.save(order);
    }
}
