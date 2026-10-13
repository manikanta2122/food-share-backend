# FoodShare Backend API

Spring Boot REST API backend service for the FoodShare platform.

## Features
- User Authentication & Authorization (JWT & Spring Security)
- Food Donation Management (Post donations, upload pictures, track statuses)
- Donation Claim Workflow (Donors & Claimants)
- Role-based Access Control (DONOR, CLAIMANT, ADMIN)
- PostgreSQL Database Integration
- Docker & Render deployment ready

## Tech Stack
- Java 17+
- Spring Boot 3
- Spring Security + JJWT
- Spring Data JPA + Hibernate
- PostgreSQL
- Maven
- Docker

## Getting Started

### Prerequisites
- JDK 17 or higher
- Maven 3.8+
- PostgreSQL database

### Run Locally
```bash
mvn clean spring-boot:run
```

The application starts on `http://localhost:8080`.
