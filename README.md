# Lending System - Microservices Architecture

A Spring Cloud microservices-based digital lending platform built with Java 21 and Spring Boot 3.4.3.

## Architecture Overview

The system follows an event-driven microservices architecture with centralized configuration, service discovery, and API gateway routing.

```
                    ┌──────────────┐
                    │  API Gateway │ :8080
                    └──────┬───────┘
                           │
         ┌─────────────────┼─────────────────┐
         │                 │                  │
┌────────▼──────┐ ┌───────▼───────┐ ┌────────▼──────┐ ┌──────────────────┐
│Product Service│ │Customer Service│ │ Loan Service  │ │Notification Svc  │
│    :8081      │ │    :8082      │ │    :8083      │ │    :8084         │
└───────────────┘ └───────────────┘ └───────┬───────┘ └────────▲─────────┘
                                            │                  │
                                     Kafka (loan.events) ──────┘
```

### Infrastructure Services

| Service | Port | Description |
|---------|------|-------------|
| Discovery Service (Eureka) | 8761 | Service registry and discovery |
| Config Server | 8888 | Centralized configuration management |
| API Gateway | 8080 | Single entry point, routes to all services |
| Zipkin | 9411 | Distributed tracing |

### Business Services

| Service | Port | Description |
|---------|------|-------------|
| Product Service | 8081 | Lending product catalog and fee configuration |
| Customer Service | 8082 | Customer profiles and loan limit management |
| Loan Service | 8083 | Loan lifecycle, repayments, billing, scheduled jobs |
| Notification Service | 8084 | Multi-channel notifications (Email, SMS, Push) |

## Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot 3.4.3, Spring Cloud 2024.0.0
- **Database:** PostgreSQL 16
- **Messaging:** Apache Kafka (loan events), RabbitMQ (notification routing)
- **Service Discovery:** Netflix Eureka
- **API Gateway:** Spring Cloud Gateway
- **Configuration:** Spring Cloud Config Server (native profile)
- **ORM:** Spring Data JPA with Hibernate
- **Migrations:** Flyway
- **Mapping:** MapStruct 1.5.5
- **API Docs:** SpringDoc OpenAPI (Swagger UI)
- **Tracing:** Zipkin
- **Build:** Maven (multi-module)
- **Containerization:** Docker Compose

## Module Details

### Product Service
- CRUD operations for lending products
- Fee configuration per product (SERVICE, DAILY, LATE fee types)
- Supports FIXED and PERCENTAGE calculation methods
- Loan structures: LUMP_SUM and INSTALLMENT
- Configurable tenure (DAYS, WEEKS, MONTHS)
- Publishes product events to Kafka

### Customer Service
- Customer registration and profile management
- Loan limit management (set, check, increase/decrease)
- Uniqueness validation (email, phone, national ID)
- Customer status management (ACTIVE, INACTIVE, SUSPENDED, BLACKLISTED)
- Publishes customer events to Kafka

### Loan Service
- **Loan Disbursement:** Validates product and customer eligibility, calculates fees, generates installment schedules
- **Repayments:** Accepts payments, allocates to installments, auto-closes fully paid loans
- **State Machine:** OPEN → CLOSED/OVERDUE/CANCELLED; OVERDUE → CLOSED/WRITTEN_OFF
- **Billing Summary:** Consolidated view of customer's loans (active, overdue, totals)
- **Scheduled Jobs:**
  - **OverDueSweepJob** (1:00 AM): Marks past-due OPEN loans as OVERDUE
  - **DailyFeeAccrualJob** (12:30 AM): Accrues daily fees on outstanding balances
  - **WriteOffSweepJob** (3:00 AM): Writes off loans overdue beyond configurable threshold (default 90 days)
- Inter-service communication via OpenFeign (product validation, customer limit checks)

### Notification Service
- **Event-Driven:** Consumes loan events from Kafka
- **Template Engine:** Mustache-style variable substitution (e.g., `{{firstName}}`, `{{loanId}}`)
- **Rule-Based Routing:** Notification rules determine which channels to use per event type
- **Multi-Channel Delivery:** Email, SMS, Push via RabbitMQ queues
- **Retry & Dead Letter:** Failed notifications retry with 5-minute TTL, max 3 retries
- **Notification Logging:** Full audit trail of all notifications sent/failed
- CRUD for templates and rules

## API Endpoints

### Product Service (`/api/v1/products`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | List all products |
| GET | `/{id}` | Get product by ID |
| POST | `/` | Create product |
| DELETE | `/{id}` | Delete product |
| POST | `/{productId}/fees` | Add fee to product |
| PUT | `/{productId}/fees/{feeId}` | Update fee |
| DELETE | `/{productId}/fees/{feeId}` | Remove fee |

### Customer Service (`/api/v1/customers`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | List all customers |
| GET | `/{id}` | Get customer by ID |
| POST | `/` | Create customer |
| PUT | `/{id}` | Update customer |
| PUT | `/{customerId}/loan-limit` | Set loan limit |
| GET | `/{customerId}/loan-limit` | Get loan limit |
| POST | `/{customerId}/loan-limit/check` | Check loan eligibility |

### Loan Service (`/api/v1/loans`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | List all loans |
| GET | `/{id}` | Get loan by ID |
| GET | `/customer/{customerId}` | Get customer's loans |
| POST | `/` | Disburse new loan |
| POST | `/{loanId}/repayments` | Make repayment |
| GET | `/customer/{customerId}/summary` | Get billing summary |

### Notification Service (`/api/v1/notification`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/rules` | Create notification rule |
| GET | `/rules` | List all rules |
| GET | `/customer/{customerId}` | Get customer notifications |
| GET | `/loan/{loanId}` | Get loan notifications |
| POST | `/api/templates` | Create template |
| GET | `/api/templates` | List all templates |
| GET | `/api/templates/{id}` | Get template |
| DELETE | `/api/templates/{id}` | Delete template |

## Getting Started

### Prerequisites
- Docker and Docker Compose
- Java 21 (for local development)
- Maven 3.9+

### Running with Docker Compose

```bash
# Build all services
mvn clean package -DskipTests

# Start the entire stack
docker compose up -d

# Check all services are running
docker compose ps
```

### Verifying the Setup

1. **Eureka Dashboard:** http://localhost:8761
2. **Swagger UI** (per service):
   - Product: http://localhost:8081/swagger-ui.html
   - Customer: http://localhost:8082/swagger-ui.html
   - Loan: http://localhost:8083/swagger-ui.html
   - Notification: http://localhost:8084/swagger-ui.html
3. **RabbitMQ Management:** http://localhost:15672 (guest/guest)
4. **Zipkin:** http://localhost:9411

### Sample API Calls (via Gateway)

```bash
# List products
curl http://localhost:8080/api/v1/products

# List customers
curl http://localhost:8080/api/v1/customers

# Disburse a loan
curl -X POST http://localhost:8080/api/v1/loans \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "c1a2b3c4-d5e6-7890-abcd-ef1234567890",
    "productId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "amount": 2000
  }'

# Make a repayment
curl -X POST http://localhost:8080/api/v1/loans/{loanId}/repayments \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 500,
    "paymentMethod": "MPESA",
    "paymentReference": "PAY-001"
  }'

# Get customer billing summary
curl http://localhost:8080/api/v1/loans/customer/{customerId}/summary
```

### Running Tests

```bash
mvn test
```

## Database

Each service has its own PostgreSQL database with Flyway-managed migrations:

| Service | Database | Port |
|---------|----------|------|
| Product | product_db | 5433 |
| Customer | customer_db | 5434 |
| Loan | loan_db | 5435 |
| Notification | notification_db | 5436 |

Seed data is automatically loaded via Flyway migrations on first startup.

## Event Flow

1. **Loan Disbursement** → Kafka `loan.events` → Notification Service → RabbitMQ → Email/SMS/Push queues
2. **Overdue Sweep** → Marks loans overdue → Kafka event → Notification sent
3. **Daily Fee Accrual** → Accrues fees → Kafka event → Notification sent
4. **Write-Off Sweep** → Writes off old overdue loans → Kafka event → Notification sent
5. **Repayment** → Updates loan/installments → If fully paid: closes loan → Kafka event → Notification sent
