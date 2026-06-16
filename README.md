# E-Commerce Microservices Platform

A scalable, distributed e-commerce backend platform built using **Java 17**, **Spring Boot**, and **Spring Cloud**. The architecture incorporates centralized configuration management, dynamic service discovery, resilient service-to-service communication, and an API gateway topology.

---

## Architecture Diagram

- **API Gateway (Port 8080):** Single point of entry routing traffic to downstream services using logical service IDs via `lb://`.
- **Service Registry (Port 8761):** Netflix Eureka engine tracking running instances.
- **Config Server (Port 8888):** Externalized application properties engine running on a local file-based `native` profile for containerization and a Git-backed profile for cloud fallbacks.
- **Databases:** Centralized PostgreSQL instance managing service-isolated relational schemas.

---

## Component Port Mapping & Matrix

| Service Name | Local Port | Docker Exposed Port | Database Strategy |
| :--- | :--- | :--- | :--- |
| **Service Registry** | `8761` | `8761:8761` | N/A |
| **Config Server** | `8888` | `8888:8888` | N/A |
| **Zuul/API Gateway** | `8080` | `8080:8080` | N/A |
| **Customer Service** | `9001` | `9001:9001` | `ecommerce` DB (Auto-update) |
| **Product Service** | `9002` | `9002:9002` | `ecommerce` DB (Auto-update) |
| **Inventory Service**| `9003` | `9003:9003` | `ecommerce` DB (Auto-update) |
| **Cart Service** | `9004` | `9004:9004` | `ecommerce` DB (Auto-update) |
| **Order Service** | `9005` | `9005:9005` | `ecommerce` DB (Auto-update) |
| **Shipping Service** | `9006` | `9006:9006` | `ecommerce` DB (Auto-update) |

---

## Prerequisites
Ensure the following tools are installed locally on your development machine:
* **Java 17 (JDK)**
* **Apache Maven 3.8+**
* **Docker Desktop / Docker Compose Engine**

---

## 🏗️ **Architecture Overview**  
### Key Microservices:
- **Config Server** – Centralized configuration for all services.  
- **Eureka Service Registry** – Service discovery for dynamic load balancing.  
- **Zuul API Gateway** – Routing and authentication for APIs.  
- **Hystrix Server** – Circuit breaker for fault isolation and resilience.  

### Core Business Services:
- **Customer Service** – Manages user profiles and customer data.  
- **Product Service** – Handles product catalog and inventory.  
- **Order Service** – Processes orders and manages order history.  
- **Inventory Service** – Tracks stock levels and manages availability.  
- **Shipping Service** – Handles shipment tracking and delivery updates.  


## Getting Started: Compilation & Build Order

Because this project utilizes optimized runtime Docker images (`eclipse-temurin:17-jre-alpine`) without packaging source-code compilers inside the container layer, **you must compile the artifacts locally using Maven prior to launching Docker Compose.**

### Step 1: Compile the entire project
Execute the compilation across all modules. Navigate into each service directory and compile the `.jar` packages:

```bash
# Build Platform Infra
cd service-registery && mvn clean package -DskipTests && cd ..
cd Config-Server && mvn clean package -DskipTests && cd ..
cd zuul-api-gateway && mvn clean package -DskipTests && cd ..

# Build Business Applications
cd CustomerService && mvn clean package -DskipTests && cd ..
cd product-service && mvn clean package -DskipTests && cd ..
cd Inventory-Service && mvn clean package -DskipTests && cd ..
cd Cart-Entity && mvn clean package -DskipTests && cd ..
cd OrderService && mvn clean package -DskipTests && cd ..
cd shipping-service && mvn clean package -DskipTests && cd ..
