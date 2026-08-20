# Running Notes — Challenges & Fixes

Reference doc for issues hit while running this project and how they were resolved.
Not part of the committed feature work; kept for the team's reference.

---

## 1. API Gateway rewrite dropped the service prefix -> 404 on every route

**Symptom:** `GET /api/productservice/products` returned 404 with path `/productservice/products`; same for customer/cart/etc.

**Root cause:** The gateway `RewritePath` regex was
`RewritePath=/api/(?<segment>.*), /${segment}` — it stripped only the leading `/api/`
and kept the service segment, so `/api/productservice/products` was forwarded as
`/productservice/products`. But controllers serve `/products` (no class-level prefix),
so nothing matched.

**Fix:** Change each route to strip the service segment too, e.g.
`RewritePath=/api/productservice/(?<segment>.*), /${segment}` for all six routes
(`zuul-api-gateway/src/main/resources/application.properties`).

**Lesson:** The frontend's product page silently fell back to demo data when the fetch
failed, which hid this bug. Test the real endpoint, don't rely on graceful fallbacks.

---

## 2. Frontend only wired to 2 of 6 services

**Symptom:** Cart and Orders pages were "under construction"; `addToCart` only logged
a message (the real cart POST was commented out); inventory/shipping never called.

**Fix:** Rewired `frontend/src/main.js` to call Cart (`/api/cartservice/api/cart/{id}`),
Order (`/api/orderservice/api/order/{id}`), Inventory (`/api/inventoryservice/api/inventory/{id}`),
and Shipping (`/api/shoppingservice/customer/{id}/order|orders`).

---

## 3. cart-service crash-looped: `ClassNotFoundException: org.postgresql.Driver`

**Symptom:** Container kept restarting; logs showed
`Cannot load driver class: org.postgresql.Driver`.

**Root cause:** `Cart-Entity/pom.xml` only declared the H2 driver. Docker Compose injects
`SPRING_DATASOURCE_URL=jdbc:postgresql://...` (`docker-compose.yml`), so cart-service
tried Postgres but had no driver. It was the **only** service missing the dependency —
the other 5 all declare `org.postgresql:postgresql`.

**Fix:** Added the same runtime `org.postgresql:postgresql` dependency to
`Cart-Entity/pom.xml`, rebuilt, recreated the container. Hibernate then logged
`PostgreSQLDialect` and the service started.

---

## 4. Docker images copy pre-built JARs, so config changes need a jar rebuild

**Symptom:** Even after editing the gateway's `application.properties`, the container
kept the old behavior.

**Root cause:** Each `Dockerfile` does `COPY target/*.jar app.jar` — it does **not**
compile the source. So source/config edits don't take effect until the jar is rebuilt.

**Fix:** Always run `mvn package` in the service dir, then `docker compose up -d --build <service>`
to rebuild the image and recreate the container.

---

## 5. Gateway load-balancer cache goes stale -> persistent 503

**Symptom:** `503 Service Unavailable` / log "No servers available for service: Product-Service"
even though the service was UP in Eureka.

**Root cause:** The gateway fetched the registry snapshot during boot, when some services
weren't registered yet, and its LB cache (default TTL) stayed empty. Retrying did not
refresh it quickly.

**Fix:** `docker compose restart zuul-api-gateway` once all services are registered.
The restart picks up a full registry snapshot. (Worth making the gateway start last in
`startup order`.)

---

## 6. Config-server bootstrap race -> `Connection refused` on :8888

**Symptom:** A service (e.g. cart or customer) failed at boot with
`I/O error on GET http://config-server:8888/<svc>/default: Connection refused`.

**Root cause:** `docker compose up` restarted config-server and the microservice at the
same time; the microservice reached config-server before it was ready.

**Fix:** Let config-server finish booting first (or restart the failing service).
Spring Cloud retries, so the next boot succeeded once config-server was up.

---

## 7. Shipping composite failed: `No matches for virtual host name :Cart-Service`

**Symptom:** `POST /api/shoppingservice/customer/` returned 500.

**Root cause:** Shipping calls other services by Eureka app name. It tried to reach
`Cart-Service` before cart-service had registered (it was crash-looping per #3).

**Fix:** Once cart-service was healthy and registered, shipping's call succeeded.
Resolved as a side-effect of fixing #3.

---

## 8. Authentication was nonexistent

**Symptom:** "Login" was a plain `GET /customer/searchCustomer/{id}` with the user object
stored in plaintext `localStorage`; no password, no token, no `Authorization` header.

**Fix:** Added JWT (HS256) auth:
- `Customer.java` gained `password` (BCrypt) and `role` (default `CUSTOMER`).
- `POST /customer/login` returns `{token, customer}` (`CustomerService/.../security/JwtUtil.java`).
- Gateway `JwtValidationFilter` requires a Bearer token on all routes except
  products, signup, and login (401 otherwise).
- Frontend sends `Authorization: Bearer <token>` via an `authHeaders()` helper.

**Lesson:** The HMAC secret is duplicated in `JwtUtil` and `JwtValidationFilter` — change
both in production.

---

## 9. `product.imageUrl` field drift

**Symptom:** Every product card fell back to a placeholder image.

**Root cause:** The frontend reads `product.imageUrl`, but the backend `Product` entity
has no such field (only `productId`, `productName`, `productDescription`, `productPrice`).

**Fix:** None applied (cosmetic) — the fallback works. Either add the field to the
backend or remove its use in `frontend/src/main.js`.

---

## Recurring workflow reminders

- Rebuild the jar before rebuilding a Docker image (`mvn package`, then `docker compose up -d --build <svc>`).
- Bring services up, wait for full Eureka registration, then (re)start the gateway.
- When two services race at startup (config/bootstrap or Eureka registration), the
  dependent service's first boot may fail — restart it once its dependency is healthy.
