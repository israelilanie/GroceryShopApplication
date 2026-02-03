# Grocery Shop Application

A Spring Boot backend for managing grocery items, inventory, and customer orders. It includes REST APIs, validation, and a simple order workflow so you can track items from stocking to delivery.

## ✨ Features

- Grocery item catalog with CRUD endpoints
- Inventory tracking with stock adjustments
- Order creation and status updates (pending → processing → shipped → delivered)
- H2 in-memory database for easy local development
- Basic authentication for write operations
- Dockerfile, Docker Compose, and CI workflow included

## 🚀 Getting Started

### Prerequisites

- Java 25
- Maven (or use the included Maven wrapper)

### Run locally

```bash
./mvnw spring-boot:run
```

The service will be available at `http://localhost:8080`.

### Run with Docker

```bash
docker build -t grocery-shop .
docker run -p 8080:8080 grocery-shop
```

### Run with Docker Compose

```bash
docker compose up --build
```

## 🔐 Authentication

- **Read-only** endpoints for grocery items are public.
- **Write** operations require basic auth.

Default credentials (configurable in `application.properties`):

- **Username:** `admin`
- **Password:** `admin123`

## 📚 API Overview

| Method | Endpoint | Description | Auth |
| --- | --- | --- | --- |
| GET | `/api/items` | List grocery items | No |
| GET | `/api/items/{id}` | Get grocery item | No |
| POST | `/api/items` | Create grocery item | Yes |
| PUT | `/api/items/{id}` | Update grocery item | Yes |
| DELETE | `/api/items/{id}` | Delete grocery item | Yes |
| GET | `/api/inventory` | List inventory | Yes |
| POST | `/api/inventory/adjust` | Set inventory quantity | Yes |
| GET | `/api/orders` | List orders | Yes |
| GET | `/api/orders/{id}` | Get order | Yes |
| POST | `/api/orders` | Create order | Yes |
| PUT | `/api/orders/{id}/status` | Update order status | Yes |

### Example Requests

Create a new item:

```bash
curl -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{"name":"Spinach","unitPrice":2.75,"category":"Vegetables","description":"Fresh spinach"}' \
  http://localhost:8080/api/items
```

Adjust inventory:

```bash
curl -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{"groceryItemId":1,"quantity":120}' \
  http://localhost:8080/api/inventory/adjust
```

Create an order:

```bash
curl -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Riley","items":[{"groceryItemId":1,"quantity":2}]}' \
  http://localhost:8080/api/orders
```

## 🧪 Testing

```bash
./mvnw test
```

## 🗃️ H2 Console

The in-memory database console is enabled at: `http://localhost:8080/h2-console`

JDBC URL: `jdbc:h2:mem:grocerydb`

## 📦 CI

GitHub Actions workflow is located at `.github/workflows/ci.yml` and runs Maven tests on pull requests and pushes to `main`.
