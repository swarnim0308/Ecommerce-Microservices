# Architecture Diagram (Mermaid)

> Render this file in any Mermaid-capable viewer (GitHub, Mermaid Live Editor, VS Code).

## System context

```mermaid
flowchart TB
    subgraph Client
        UI["Storefront SPA (Vite)"]
    end

    subgraph Platform
        GW["API Gateway (Zuul :8080)"]
        EK["Service Registry (Eureka :8761)"]
        CFG["Config Server (:8888)"]
    end

    subgraph Services
        P["Product-Service :9002"]
        I["Inventory-Service :9003"]
        O["Order-Service :9004"]
        C["Cart-Service :9005"]
        CU["Customer-Service :9001"]
        SH["Shipping-Service :9006"]
    end

    subgraph Infra
        DB[("PostgreSQL :5432")]
        RMQ["RabbitMQ :5672"]
        RD[("Redis :6379")]
        ZK["Zipkin :9411"]
    end

    UI -->|"/api/* HTTP"| GW
    GW -->|"/api/productservice/**"| P
    GW -->|"/api/inventoryservice/**"| I
    GW -->|"/api/orderservice/**"| O
    GW -->|"/api/cartservice/**"| C
    GW -->|"/api/customerservice/**"| CU
    GW -->|"/api/shoppingservice/**"| SH

    P & I & O & C & CU & SH -->|"register / discover"| EK
    P & I & O & C & CU & SH -->|"bootstrap config"| CFG
    P & I & O & C & CU & SH --> DB
    SH -->|"order.placed event"| RMQ
    RMQ -->|"consume: decrement stock"| I
    P -->|"@Cacheable catalog"| RD
    P & I & O & C & CU & SH & GW -->|"spans"| ZK
```

## Place-order flow (saga)

```mermaid
sequenceDiagram
    participant UI as Storefront
    participant GW as API Gateway
    participant SH as Shipping-Service
    participant CU as Customer-Service
    participant C as Cart-Service
    participant O as Order-Service
    participant RMQ as RabbitMQ
    participant I as Inventory-Service

    UI->>GW: POST /api/shoppingservice/customer/{id}/order (Bearer)
    GW->>SH: forward with JWT
    SH->>CU: get customer
    SH->>C: get cart (keep original)
    SH->>O: create order
    SH->>SH: persist customer->order link
    SH->>C: empty cart
    SH->>RMQ: publish order.placed (line items)
    SH-->>UI: 201 Order
    RMQ->>I: consume order.placed
    I->>I: decrementStock(productId, qty)
```

## Create customer (composite, with compensation)

```mermaid
sequenceDiagram
    participant UI as Storefront
    participant SH as Shipping-Service
    participant CU as Customer-Service
    participant C as Cart-Service

    UI->>SH: POST /api/shoppingservice/customer/
    SH->>CU: create customer
    SH->>C: create cart
    alt cart creation fails
        SH->>CU: delete customer (compensation)
        SH-->>UI: error (rolled back)
    else success
        SH-->>UI: 200 customer
    end
```

## Create product (composite, with compensation)

```mermaid
sequenceDiagram
    participant SH as Shipping-Service
    participant P as Product-Service
    participant I as Inventory-Service

    SH->>P: create product
    SH->>I: create inventory record
    alt inventory creation fails
        SH->>P: delete product (compensation)
        SH-->>SH: rollback
    else success
        SH-->>SH: ProductInventoryVo (201)
    end
```
