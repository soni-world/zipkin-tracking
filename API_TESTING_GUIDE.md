# Zipkin Distributed Tracing - API Testing Guide

## Overview
This application demonstrates distributed tracing with Zipkin across multiple service layers:
- **Controllers** → **Services** → **Database**
- Traces flow through UserService and OrderService
- OrderService calls UserService (demonstrating service-to-service tracing)

## Quick Start

### 1. Start Zipkin (Required for viewing traces)
```bash
docker run -d -p 9411:9411 openzipkin/zipkin
```

### 2. Access Points
- **Application**: http://localhost:8080
- **Zipkin UI**: http://localhost:9411
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:zipkindb`
  - Username: `sa`
  - Password: (leave empty)
- **Actuator**: http://localhost:8080/actuator
- **Prometheus Metrics**: http://localhost:8080/actuator/prometheus

## API Endpoints

### User Endpoints

#### Get All Users
```bash
curl http://localhost:8080/api/users
```

#### Get User by ID
```bash
curl http://localhost:8080/api/users/1
```

#### Create User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New User",
    "email": "newuser@example.com",
    "city": "San Francisco"
  }'
```

#### Update User
```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Name",
    "email": "updated@example.com",
    "city": "Seattle"
  }'
```

#### Delete User
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

### Order Endpoints

#### Get All Orders
```bash
curl http://localhost:8080/api/orders
```

#### Get Order by ID
```bash
curl http://localhost:8080/api/orders/1
```

#### Get Order with User Details (Service-to-Service Call)
**This endpoint demonstrates distributed tracing!**
```bash
curl http://localhost:8080/api/orders/1/with-user
```

#### Get Orders by User ID (Service-to-Service Call)
**This endpoint also demonstrates distributed tracing!**
```bash
curl http://localhost:8080/api/orders/user/1
```

#### Create Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productName": "New Product",
    "amount": 99.99
  }'
```

#### Delete Order
```bash
curl -X DELETE http://localhost:8080/api/orders/1
```

## Viewing Traces in Zipkin

1. Make some API calls (especially the ones marked for distributed tracing)
2. Open Zipkin UI: http://localhost:9411
3. Click "Run Query" to see recent traces
4. Click on any trace to see the detailed span information
5. You'll see spans for:
   - HTTP request (Controller layer)
   - Service method calls (UserService, OrderService)
   - Database queries (JPA/Hibernate)
   - Service-to-service calls (OrderService → UserService)

## Sample Data

### Users
- ID 1: John Doe (john.doe@example.com) - New York
- ID 2: Jane Smith (jane.smith@example.com) - Los Angeles
- ID 3: Bob Johnson (bob.johnson@example.com) - Chicago
- ID 4: Alice Williams (alice.williams@example.com) - Houston

### Orders
- 6 sample orders distributed across users
- Products: Laptop, Mouse, Keyboard, Monitor, Headphones, Webcam

## Testing Distributed Tracing

Run these commands in sequence to see traces flow through multiple services:

```bash
# 1. Get order with user details (OrderController → OrderService → UserService)
curl http://localhost:8080/api/orders/1/with-user

# 2. Create a new order (validates user exists first)
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 2, "productName": "Tablet", "amount": 299.99}'

# 3. Get all orders for a specific user
curl http://localhost:8080/api/orders/user/2
```

Then check Zipkin UI to see the complete trace with all spans!

