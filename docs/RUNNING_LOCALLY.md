# Running Locally — URLs & Verification

This is the operational companion to `README.md`: every URL the running stack exposes, plus
the startup gotchas that cost time if you don't know them.

Verified against a full `docker compose up -d --build` on Windows + Docker Desktop (Engine
29.5.3, Compose v5.1.4).

---

## Start the stack

```powershell
docker compose up -d --build
```

Images are built from each module's Dockerfile, so a prior `mvn package` is not required.

**Startup order is now enforced by healthchecks.** `service-registry` and `config-server`
declare healthchecks, and every config client waits for `config-server` to report healthy
before starting, so a single command is enough:

```powershell
docker compose up -d --build
```

Cold boots are slow: Eureka has been observed taking ~108 s, and each config client 30–60 s
after that. Budget ~3 minutes before expecting a fully green stack. See
[Known issues](#known-issues) for the history behind the healthchecks.

---

## URLs

### Observability

| What | URL | Credentials |
|------|-----|-------------|
| **Zipkin** (traces) | http://localhost:9411 | none |
| **RabbitMQ management** | http://localhost:15672 | `guest` / `guest` |
| **Prometheus** (metrics) | http://localhost:9090 | none |
| **Grafana** (dashboards) | http://localhost:3000 | `admin` / `admin` |

Useful sub-pages:

- Prometheus targets — http://localhost:9090/targets
- Prometheus query — http://localhost:9090/graph
- Zipkin service list — http://localhost:9411/api/v2/services
- Zipkin dependency graph — http://localhost:9411/zipkin/dependency/

### Discovery & configuration

| What | URL |
|------|-----|
| **Eureka dashboard** | http://localhost:8761 |
| **Config Server** | http://localhost:8888 |
| Config for one service | http://localhost:8888/order-service/default |
| **API Gateway** (single entry point) | http://localhost:8080 |

### Service actuators (direct access)

Every service exposes `/actuator/health`, `/actuator/prometheus`, and the Kubernetes-style
probes `/actuator/health/liveness` and `/actuator/health/readiness`.

| Service | Port | Health |
|---------|------|--------|
| Customer-Service | 9001 | http://localhost:9001/actuator/health |
| Product-Service | 9002 | http://localhost:9002/actuator/health |
| Inventory-Service | 9003 | http://localhost:9003/actuator/health |
| Cart-Service | 9004 | http://localhost:9004/actuator/health |
| Order-Service | 9005 | http://localhost:9005/actuator/health |
| Shipping-Service | 9006 | http://localhost:9006/actuator/health |
| Config-Server | 8888 | http://localhost:8888/actuator/health |
| API Gateway | 8080 | http://localhost:8080/actuator/health |
| Eureka | 8761 | http://localhost:8761/actuator/health |

### Infrastructure

| What | Address | How to inspect |
|------|---------|----------------|
| PostgreSQL | `localhost:5432`, db `ecommerce` | `docker compose exec postgres-db psql -U root -d ecommerce` |
| RabbitMQ (AMQP) | `localhost:5672` | use the management UI on 15672 |
| **Redis** | `localhost:6379` | **no web UI** — `docker compose exec redis redis-cli` |

Redis has no browser dashboard. To check the product catalog cache:

```powershell
docker compose exec redis redis-cli KEYS '*'
```

### Gateway routes

All routes are prefixed `/api/<service>/` and the prefix is stripped before forwarding
(`zuul-api-gateway/src/main/resources/application.properties:9-37`):

| Path prefix | Target service |
|-------------|----------------|
| `/api/customerservice/**` | Customer-Service |
| `/api/productservice/**` | Product-Service |
| `/api/inventoryservice/**` | Inventory-Service |
| `/api/cartservice/**` | Cart-Service |
| `/api/orderservice/**` | Order-Service |
| `/api/shoppingservice/**` | Shipping-Service |

Example: http://localhost:8080/api/productservice/products

Protected routes need `Authorization: Bearer <token>` — see the JWT section in `README.md`.

---

## Verify it's all up

Copy-paste health check across every port:

```powershell
foreach ($p in 8761,8888,8080,9001,9002,9003,9004,9005,9006) {
  "{0,-6} {1}" -f $p, (Invoke-WebRequest "http://localhost:$p/actuator/health" -UseBasicParsing).StatusCode
}
```

Expect `200` from all nine. Then confirm Eureka has the full set of registrations:

```powershell
(Invoke-WebRequest "http://localhost:8761/eureka/apps" -Headers @{Accept="application/json"} -UseBasicParsing).Content
```

Expected: `API-GATEWAY`, `CART-SERVICE`, `CONFIG-SERVER`, `CUSTOMER-SERVICE`,
`INVENTORY-SERVICE`, `ORDER-SERVICE`, `PRODUCT-SERVICE`, `SHIPPING-SERVICE`.

---

## Known issues

Issues 1 and 2 have been **fixed** in the repo; issue 3 remains open. They are documented
because the causes are subtle enough to be worth knowing if they resurface.

### 1. Config clients died on a cold start (startup race) — FIXED

On a cold `docker compose up -d`, Order-Service and the other config clients used to fail with:

```
Could not locate PropertySource and the fail fast property is set, failing
Caused by: java.net.ConnectException: Connection refused
    at ... GET http://config-server:8888/order-service/default
```

**Cause:** `docker-compose.yml` originally used plain `depends_on`, which waits only for the
container to *exist*, not for the app inside to accept connections. Config Server needs ~40 s to
boot, so clients started too early and aborted because `spring.cloud.config.fail-fast=true`
(`OrderService/src/main/resources/bootstrap.properties:2`). The retry settings
(`max-attempts=12`, `initial-interval=3000`) were not enough to cover a cold Config Server boot.

**Applied fix:** `config-server` and `service-registry` now declare a `healthcheck`, and every
config client waits on it via `depends_on: {config-server: {condition: service_healthy}}`.
Verified by a full `docker compose down` followed by `up` — all nine services reached `200`
without any manual restart.

The healthcheck added to `config-server`:

```yaml
    healthcheck:
      test: ["CMD", "wget", "-qO-", "http://localhost:8888/actuator/health"]
      interval: 10s
      timeout: 5s
      retries: 12
      start_period: 60s
```

### 2. Prometheus targets were DOWN (endpoints not exposed) — FIXED

Every job at http://localhost:9090/targets used to show **DOWN**. Two independent causes, both
now fixed; all seven targets report **up**.

**Cause A — the `/actuator/prometheus` endpoint was not exposed.** Five config files set a
narrow exposure list, and `micrometer-registry-prometheus` was only a dependency of
`shipping-service` and `zuul-api-gateway`.

| Config file | was | now |
|-------------|-----|-----|
| `config-properties/shipping-service.properties:17` | `health,info,metrics,prometheus,circuitbreakers` | unchanged |
| `config-properties/order-service.properties:19` | `health,info` | `health,info,metrics,prometheus` |
| `config-properties/cart-service.properties:19` | `health,info` | `health,info,metrics,prometheus` |
| `config-properties/customer-service.properties:19` | `health,info` | `health,info,metrics,prometheus` |
| `config-properties/inventory-service.properties:19` | `health,info` | `health,info,metrics,prometheus` |
| `config-properties/product-service.properties:19` | `health,info` | `health,info,metrics,prometheus` |

The `micrometer-registry-prometheus` dependency was added to `OrderService`, `Cart-Entity`,
`CustomerService`, `Inventory-Service`, `product-service` and `Config-Server` `pom.xml` files.
The API Gateway is not a config client, so its exposure list was added directly at
`zuul-api-gateway/src/main/resources/application.properties:43`.

A diagnostic gotcha worth remembering: ports 9002 and 9003 returned **500**, not 404, before
the fix. That was the same missing-endpoint problem — their `@RestControllerAdvice` re-wrapped
it as `{"httpStatusCode":500,...,"message":"Unexpected error: No static resource
actuator/prometheus."}`. A 500 there does not indicate a different fault.

**Cause B — wrong hostnames and a port swap in `prometheus/prometheus.yml`.** The ports for
Order-Service and Cart-Service were swapped, and four targets used Spring application names
instead of compose service names:

| before | after |
|--------|-------|
| `Inventory-Service:9003` | `inventory-service:9003` |
| `OrderService:9004` | `order-service:9005` |
| `Cart-Entity:9005` | `cart-service:9004` |
| `CustomerService:9001` | `customer-service:9001` |

Docker DNS resolves **compose service** names on the network, so align this file on the
lowercase compose keys.

### 3. Grafana starts with no dashboards provisioned — NOT FIXED

Grafana is up and reachable, but `docker-compose.yml` (lines 205-215) mounts no provisioning
directory — the only volumes in the whole file are `postgres-data` and the Prometheus config
bind-mount. So there is no pre-built Prometheus datasource or dashboard. Add the Prometheus
datasource manually (URL `http://prometheus:9090`) on first use.

---

## End-to-end verification results

Last full run of the checklist in `VERIFY_CURLS.md`, against a stack built from `docker compose
up -d --build`.

| Flow | Check | Result |
|------|-------|--------|
| Health probes | `/actuator/health` + `/liveness` on 9002 | `UP`, groups `liveness`,`readiness` |
| Seed data | `GET :9002/products` | 4 seeded products |
| Seed data | `GET :9003/api/inventory/1` | `quantity:48` |
| JWT signup | `POST /api/customerservice/customer/addCustomer` | `201`, password BCrypt-hashed |
| JWT login | `POST /api/customerservice/customer/login` | token issued, `role:CUSTOMER` |
| Auth deny | cart route, no token | `401` |
| Auth allow | cart route, valid token | `404` (reaches service) |
| Auth deny | cart route, forged token | `401` |
| Saga happy path | create cart → `POST /api/shoppingservice/customer/{id}/order` | `201`, `orderid:7` |
| RabbitMQ event | inventory after order | `48 → 46`, queue `inventory.order.placed` drained |
| Redis cache | `redis-cli KEYS '*'` | `products::all` populated on read |
| Redis eviction | add product, re-check keys | key evicted on write |
| Tracing | `localhost:9411/api/v2/services` | all 7 services present |
| Metrics | `localhost:9090/targets` | 7/7 `up` |

Not covered by `VERIFY_CURLS.md`, verified separately:

- **Frontend** — `npm run build` succeeds. There is **no test script**: `frontend/package.json`
  defines only `dev`, `build`, `preview`.
- **Circuit breaker** — `GET :9006/actuator/circuitbreakers` returns `{"circuitBreakers":{}}`
  (empty), because no breaker has been triggered yet. It would populate after a downstream
  failure is induced.

### Open defect: an empty cart does not roll back

`VERIFY_CURLS.md:119-124` documents that placing an order with a missing/empty cart returns
`500` with `"Order placement failed and was rolled back."` That is **not** what happens.

Placing a second order for a customer whose cart was already emptied returns:

```
{"orderid":8,"lineitem":[]}   // HTTP 201
```

A persisted order row with no line items is left behind (`select count(*) from orders` → 8),
and a `order.placed` event is published with an empty payload. Shipping logs show
`[rabbit] published order.placed` with no `[saga] placing order failed` line — so the catch
block never ran.

**Cause:** the documented `500`/rollback path only triggers when the cart returns **404**
(cart missing entirely), which `CustomerController.placingOrder` catches as an exception.
An existing-but-empty cart returns **200** with `{"cartid":9,"lineitem":[]}`, so no exception is
thrown, `theCart.getLineitem()` is an empty list, and the saga reports success.

To reproduce the documented rollback, request an order for a customer with no cart at all:

```bash
curl -o /dev/null -w "%{http_code}" localhost:9004/api/cart/9999
# 404 -> this is the case that compensates
```

Fixing it means treating an empty line-item list as a failure in
`placingOrder` (step 2, after the cart resolves) and routing it to `compensateOrder`. Not done.

---

## Stopping

```powershell
docker compose down          # stop and remove containers
docker compose down -v       # also drop the postgres volume (destructive)
```

`down -v` deletes the persisted `postgres-data` volume — all seeded data is lost.
