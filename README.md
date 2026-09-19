# url-shortener-service

Minimal REST API foundation using Java 21, Spring Boot 3, and Maven.
URL shortening is not implemented yet.

## Run locally

Requires Java 21 and Maven.

```sh
mvn spring-boot:run
```

`GET http://localhost:8080/api/health` returns HTTP 200 with:

```json
{"status":"UP"}
```

Local development and tests use an in-memory H2 database. Data is discarded
when the application stops.

## Run tests

```sh
mvn test
```
