# Brickshop E-commerce Application

A self-learning e-commerce project built with Spring Boot, JPA, and Spring Security.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Database](#database)
- [Security & Roles](#security--roles)
- [API Endpoints](#api-endpoints)
- [Running Tests](#running-tests)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

It's a simple ecommerce platform with order management, packing employee interface and role based authorization. It allows to add new product with selected categories, allows anonymous users to explore products page and add the products to cart, but for checkout and order making (on purpose with mock payments) needs a free registration. Free registration allows for password reset, password recovery, choosing a role (Moderator can peek admin flow and possibilities for demo purposes, but cannot persist and access user information, can use the packing interface too). 
The project works locally with a local by hand managed database, as well as docker compose service and database setup depending of the configuration and spring profile. Superuser has runtime access to banning specific ip's and logging filters configuration.
For not allowing bots to register the app is using the prototype of my own captcha service (another deployed project)

## Features

- User registration & authentication
- Role-based authorization (e.g., user, admin, superuser, moderator)
- Product catalog management
- Shopping cart & checkout
- Order management
- Image upload for products
- RESTful API endpoints
- Error handling with standardized JSON responses

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA (Hibernate)
- Spring Security
- PostgreSQL (or your DB)
- Thymeleaf (if using server-side rendering)
- Docker (optional)
- Other libraries: (list here)

## Getting Started

### Prerequisites

- Java 21+
- Maven
- PostgreSQL (or your DB)
- Docker (optional)

### Installation

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/brickshop.git
   cd brickshop
   ```
2. Configure environment variables or application.yml files.
3. Build the project:
   ```
   ./mvnw clean install
   ```
4. Run the application:
   ```
   ./mvnw spring-boot:run
   ```

## Configuration

- Application properties are in `src/main/resources/application.yml` (or `.properties`).
- Environment variables for DB, mail, etc.
- Example configuration:
  ```
  DB_HOST=localhost
  DB_PORT=5432
  DB_NAME=brickshopdb
  DB_USER=brickshop_app
  DB_PASSWORD=yourpassword
  ```

## Database

- Uses JPA/Hibernate for ORM.
- Database migrations via Flyway/Liquibase (if used).

## Security & Roles

- Spring Security for authentication and authorization.
- Roles: (SUPERUSER, ADMIN, PACKER, MODERATOR, USER)
- Session-based authentication (specify which).
- Custom error handling for unauthorized/forbidden access.

## API Endpoints

For flexibility and futureproofing the frontend works by calling api endpoints. There is a lot of them,
the code explains itself and endpoints are secured by Authorization check, for example the moderator role for peeking,
to admin methods has ability to get more data than a casul user, but can't post the data and modify the data.

## Running Tests

- To run unit and integration tests:
  ```
  ./mvnw test
  ```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
