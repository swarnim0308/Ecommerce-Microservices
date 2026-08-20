# E-Commerce Microservices Platform

A distributed e-commerce backend built with **Java 17**, **Spring Boot 3.2**, and **Spring Cloud**. The stack uses Eureka for service discovery, Spring Cloud Config for externalized configuration, Spring Cloud Gateway as the API entry point, and PostgreSQL as the shared database. Authentication is handled with **JWT (HS256)** issued by Customer Service and validated by a filter on the API Gateway.

---

## Architecture

| Component | Port | Description |
|-----------|------|-------------|
| **Service Registry** (Eureka) | 8761 | Service discovery |
| **Config Server** | 8888 | Centralized configuration (`native` profile in Docker) |
| **API Gateway** | 8080 | Routes traffic to downstream services via `lb://` |
| **PostgreSQL** | 5432 | Shared database (`ecommerce`) |
| **Customer Service** | 9001 | Customer profiles |
| **Product Service** | 9002 | Product catalog |
| **Inventory Service** | 9003 | Stock management |
| **Cart Service** | 9004 | Shopping cart |
| **Order Service** | 9005 | Order processing |
| **Shipping Service** | 9006 | Composite orchestration (Product + Inventory) |
| **Zipkin** | 9411 | Distributed tracing (Micrometer) |

```
Client ──▶ API Gateway (8080) ──▶ Eureka (8761) ──▶ Microservices (9001–9006)
                 │  ▲                           │              │
                 │  └─────── Config Server (8888) ──┘            │
                 │                                              │
                 ▼                    Postgres (5432) ◀──── Postgres (5432)
              Zipkin (9411) ◀─────────── spans ────────────────┘
```

Every service reports trace spans (Micrometer Tracing + Brave) to **Zipkin** so a single
request can be followed across the gateway and all downstream microservices.

### Place-order flow (orchestration saga)

1. `POST /api/shoppingservice/customer/{id}/order` → Shipping-Service.
2. Shipping-Service resolves the customer (Customer-Service) and the cart (Cart-Service).
3. It creates the order (Order-Service) and records the customer→order link.
4. It empties the cart (Cart-Service) and returns the order.

If any step fails, **compensating actions** undo the completed steps: the customer-order
link is removed, the order is deleted, and the cart is restored — giving an at-least-once
saga with rollback.

---

## Prerequisites

- **Java 17 (JDK)**
- **Apache Maven 3.8+**
- **Docker Desktop** (for containerized deployment)

---

## Quick Start (Docker)

Docker images expect pre-built JARs. Build all services first, then start the stack.

### Step 1: Build all services

From the project root:

```powershell
# Infrastructure
cd service-registery   && mvn clean package -DskipTests && cd ..
cd Config-Server       && mvn clean package -DskipTests && cd ..
cd zuul-api-gateway    && mvn clean package -DskipTests && cd ..

# Business services
cd CustomerService     && mvn clean package -DskipTests && cd ..
cd product-service     && mvn clean package -DskipTests && cd ..
cd Inventory-Service   && mvn clean package -DskipTests && cd ..
cd Cart-Entity         && mvn clean package -DskipTests && cd ..
cd OrderService        && mvn clean package -DskipTests && cd ..
cd shipping-service    && mvn clean package -DskipTests && cd ..
```

### Step 2: Start the stack

```powershell
docker compose up -d --build
```

Startup order: PostgreSQL → Eureka → Config Server → microservices → gateway.

Config Server uses the **native** profile in Docker and reads from the mounted `./config-properties` folder (no GitHub access required).

### Step 3: Verify

| Check | URL / Command |
|-------|----------------|
| Eureka dashboard | http://localhost:8761 |
| Config Server | http://localhost:8888/customer-service/default |
| Customer health | http://localhost:9001/actuator/health |
| Customer hello | http://localhost:9001/customer/hello |
| Gateway (via customer) | http://localhost:8080/api/customerservice/customer/hello |

Expected Eureka registrations: `Customer-Service`, `Product-Service`, `Inventory-Service`, `Cart-Service`, `Order-Service`, `Shipping-Service`, `Api-Gateway`, `Config-Server`.

### Stop the stack

```powershell
docker compose down
```

---

## Local Development (without Docker)

Run services individually in this order:

1. **PostgreSQL** on `localhost:5432` (user: `root`, password: `password`, database: `ecommerce`)
2. **Service Registry** — `cd service-registery && mvn spring-boot:run`
3. **Config Server** — `cd Config-Server && mvn spring-boot:run` (Git-backed by default; or use `SPRING_PROFILES_ACTIVE=native` with local `config-properties`)
4. **Microservices** — any order after Config Server is up
5. **API Gateway** — `cd zuul-api-gateway && mvn spring-boot:run`

For local runs, Eureka defaults to `http://localhost:8761/eureka/` and Config Server to `http://localhost:8888`.

---

## Authentication & Authorization (JWT)

Login and signup are exposed through Customer Service and the API Gateway:

| Action | Endpoint (via gateway) |
|--------|------------------------|
| Sign up | `POST /api/customerservice/customer/addCustomer` (body includes `password`) |
| Login | `POST /api/customerservice/customer/login` (`{customerEmail, password}`) |

- Passwords are stored as **BCrypt hashes** (`spring-security-crypto`).
- Login returns `{ token, customer }`; the token is a **24-hour HS256 JWT** (`jjwt`) containing `sub` (customerId), `email`, and `role`.
- New accounts default to role `CUSTOMER`.

### Gateway validation

A global `GlobalFilter` (`JwtValidationFilter`) on the API Gateway:

- **Public** (no token required): `GET /api/productservice/products/**`, `POST /api/customerservice/customer/addCustomer`, `POST /api/customerservice/customer/login`.
- **Protected** (Bearer token required): every other route — cart, orders, inventory, shipping, customer lookup/update.
- Requests without a valid token get `401 Unauthorized`.

### Frontend

The storefront (Vite) stores the token in `localStorage` and sends it as `Authorization: Bearer <token>` on all protected calls via a shared `authHeaders()` helper. Login uses email + password; signup collects a password. Logout clears the stored token.

The shared HMAC secret lives in both `CustomerService.security.JwtUtil` and `zuul-api-gateway.JwtValidationFilter`. Change it in both places for production.

---

## API Gateway Routes

| Path prefix | Target service |
|-------------|----------------|
| `/api/customerservice/**` | Customer-Service |
| `/api/productservice/**` | Product-Service |
| `/api/inventoryservice/**` | Inventory-Service |
| `/api/cartservice/**` | Cart-Service |
| `/api/orderservice/**` | Order-Service |
| `/api/shoppingservice/**` | Shipping-Service |

Path rewrite strips the `/api/<service>/` prefix before forwarding. With JWT enabled, protected routes require an `Authorization: Bearer <token>` header.

---

## Service Endpoints (direct access)

| Service | Sample endpoint |
|---------|-----------------|
| Customer | `GET /customer/hello` |
| Product | `GET /products/hello` |
| Inventory | `GET /api/hello` |
| Cart | `GET /api/hello` |
| Order | `GET /api/hello` |
| Shipping | `GET /products/say` |

Use `ECommerceApplication.postman_collection.json` for full API examples, and
`VERIFY_CURLS.md` for the curl commands used to verify each feature end-to-end.

---

## Configuration

- **Docker:** Config from `./config-properties` (mounted into Config Server)
- **Local (default):** Config Server can pull from Git (`jdk_update` branch) if native profile is not used
- **Bootstrap:** Services retry Config Server connection on startup (`fail-fast` + retry enabled)
- **Database:** All services share PostgreSQL database `ecommerce` with `spring.jpa.hibernate.ddl-auto=update`

Environment variables used in Docker (see `docker-compose.yml`):

- `SPRING_CLOUD_CONFIG_URI=http://config-server:8888`
- `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://service-registry:8761/eureka/`
- `SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-db:5432/ecommerce`

---

## Project Structure

```
├── service-registery/     # Eureka server
├── Config-Server/         # Spring Cloud Config
├── zuul-api-gateway/      # Spring Cloud Gateway
├── CustomerService/
├── product-service/
├── Inventory-Service/
├── Cart-Entity/             # Cart service
├── OrderService/
├── shipping-service/
├── config-properties/       # Externalized config (Docker native profile)
├── docker-compose.yml
├── frontend/                # Vite storefront, fully wired to the API gateway
└── hystrix-server/          # Legacy Hystrix dashboard; deprecated (replaced by Resilience4j)
```

---

## Resilience, Health & Seed Data

### Resilience4j circuit breaker (replaces Hystrix)

The shipping-service composite calls (create product, create customer) are wrapped in
Resilience4j circuit breakers via `CircuitBreakerFactory`. When a downstream service is
down, the call returns a graceful `503` fallback instead of failing hard.

### Health / liveness / readiness probes

Every service exposes actuator health, liveness and readiness endpoints
(`management.endpoint.health.probes.enabled=true`). Kubernetes-style probes are
available at `/actuator/health/liveness` and `/actuator/health/readiness`.

### Seed data on startup

- `product-service` and `inventory-service` seed demo products and matching stock on
  first boot (idempotent — skips if data already present).

---

## Tech Stack

- Java 17, Spring Boot 3.2.4, Spring Cloud 2023.0.1
- Netflix Eureka, Spring Cloud Config, Spring Cloud Gateway
- Spring Data JPA, PostgreSQL 15
- Resilience4j (circuit breaker) on shipping-service
- JWT (jjwt) + BCrypt for authentication
- Docker Compose 3.x
