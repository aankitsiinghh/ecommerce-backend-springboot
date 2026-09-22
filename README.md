# E-Commerce Backend API

A full-featured e-commerce REST API built with **Spring Boot**, **Spring Security**, and **JWT authentication**. Supports role-based access (Buyer/Seller/Admin), OTP-based email verification, product catalog, cart, order & payment processing, and reviews — backed by MySQL and JPA/Hibernate.

## Tech Stack

- **Java 24**
- **Spring Boot 4.1.0**
- **Spring Security** + **JWT** (jjwt)
- **MySQL** + **Spring Data JPA / Hibernate**
- **Lombok**
- **springdoc-openapi** (Swagger UI)
- **Spring Mail** (Gmail SMTP for OTP emails)

## Features

- **Authentication** — Register/login with JWT, OTP-based email verification before account activation
- **Role-based access control** — BUYER, SELLER, and ADMIN roles with endpoint-level authorization
- **Category management** — Admin-managed product categories
- **Product catalog** — Sellers manage their own products; admins can manage any; public browsing with pagination
- **Cart** — Add, update, and remove cart items
- **Orders & Payments** — Create orders from selected cart items, track order status, process payments, view order history
- **Addresses** — Buyers manage shipping addresses, snapshotted onto orders at checkout
- **Reviews** — Buyers can review products
- **Pagination** — Applied across Product, Order, Review, and Category listing endpoints

## Getting Started

### Prerequisites

- JDK 24
- MySQL
- Maven (or use the included `mvnw` / `mvnw.cmd` wrapper)

### Environment Variables

| Variable            | Description                                                                                                                      |
| ------------------- | -------------------------------------------------------------------------------------------------------------------------------- |
| `DB_URL`            | MySQL connection URL, e.g. `jdbc:mysql://localhost:3306/e_commerce`                                                              |
| `DB_USERNAME`       | MySQL username, e.g. `root`                                                                                                      |
| `DB_PASSWORD`       | MySQL password                                                                                                                   |
| `JWT_SECRET`        | Secret key used to sign JWTs                                                                                                     |
| `MAIL_USERNAME`     | Gmail address used to send OTP emails                                                                                            |
| `MAIL_APP_PASSWORD` | Gmail App Password. **Do not use your regular Gmail password.** Generate one from **Google Account → Security → App Passwords**. |

### Run the app

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

## API Documentation

Interactive API documentation is available via Swagger UI once the app is running:

- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
- **Raw OpenAPI spec:** `http://localhost:8080/v3/api-docs`

Swagger UI lists every endpoint grouped by controller (Auth, Category, Product, Cart, Order, Address, Review) with request/response schemas and a built-in "Try it out" console for testing calls directly from the browser.

## Project Structure

```
src/main/java/com/example/E_Commerce/
├── config/          # OpenAPI and other app-level configuration
├── controller/       # REST controllers
├── dto/               # Request/response DTOs
├── exception/       # Custom exceptions and global exception handler
├── model/               # JPA entities
├── repository/       # Spring Data repositories
├── security/         # Spring Security + JWT configuration
└── service/           # Business logic
```
