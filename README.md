# IIS App

This project is a Spring Boot backend that exposes NBA statistics, validates XML with XSD, supports SOAP-based player search, and offers an XML-RPC weather service.

## Backend

Run the backend with Maven:

```bash
./mvnw spring-boot:run
```

### Features
- **Login** via `/api/auth/login` with token refresh.
- **Player search** from `GET /players` with optional filters.
- **Upload XML** to `/validateAndSaveXml` and show validation result.
- **CRUD** operations against `/api/players/**` using stored JWT token.
- **SOAP search** against `http://localhost:8080/ws`.
- **XML-RPC weather** queries against `http://localhost:9090/RPC2`.

### Configuration
Set the `RAPIDAPI_KEY` environment variable to configure the RapidAPI key for external services.
