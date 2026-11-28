# E-Commerce Microservices

A Java/Spring-based e-commerce backend organized as independently runnable microservices. The repository includes an API gateway, Eureka service discovery, and separate product, inventory, and order services. Services use H2 for their configured databases, and OpenTelemetry/Zipkin configuration is included for observability.

## Architecture

| Component | Purpose | Default port |
|---|---|---:|
| `api-gateway` | Routes client requests to services using Eureka service discovery | 9090 |
| `eureka-server` | Service registry | 8761 |
| `product-service` | Product API and data | 8082 |
| `inventory-service` | Inventory API and data | 8081 |
| `order-service` | Order API and orchestration | 8080 |
| `otel-collectors` | OpenTelemetry Collector and Zipkin configuration | 4317, 4318, 9411 |

The gateway routes configured in `api-gateway/src/main/resources/application.yaml` include:

- `/product` and `/product/{id}` → product service
- `/inventory` and `/inventory/{id}` → inventory service
- `/inventory/{id}/reserve`, `/commit`, and `/release` → inventory service
- `/order/{productId}` and `/api/v1/orders/**` → order service

## Technology

- Java 21
- Spring Boot 4.2.0
- Spring Cloud 2026.0.0
- Maven (a Maven Wrapper is included in each service)
- Spring Cloud Gateway and Netflix Eureka
- Spring Data JPA with in-memory H2 databases
- OpenTelemetry; Zipkin for trace visualization

## Prerequisites

- JDK 21
- Docker and Docker Compose (optional, for the OpenTelemetry Collector and Zipkin)

## Run locally

Run each command from the repository root in a separate terminal. Start Eureka first so the other services can register with it.

```bash
cd eureka-server
./mvnw spring-boot:run
```

Then start the business services and gateway:

```bash
cd product-service
./mvnw spring-boot:run
```

```bash
cd inventory-service
./mvnw spring-boot:run
```

```bash
cd order-service
./mvnw spring-boot:run
```

```bash
cd api-gateway
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd` instead of `./mvnw`. The Eureka dashboard is available at [http://localhost:8761](http://localhost:8761), and the gateway listens at [http://localhost:9090](http://localhost:9090).

## Observability (optional)

The `otel-collectors` directory provides a Compose setup for an OpenTelemetry Collector and Zipkin. Start them with:

```bash
cd otel-collectors
docker compose up -d
```

Zipkin's UI is available at [http://localhost:9411](http://localhost:9411). The collector accepts OTLP over gRPC on port `4317` and HTTP on port `4318`. Ensure each service's configured OTLP endpoint is reachable from where that service runs; adjust hostnames if running services and the collector in different Docker/network environments.

## Configuration notes

- Each service's settings are in its `src/main/resources/application.yaml`.
- Product, inventory, and order services use separate in-memory H2 databases. Data is not persisted after a service stops.
- The H2 console is enabled in the configured data services at `/h2-console` on each service's port.
- Eureka's default URL is `http://localhost:8761/eureka`.
- Dockerfiles are included for the gateway and application services; the collector Compose file only defines the collector and Zipkin.

## Build and test

Build or test a service from its module directory:

```bash
./mvnw test
./mvnw package
```

Repeat for each module (`api-gateway`, `eureka-server`, `product-service`, `inventory-service`, and `order-service`).
