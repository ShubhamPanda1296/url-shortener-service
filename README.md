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
## Resolve a short URL

Replace `aB3xY7` with the code returned by `POST /api/urls`:

```sh
curl -i http://localhost:8080/aB3xY7
```

Returns HTTP 302 with the original URL in the `Location` header. Open the short
URL in a browser or use `curl -L` to follow the redirect. Unknown codes return
HTTP 404 with a JSON error message.

## Swagger UI and OpenAPI

After starting the application with `mvn spring-boot:run`, open:

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

Expand an endpoint and select **Try it out**, enter the request body or short
code, then select **Execute**. For `POST /api/urls`, use
`{"url":"https://www.example.com/some/long/path"}`. Use the returned code to test
`GET /{shortCode}`. Swagger UI may follow redirects; use `curl -i` as shown above
to inspect the 302 response directly.

Test health with `curl -i http://localhost:8080/api/health`.

| API | Description | Expected responses |
| --- | --- | --- |
| `POST /api/urls` | Create a short URL | 201 created; 400 invalid request |
| `GET /{shortCode}` | Redirect to the original URL | 302 redirect; 404 unknown code |
| `GET /api/health` | Check application status | 200 running |

## Run tests

```sh
mvn test
```
