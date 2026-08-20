# Verification Curls — Reference

Commands used to verify the microservices end-to-end, with the responses we saw and a
short explanation for each. Run from the project root with the stack up
(`docker compose up -d --build`).

Note: some endpoints are JWT-protected. Get a token via the login call below and send it
as `Authorization: Bearer <token>`.

---

## Health & readiness probes

```bash
curl localhost:9002/actuator/health
curl localhost:9002/actuator/health/liveness
curl localhost:9006/actuator/health
```

**Response (product-service):**
```json
{"status":"UP","groups":["liveness","readiness"]}
{"status":"UP"}
```
Shows the actuator health/liveness/readiness probes we enabled are working.

## Seed data

```bash
curl localhost:9002/products
curl localhost:9003/api/inventory/1
```

**Response (products):**
```json
[{"productId":1,"productName":"Wireless Mouse","productDescription":"Ergonomic 2.4GHz wireless mouse","productPrice":1999.0}, ...]
```
The `DataSeeder` populated 4 products and matching inventory on first boot, so the
storefront is demo-ready without manual inserts.

## Distributed tracing (Zipkin)

```bash
curl localhost:9411/api/v2/services
```

**Response:**
```json
["api-gateway","cart-service","customer-service","order-service","product-service","shipping-service"]
```
Every service that handled a request appears here — proving spans propagate end-to-end.
The Zipkin UI is at `http://localhost:9411`.

## JWT auth

```bash
# Sign up (password is BCrypt-hashed in the response)
curl -X POST localhost:8080/api/customerservice/customer/addCustomer \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Auth Test","customerEmail":"auth@example.com","password":"secret123"}'

# Login -> returns { token, customer }
curl -X POST localhost:8080/api/customerservice/customer/login \
  -H "Content-Type: application/json" \
  -d '{"customerEmail":"auth@example.com","password":"secret123"}'
```

**Login response (truncated):**
```json
{"token":"eyJhbGciOi...","customer":{...,"role":"CUSTOMER","customerId":4}}
```

```bash
TOKEN="<token-from-login>"

# Protected route WITHOUT token -> 401
curl -o /dev/null -w "%{http_code}" localhost:8080/api/cartservice/api/cart/4
# 401

# Protected route WITH valid token -> reaches the service (404 here: no cart for id 4)
curl -o /dev/null -w "%{http_code}" -H "Authorization: Bearer $TOKEN" localhost:8080/api/cartservice/api/cart/4
# 404

# With an invalid/forged token -> 401
curl -o /dev/null -w "%{http_code}" -H "Authorization: Bearer invalid.token.here" localhost:8080/api/cartservice/api/cart/4
# 401
```
Confirms the gateway's JWT filter rejects unauthenticated/invalid requests and lets
authenticated ones through.

## Circuit breaker (Resilience4j)

```bash
# Breaker state endpoint
curl localhost:9006/actuator/circuitbreakers
```
When a downstream service is unreachable, the composite returns the fallback:
```json
"Customer/Cart service unavailable. Please retry."   // HTTP 503
```

## Saga — place order

```bash
# Create a cart for the customer, then place an order (saga orchestration)
curl -X POST -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  localhost:8080/api/cartservice/api/cart \
  -d '{"cartid":4,"lineitem":[{"productId":1,"productName":"Wireless Mouse","quantity":2,"price":1999}]}'

curl -X POST -H "Authorization: Bearer $TOKEN" \
  localhost:8080/api/shoppingservice/customer/4/order
```

**Happy-path response:**
```json
{"orderid":4,"lineitem":[{"itemid":3,"productId":1,"productName":"Wireless Mouse","quantity":2}]}   // HTTP 201
```

**Failure path (empty/missing cart):** the saga catches the downstream 404 and compensates
(rolls back the order). Response:
```json
"Order placement failed and was rolled back."   // HTTP 500
```
Shipping log shows: `[saga] placing order failed, compensating: 404 : "Cart Not Found."`

---

## Prerequisites

- Set `TOKEN` from a fresh login call (tokens expire after 24h).
- Seed product IDs (1-4) and customer IDs may differ if the DB already had data.
