# Employee Management System – Spring Boot REST API

A backend application for managing employees, departments and designations.
Built with **Core Java, Spring Boot, Spring Data JPA, Hibernate ORM, MySQL, RESTful APIs, Maven** and tested with **Postman**.

## Features

| Module | Features |
|---|---|
| Employee Management | Add, update, delete and retrieve employees |
| Department / Designation Management | Create and manage departments and designations |
| Database Integration | MySQL tables with JPA entity relationships |
| Search | Search employees by ID, name and department |
| REST APIs | GET, POST, PUT, PATCH, DELETE |
| Validation | Bean Validation (`@NotBlank`, `@Email`, `@Positive`, …) on request bodies |
| Exception Handling | Global handling with `@ControllerAdvice` – consistent JSON errors |
| ORM | Entity mapping using Hibernate & JPA |

## Database design (`employee_management`)

```
departments (department_id PK, department_name UNIQUE)
designations (designation_id PK, designation_name UNIQUE)
employees (employee_id PK, name, email UNIQUE, salary, department_id FK, designation_id FK)
employee_addresses (address_id PK, employee_id FK UNIQUE, city, state, country)
```

* Department → Employee : **One-to-Many**
* Designation → Employee : **One-to-Many**
* Employee → Address : **One-to-One** (address is deleted with its employee)

## Project structure

```
src/main/java/com/ems
├── controller   REST controllers
├── service      business logic & transactions
├── repository   Spring Data JPA repositories (+ search Specification)
├── entity       JPA entities
├── dto          request / response records with validation rules
├── mapper       entity → DTO mapping
└── exception    custom exceptions + @ControllerAdvice handler
```

## Getting started

**Prerequisites:** JDK 17+, Maven 3.8+, MySQL 8+

1. Start MySQL. The database is created automatically (`createDatabaseIfNotExist=true`), and Hibernate creates the tables.
   (Or run `database/schema.sql` manually.)
2. Set your credentials – either edit `src/main/resources/application.properties` or use env vars:
   ```bash
   export DB_USERNAME=root
   export DB_PASSWORD=your_password
   ```
3. Run:
   ```bash
   mvn spring-boot:run
   ```
   The API is available at `http://localhost:8080`.
4. Import `postman/Employee-Management-System.postman_collection.json` into Postman.

Run tests (in-memory H2, no MySQL needed): `mvn test`

## API reference

### Departments – `/api/departments`  ·  Designations – `/api/designations`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/departments` | Create `{"name": "Engineering"}` |
| GET | `/api/departments` | List all |
| GET | `/api/departments/{id}` | Get one |
| PUT | `/api/departments/{id}` | Rename |
| DELETE | `/api/departments/{id}` | Delete (409 if employees still assigned) |

### Employees – `/api/employees`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/employees` | Create employee |
| GET | `/api/employees` | List all |
| GET | `/api/employees/{id}` | Search by ID |
| GET | `/api/employees/search?name=&departmentId=&departmentName=` | Search (all params optional, combined with AND, name matching is case-insensitive "contains") |
| PUT | `/api/employees/{id}` | Replace all fields |
| PATCH | `/api/employees/{id}` | Update only the fields sent |
| DELETE | `/api/employees/{id}` | Delete employee and address |

Create employee – request:
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "salary": 55000.50,
  "departmentId": 1,
  "designationId": 1,
  "address": { "city": "Pune", "state": "Maharashtra", "country": "India" }
}
```

Response (`201 Created`):
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "salary": 55000.50,
  "department": { "id": 1, "name": "Engineering" },
  "designation": { "id": 1, "name": "Software Engineer" },
  "address": { "id": 1, "city": "Pune", "state": "Maharashtra", "country": "India" }
}
```

### Error format
```json
{
  "timestamp": "2026-10-06T19:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/employees",
  "validationErrors": { "email": "Email must be a valid email address" }
}
```

| Status | When |
|---|---|
| 400 | Validation failure, malformed JSON, invalid parameter type |
| 404 | Employee / department / designation not found |
| 409 | Duplicate email or name, or deleting a department/designation still in use |
| 500 | Unexpected error (details logged, not leaked) |

## Possible enhancements
Pagination & sorting, Spring Security (JWT, admin role), Swagger/OpenAPI docs, Docker Compose with MySQL, Flyway migrations.
