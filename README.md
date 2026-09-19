# URL Shortener Service

A REST API built with Java 21 and Spring Boot 3 to create short URLs and redirect requests to their original destinations.

## Features

- Create short URLs with random 6-character alphanumeric codes (`A–Z`, `a–z`, `0–9`).
- HTTP 302 redirection to the original URL.
- URL validation for nonblank HTTP/HTTPS URLs.
- Short-code collision handling through a database uniqueness constraint and retry.
- Centralized exception handling for unknown short codes.
- H2 persistence through Spring Data JPA (in-memory; data is lost on shutdown).
- Swagger/OpenAPI documentation and automated tests.

## Tech Stack

- Java 21 and Spring Boot 3
- Spring Web, Spring Data JPA, and Bean Validation
- H2 and Maven
- JUnit 5 / Spring Boot Test
- Springdoc OpenAPI / Swagger UI

## Architecture

```text
Controller -> Service -> Repository -> H2 Database
```

Controllers expose the REST endpoints, the service creates and resolves short codes,
and the repository handles database access. Supporting packages separate **DTO**
request/response models, the **Entity** persistence model, **Validation** rules,
**Util** code generation, and **Exception** handling.

## Project Structure

```text
src/
├── main/
│   ├── java/com/shubhampanda/urlshortener/
│   │   ├── UrlShortenerApplication.java
│   │   ├── controller/
│   │   │   ├── HealthController.java
│   │   │   ├── RedirectController.java
│   │   │   └── UrlController.java
│   │   ├── dto/
│   │   │   ├── CreateUrlRequest.java
│   │   │   ├── CreateUrlResponse.java
│   │   │   └── ErrorResponse.java
│   │   ├── entity/Url.java
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── UrlNotFoundException.java
│   │   ├── repository/UrlRepository.java
│   │   ├── service/UrlService.java
│   │   ├── util/ShortCodeGenerator.java
│   │   └── validation/
│   │       ├── HttpUrl.java
│   │       └── HttpUrlValidator.java
│   └── resources/application.properties
└── test/java/com/shubhampanda/urlshortener/
    ├── controller/
    │   ├── HealthControllerTest.java
    │   └── UrlControllerTest.java
    └── util/ShortCodeGeneratorTest.java
```

## API Endpoints

Base URL: `http://localhost:8080`

| Method | Endpoint | Request | Response |
| --- | --- | --- | --- |
| POST | `/api/urls` | `{"url":"https://example.com/long/path"}` | `201 Created`: `{"originalUrl":"https://example.com/long/path","shortCode":"aB3xY7"}` |
| GET | `/{shortCode}` | `/aB3xY7` | `302 Found` with `Location: https://example.com/long/path` and an empty body |
| GET | `/api/health` | No body | `200 OK`: `{"status":"UP"}` |

The short code above is illustrative. Missing or invalid URLs return `400 Bad Request`.
Unknown short codes return `404 Not Found`, for example:

```json
{"message":"URL not found for short code: aB3xY7"}
```

## Running Locally

Prerequisites: **Java 21** and **Maven**.

From the repository root:

```sh
mvn clean test
mvn spring-boot:run
```

The application runs at `http://localhost:8080`. H2 runs in memory, so no separate
database setup is required.

## Swagger

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

Use **Try it out** to explore the endpoints. Browsers and Swagger UI may follow
the external 302 redirect; use `curl -i` without `-L` to inspect the `Location`
header directly.

## Testing

```sh
mvn test
```

JUnit 5 and Spring Boot Test with MockMvc cover URL creation and H2 persistence,
HTTP 302 redirects and their `Location` headers, invalid URL rejection, unknown-code
404 responses, collision retries, and the health endpoint. A unit test checks the
six-character alphanumeric format of generated codes.

## Example End-to-End Usage

Create a short URL:

```sh
curl -i -X POST http://localhost:8080/api/urls \
  -H 'Content-Type: application/json' \
  -d '{"url":"https://example.com/long/path"}'
```

Example response (headers abbreviated):

```http
HTTP/1.1 201 Created
Content-Type: application/json

{"originalUrl":"https://example.com/long/path","shortCode":"aB3xY7"}
```

Use the returned `shortCode` in place of `aB3xY7`:

```sh
curl -i http://localhost:8080/aB3xY7
```

Expected response (headers abbreviated):

```http
HTTP/1.1 302 Found
Location: https://example.com/long/path
```

## Future Improvements

Potential additions, not currently implemented:

- PostgreSQL
- Docker
- URL expiration
- Click analytics
- Redis caching
