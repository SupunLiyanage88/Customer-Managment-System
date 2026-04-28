# Customer Management System

Spring Boot customer management application built for Java 8 and backed by an embedded H2 database.

## Requirements

- Java 8
- Maven 3.8+

## Run

```bash
mvn spring-boot:run
```

The application starts with an in-memory H2 database, loads the schema from `src/main/resources/schema.sql`, and seeds master data plus one sample customer from `src/main/resources/data.sql`.

## Test

```bash
mvn test
```

The test suite includes a startup test that verifies the schema and seed data load correctly.

## Database

- H2 console: `/h2-console`
- JDBC URL: `jdbc:h2:mem:cms`
- Username: `sa`
- Password: empty

## API

- `GET /api/customers`
- `GET /api/customers/{id}`
- `GET /api/customers/by-nic/{nicNumber}`
- `POST /api/customers`
- `PUT /api/customers/{id}`
- `POST /api/customers/bulk/create`
- `POST /api/customers/bulk/update`
