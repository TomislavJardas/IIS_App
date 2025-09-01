# IIS App

Spring Boot backend exposing NBA player statistics, XML validation, SOAP search, and an XML-RPC weather service.

## Backend

Run the backend with Maven:

```bash
./mvnw spring-boot:run
```

## Features

- **Authentication** via `/api/auth/login` with refresh token support.
- **Player search** from `GET /players` with optional filters.
- **Upload XML** to `/validateAndSaveXml` and show validation result.
- **CRUD** operations against `/api/players` using JWT authorization.
- **SOAP search** against `http://localhost:8080/ws`.
- **XML-RPC weather** queries against `http://localhost:9090/RPC2`.

## Notes

The backend uses the NBA RapidAPI service. Set the `RAPIDAPI_KEY` environment variable to supply your own key; otherwise a default key is used.
