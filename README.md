# 🛒 E-Commerce Microservices

## 🚀 Overview  
This project is a scalable e-commerce platform built using **Spring Boot** and a **microservices architecture**. It includes multiple independent services communicating via **REST APIs**, with centralized configuration, load balancing, service discovery, and fault tolerance.

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

---

## ⚙️ **Tech Stack**  
- **Java 11**  
- **Spring Boot** (Microservices, REST)  
- **Eureka** (Service Discovery)  
- **Zuul** (API Gateway)  
- **Ribbon** (Client-Side Load Balancing)  
- **Hystrix** (Circuit Breaker)  
- **Postman** (API Testing)  

---
