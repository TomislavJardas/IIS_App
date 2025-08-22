# IIS App

This project includes the Spring Boot backend and a WPF desktop client for interacting with the NBA and weather services.

## Backend

Run the backend with Maven:

```bash
./mvnw spring-boot:run
```

## Desktop Client

The WPF client is located in `ClientApp`.

### Build and run

Ensure the environment has the .NET SDK installed (8.0 for Windows Desktop).

```bash
cd ClientApp
dotnet build
dotnet run
```

### Features
- **Login** via `/api/auth/login` with token refresh.
- **Player search** from `GET /players` with optional filters.
- **Upload XML** to `/validateAndSaveXml` and show validation result.
- **CRUD** operations against `/api/players` using stored JWT token.
- **SOAP search** against `http://localhost:8080/ws`.
- **XML-RPC weather** queries against `http://localhost:9090/RPC2`.
- **Configuration** panel for `RAPIDAPI_KEY` which overrides the backend key.

### Notes
The client requires the backend to be running locally on port `8080` and the XML-RPC weather service on port `9090`.
