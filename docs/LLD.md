# Low-Level Design (LLD) — E-Commerce Microservices Platform

## 1. Per-service component breakdown

### 1.1 API Gateway (`zuul-api-gateway`, :8080)
- **JwtValidationFilter** (`GlobalFilter`) — validates `Authorization: Bearer <token>` on
  protected routes; public routes (`/products`, `addCustomer`, `login`) pass through.
- **Routing** — `application.properties` maps `/api/<service>/**` to each service via
  `lb://<ServiceName>` and rewrites the prefix off (e.g.
  `/api/cartservice/api/cart/{id}` → `/api/cart/{id}`).

### 1.2 Product-Service (:9002)
- `ProductController` — REST endpoints under `/products`.
- `ProductService` — `findAll()` is `@Cacheable("products", key="'all'")`; writes
  (`addProduct`, `updateProduct`, `deleteProductById`) are `@CacheEvict` on the same key.
- `RedisCacheConfig` — JSON value serializer, 30-min TTL.
- `ProductRepository` (Spring Data JPA).
- `DataSeeder` — seeds 4 products on startup if empty.

### 1.3 Inventory-Service (:9003)
- `InventoryController` — CRUD under `/api/inventory`.
- `InventoryService` / `InventoryServiceImpl` — `decrementStock(productId, qty)` decrements
  stock by the given amount (guards insufficient stock), used by the RabbitMQ consumer.
- `OrderPlacedListener` (`@RabbitListener` on `inventory.order.placed`) — for each line item
  calls `decrementStock`.
- `InventoryRepository` — adds `findByProductId`.

### 1.4 Order-Service (:9004)
- `OrderController` — create/get orders under `/api/order`.
- `OrderService` / repository — JPA persistence.

### 1.5 Cart-Service (`Cart-Entity`, :9005)
- `CartController` — GET/POST/PUT `/api/cart`, GET/PUT `/api/cart/{id}`.
- `CartEntityService` / `LineItemService` — per-customer cart + line items.

### 1.6 Customer-Service (`CustomerService`, :9001)
- `Controller` — `addCustomer` (BCrypt-hashes password, role `CUSTOMER`), `login`
  (returns JWT), `searchCustomer/{id}`, `deleteCustomer/{id}`, `updateCustomer`.
- `security/JwtUtil` — issues HS256 JWT (subject, email, role), 24h expiry.
- `exception/ControllerAdvisor` — uniform error envelope.

### 1.7 Shipping-Service (:9006)
- `CustomerController` (orchestrator)
  - `createCustomer` → `doCreateCustomer` — creates customer then cart; **compensation**
    deletes the customer if the cart call fails.
  - `placingOrder` — saga: resolve customer → resolve cart → create order → persist
    link → empty cart → **publish `order.placed` on RabbitMQ**; `compensateOrder` rolls
    back on failure.
  - Wrapped in `CircuitBreakerFactory` (Resilience4j) with `503` fallback.
- `ProductController` — `createProduct` → `doCreateProduct` — creates product then
  inventory; **compensation** deletes the product if the inventory call fails.
- `RabbitMQConfig` — declares `order.exchange`, `order.placed` routing key,
  `inventory.order.placed` queue.

## 2. Data model

- **Product** — productId, productName, productDescription, productPrice, quantity.
- **Inventory** — inventoryId, productId, quantity.
- **Order** — orderId, line items.
- **Cart** — cartId (customer id), line items.
- **Customer** — customerId, customerName, customerEmail, password (BCrypt), role, addresses.
- **CustomerOrder** (shipping) — customerId, orderId link.

## 3. Configuration

- All services pull config from Config-Server (native files in `config-properties/*.properties`).
- Shared infra env injected via the `service-env` anchor in `docker-compose.yml`:
  `SPRING_DATASOURCE_*`, `SPRING_RABBITMQ_*`, `SPRING_DATA_REDIS_*`.
- Tracing: `management.tracing.sampling.probability=1.0` +
  `management.zipkin.tracing.endpoint=http://zipkin:9411/api/v2/spans`.

## 4. Key flows

### Place order (saga + event)
1. Gateway validates JWT, routes to Shipping.
2. Shipping resolves customer & cart.
3. Creates order (Order-Service), persists link, empties cart.
4. Publishes `order.placed` → RabbitMQ.
5. Inventory consumes event, decrements stock.
6. On any failure: `compensateOrder` removes link, deletes order, restores cart.

### Create customer (composite)
1. Shipping creates customer in Customer-Service.
2. Creates cart in Cart-Service.
3. If (2) fails → delete customer (compensation).

### Catalog read (cached)
1. Frontend `GET /api/productservice/products`.
2. Gateway routes to Product-Service.
3. `findAll()` → Redis cache hit (key `products::all`, 30-min TTL) or DB miss→populate.
4. Writes evict the cache.

## 5. Error handling

- `@RestControllerAdvice` per service returns a uniform envelope
  `{timeStamp, statusCode, httpStatus, reason, message}`.
- Circuit breaker returns `503` with a retry message when a composite downstream is down.
