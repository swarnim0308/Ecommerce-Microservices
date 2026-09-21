# High-Level Design (HLD) — E-Commerce Microservices Platform

## 1. Overview

A distributed e-commerce platform built as a set of Spring Boot microservices, fronted
by an API Gateway and backed by shared infrastructure (service discovery, central
config, a relational database, a message broker, a cache, and distributed tracing).
A vanilla-JS/Vite storefront consumes the gateway.

## 2. System Context (top level)

```
                 +------------------+
                 |   Storefront UI   |
                 |   (Vite / SPA)    |
                 +---------+--------+
                           | HTTP (REST)  /api/*
                           v
                 +------------------+      eureka / config      +---------------------+
                 |  API Gateway     |-------------------------->| Service Registry     |
                 |  (Zuul :8080)    |      service discovery    | (Eureka :8761)       |
                 +---------+--------+                           +----------+----------+
                           |                                                |
           routes /api/<service>/**                                       | bootstrap config
                           |                                                v
   +-------+-------+-------+-------+-------+-------+          +---------------------+
   | Product| Inventory| Order| Cart| Customer|Shipping|      |  Config Server       |
   |  :9002 |  :9003   | :9004| :9005| :9006  | :9006  |      |  (:8888)             |
   +---+----+----+-----+------+-----+---+----+-----+----+      +----------+----------+
       |         |           |          |         |                       |
       +---------+-----------+----------+---------+                       |
                 |                       |                                |
                 v                       v                                v
      +--------------------+    +------------------+         +-------------------------+
      |  PostgreSQL (:5432) |    |  RabbitMQ (:5672)|         |   Redis (:6379)         |
      |  shared DB          |    |  order-placed evt|         |   product catalog cache |
      +--------------------+    +------------------+         +-------------------------+
                                                        +---------------------------+
                                                        |   Zipkin (:9411)          |
                                                        |   distributed tracing     |
                                                        +---------------------------+
```

## 3. Core building blocks

| Component          | Responsibility                                                      | Port |
|--------------------|---------------------------------------------------------------------|------|
| API Gateway (Zuul) | Single entry point, routes `/api/<service>/**`, JWT validation filter | 8080 |
| Service Registry   | Eureka — service discovery & registration                           | 8761 |
| Config Server      | Central external configuration (native files)                        | 8888 |
| Product-Service    | Product catalog CRUD (Redis-cached reads)                            | 9002 |
| Inventory-Service  | Stock levels; consumes `order-placed` event to decrement             | 9003 |
| Order-Service      | Order creation / lookup                                              | 9004 |
| Cart-Service       | Shopping cart per customer                                           | 9005 |
| Customer-Service   | Customers + auth (JWT/BCrypt)                                        | 9001 |
| Shipping-Service   | Composite orchestration: create customer/product, place order (saga) | 9006 |
| PostgreSQL         | Persistence for all services                                         | 5432 |
| RabbitMQ           | Async event bus (order-placed)                                       | 5672 |
| Redis              | Distributed cache (product catalog)                                  | 6379 |
| Zipkin             | Distributed tracing (Micrometer + Brave)                             | 9411 |

> Note: shipping-service is an orchestrator (composite) — it holds no business domain
> data beyond the customer→order link and drives the other services.

## 4. Key design decisions

- **Stateless JWT auth at the gateway** — a single choke point validates tokens; business
  services stay auth-unaware (except Customer-Service which issues tokens).
- **Saga orchestration** — placing an order is a saga in Shipping-Service with compensating
  actions (delete order, restore cart) to maintain consistency without distributed tx.
- **Event-driven inventory** — order success publishes `order-placed` on RabbitMQ;
  Inventory-Service consumes it to decrement stock, decoupling the services.
- **Distributed cache** — the read-mostly product catalog is cached in Redis with
  `@CacheEvict` on writes to keep it fresh.
- **Resilience4j circuit breaker** — Shipping-Service wraps its composite calls with a
  fallback `503` instead of a hard failure.
- **Distributed tracing** — all services emit Brave/Micrometer spans to Zipkin, so a single
  request is traceable gateway → orchestrator → business services.

## 5. Infrastructure & deployment

- All services and infrastructure run as **Docker Compose** containers on a shared bridge
  network (`ecommerce-network`).
- Health/liveness/readiness probes exposed per service via Spring Boot Actuator.
- PostgreSQL volume is persisted across restarts (`postgres-data`).

## 6. Non-functional characteristics

- **Scalability** — stateless services can scale horizontally behind Eureka.
- **Availability** — circuit breakers and event-driven decoupling prevent cascading failures.
- **Observability** — health probes, Zipkin tracing, and centralized error envelopes.
- **Security** — gateway-level JWT validation, BCrypt password hashing.
