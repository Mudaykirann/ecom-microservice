package com.ecom.order_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.retry.annotation.EnableRetry;

import com.ecom.order_service.config.RestClientConfig;

@SpringBootApplication
@Import(RestClientConfig.class)
@EnableFeignClients
@EnableRetry 
@EnableResilientMethods 
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}
