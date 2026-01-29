# Zipkin Distributed Tracing - Spring Boot Demo

A comprehensive Spring Boot application demonstrating **distributed tracing** with Zipkin across multiple service layers, including service-to-service communication and database interactions.

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                         Client/Browser                          │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTP Request
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Controller Layer                             │
│  ┌──────────────┐              ┌──────────────┐                │
│  │UserController│              │OrderController│                │
│  └──────┬───────┘              └──────┬────────┘                │
└─────────┼──────────────────────────────┼─────────────────────────┘
          │                              │
          │ Trace Propagation            │ Trace Propagation
          ▼                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Service Layer                               │
│  ┌──────────────┐              ┌──────────────┐                │
│  │ UserService  │◄─────────────│ OrderService │                │
│  └──────┬───────┘  Service Call└──────┬────────┘                │
└─────────┼──────────────────────────────┼─────────────────────────┘
          │                              │
          │ JPA/Hibernate                │ JPA/Hibernate
          ▼                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                  Repository Layer (Spring Data JPA)             │
│  ┌──────────────┐              ┌──────────────┐                │
│  │UserRepository│              │OrderRepository│                │
│  └──────┬───────┘              └──────┬────────┘                │
└─────────┼──────────────────────────────┼─────────────────────────┘
          │                              │
          ▼                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    H2 In-Memory Database                        │
│                    (jdbc:h2:mem:zipkindb)                       │
└─────────────────────────────────────────────────────────────────┘
          │
          │ Trace Data (via Micrometer)
          ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Zipkin Server                              │
│                   (http://localhost:9411)                       │
│                                                                 │
│  - Collects trace spans from all layers                        │
│  - Visualizes distributed traces                               │
│  - Shows service dependencies                                  │
└─────────────────────────────────────────────────────────────────┘
```

## 🔍 How Zipkin Tracing Works

### Trace Flow Example: `GET /api/orders/1/with-user`

1. **HTTP Request arrives** at `OrderController`
   - Zipkin creates a **Trace ID** (unique identifier for the entire request)
   - Creates first **Span** for the HTTP request

2. **OrderController** calls `OrderService.getOrderWithUserDetails()`
   - New **Span** created for service method
   - Inherits the same **Trace ID**

3. **OrderService** calls `UserService.getUserById()`
   - **Service-to-Service Call** - New **Span** created
   - Same **Trace ID** propagated (this is the key to distributed tracing!)

4. **Database Queries** executed via JPA
   - Additional **Spans** for each database operation
   - All under the same **Trace ID**

5. **Response flows back** through the layers
   - Each span records its duration
   - All spans sent to Zipkin asynchronously

### Key Concepts

- **Trace**: End-to-end journey of a request (identified by Trace ID)
- **Span**: Individual operation within a trace (e.g., HTTP call, service method, DB query)
- **Trace ID**: Unique identifier shared across all spans in a trace
- **Span ID**: Unique identifier for each individual span
- **Parent Span**: The span that initiated a child span (creates hierarchy)

## 🚀 Quick Start

### Prerequisites
- Java 22
- Maven 3.6+
- Docker (for Zipkin)

### 1. Start Zipkin Server
```bash
docker run -d -p 9411:9411 openzipkin/zipkin
```

### 2. Build and Run Application
```bash
# Set JAVA_HOME (if not already set)
export JAVA_HOME=$(/usr/libexec/java_home -v 22)

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### 3. Access Points
- **Application**: http://localhost:8080
- **Zipkin UI**: http://localhost:9411
- **H2 Console**: http://localhost:8080/h2-console
- **Actuator**: http://localhost:8080/actuator
- **Prometheus**: http://localhost:8080/actuator/prometheus

## 📦 Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 3.4.1 | Application framework |
| Java | 22 | Programming language |
| Micrometer Tracing | 1.4.1 | Tracing abstraction |
| Brave | (managed) | Zipkin tracer implementation |
| Zipkin Reporter | (managed) | Sends traces to Zipkin |
| Spring Data JPA | 3.4.1 | Database access |
| H2 Database | (managed) | In-memory database |
| Lombok | (managed) | Reduce boilerplate code |
| Prometheus | (managed) | Metrics collection |

## 🎯 Key Features

### 1. Multi-Layer Architecture
- **Controllers**: Handle HTTP requests
- **Services**: Business logic with service-to-service calls
- **Repositories**: Data access layer
- **Database**: H2 in-memory storage

### 2. Distributed Tracing
- Automatic trace propagation across layers
- Service-to-service call tracking
- Database query tracing
- 100% sampling rate (all requests traced)

### 3. Service-to-Service Communication
- `OrderService` → `UserService` calls demonstrate distributed tracing
- Trace context automatically propagated
- Parent-child span relationships maintained

## 📊 Project Structure

```
zipkin-tracking/
├── src/main/java/com/example/zipkintracking/
│   ├── ZipkinTrackingApplication.java    # Main application class
│   ├── controller/
│   │   ├── HelloController.java          # Simple hello endpoint
│   │   ├── UserController.java           # User CRUD endpoints
│   │   └── OrderController.java          # Order CRUD + distributed tracing endpoints
│   ├── service/
│   │   ├── UserService.java              # User business logic
│   │   └── OrderService.java             # Order logic + calls UserService
│   ├── repository/
│   │   ├── UserRepository.java           # User data access
│   │   └── OrderRepository.java          # Order data access
│   ├── entity/
│   │   ├── User.java                     # User entity (JPA)
│   │   └── Order.java                    # Order entity (JPA)
│   └── config/
│       └── DataLoader.java               # Loads sample data on startup
├── src/main/resources/
│   └── application.properties            # Application configuration
├── pom.xml                               # Maven dependencies
├── README.md                             # This file
└── API_TESTING_GUIDE.md                  # API testing documentation
```

## ⚙️ Configuration

### application.properties

```properties
# Application Name (appears in Zipkin as service name)
spring.application.name=zipkin-tracking

# Server Configuration
server.port=8080

# Zipkin Configuration
management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans
management.tracing.sampling.probability=1.0  # 100% sampling (trace all requests)

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always

# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:zipkindb
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### Key Configuration Explained

| Property | Value | Purpose |
|----------|-------|---------|
| `spring.application.name` | zipkin-tracking | Service name in Zipkin UI |
| `management.tracing.sampling.probability` | 1.0 | Trace 100% of requests (use 0.1 for 10% in production) |
| `management.zipkin.tracing.endpoint` | http://localhost:9411/api/v2/spans | Where to send trace data |
| `spring.jpa.show-sql` | true | Show SQL queries in logs (helps see what's being traced) |

## 🧪 Testing Distributed Tracing

### Example 1: Simple Request (Single Service)
```bash
curl http://localhost:8080/api/users
```
**Trace will show:**
- 1 HTTP span (GET /api/users)
- 1 Service span (UserService.getAllUsers)
- 1 Database span (SELECT query)

### Example 2: Service-to-Service Call
```bash
curl http://localhost:8080/api/orders/1/with-user
```
**Trace will show:**
- 1 HTTP span (GET /api/orders/{id}/with-user)
- 1 Service span (OrderService.getOrderWithUserDetails)
- 1 Database span (SELECT order)
- **1 Service-to-Service span (OrderService → UserService.getUserById)**
- 1 Database span (SELECT user)

### Example 3: Create Order (Validation Flow)
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "productName": "Laptop", "amount": 999.99}'
```
**Trace will show:**
- 1 HTTP span (POST /api/orders)
- 1 Service span (OrderService.createOrder)
- **1 Service-to-Service span (OrderService → UserService.getUserById for validation)**
- 1 Database span (SELECT user - validation)
- 1 Database span (INSERT order)

## 🔬 Understanding Zipkin UI

### Viewing Traces
1. Open http://localhost:9411
2. Click **"Run Query"** to see recent traces
3. Each row represents one complete trace (one request)
4. Click on a trace to see detailed span timeline

### Trace Details View
- **Timeline**: Visual representation of span durations
- **Span Hierarchy**: Parent-child relationships
- **Tags**: Metadata (HTTP method, URL, status code, etc.)
- **Annotations**: Timing events within spans

### Service Dependencies
- Click **"Dependencies"** in Zipkin UI
- See visual graph: `OrderService` → `UserService`
- Shows how services communicate

## 📝 Sample Data

The application loads sample data on startup via `DataLoader.java`:

### Users
| ID | Name | Email | City |
|----|------|-------|------|
| 1 | John Doe | john.doe@example.com | New York |
| 2 | Jane Smith | jane.smith@example.com | Los Angeles |
| 3 | Bob Johnson | bob.johnson@example.com | Chicago |
| 4 | Alice Williams | alice.williams@example.com | Houston |

### Orders
| ID | User ID | Product | Amount |
|----|---------|---------|--------|
| 1 | 1 | Laptop | $1,299.99 |
| 2 | 1 | Mouse | $29.99 |
| 3 | 2 | Keyboard | $89.99 |
| 4 | 2 | Monitor | $399.99 |
| 5 | 3 | Headphones | $149.99 |
| 6 | 4 | Webcam | $79.99 |

## 🎓 Learning Objectives

This project demonstrates:

1. **Distributed Tracing Fundamentals**
   - How traces flow through multiple layers
   - Trace ID and Span ID propagation
   - Parent-child span relationships

2. **Zipkin Integration**
   - Automatic instrumentation with Micrometer
   - Brave tracer implementation
   - Sending traces to Zipkin server

3. **Service-to-Service Tracing**
   - How trace context propagates between services
   - Visualizing service dependencies
   - Understanding latency across services

4. **Spring Boot Observability**
   - Actuator endpoints for monitoring
   - Prometheus metrics integration
   - Health checks and application info

## 🚨 Troubleshooting

### Traces not appearing in Zipkin?
1. Check Zipkin is running: `curl http://localhost:9411/health`
2. Verify sampling probability is set to 1.0
3. Check application logs for connection errors
4. Ensure `management.zipkin.tracing.endpoint` is correct

### Application won't start?
1. Verify Java 22 is installed: `java -version`
2. Check JAVA_HOME is set: `echo $JAVA_HOME`
3. Ensure port 8080 is not in use: `lsof -i :8080`

### Database errors?
1. H2 is in-memory - data resets on restart (this is expected)
2. Check H2 console: http://localhost:8080/h2-console
3. JDBC URL: `jdbc:h2:mem:zipkindb`, User: `sa`, Password: (empty)

## 📚 Additional Resources

- [Zipkin Documentation](https://zipkin.io/)
- [Micrometer Tracing](https://micrometer.io/docs/tracing)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Distributed Tracing Concepts](https://opentelemetry.io/docs/concepts/observability-primer/)

## 📄 License

This is a learning/demo project for understanding Zipkin distributed tracing.

## 👤 Author

**soni-world**

---

**Happy Tracing! 🔍✨**

