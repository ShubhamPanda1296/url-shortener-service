# url-shortener-service

Minimal REST API foundation using Java 21, Spring Boot 3, and Maven.
Supports creating and storing short URL codes in H2.

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

## Create a short URL

```sh
curl -i -X POST http://localhost:8080/api/urls \
  -H 'Content-Type: application/json' \
  -d '{"url":"https://www.example.com/some/long/path"}'
```

Returns HTTP 201 with the original URL and a random six-character code:

```json
{"originalUrl":"https://www.example.com/some/long/path","shortCode":"aB3xY7"}
```

Blank values and invalid HTTP/HTTPS URLs return HTTP 400. Codes use uppercase
letters, lowercase letters, and digits. A database uniqueness constraint prevents
duplicate codes; the service retries with a new code after a collision.
Redirects are not implemented.

## Run tests

```sh
mvn test
```
