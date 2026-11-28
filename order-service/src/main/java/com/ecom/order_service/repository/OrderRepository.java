package com.ecom.order_service.repository;

import org.springframework.stereotype.Repository;

import com.ecom.order_service.model.Order;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
