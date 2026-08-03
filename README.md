# CatServer

A Ktor-based GraphQL server connected to a PostgreSQL database using JetBrains Exposed.

## Prerequisites

- **Java**: JDK 21 or higher.
- **PostgreSQL**: A running instance with a database named `catracker` on `localhost:5432` (or configured via environment variables).

## Configuration

The server supports configuration via the following environment variables:

- `PORT`: The port on which the Ktor server runs (default: `8080`).
- `JDBC_URL`: The JDBC URL for the PostgreSQL database (default: `jdbc:postgresql://localhost:5432/catracker`).
- `DB_USER`: Database username (default: `postgres`).
- `DB_PASSWORD`: Database password (default: `postgres`).

## Starting the Server

To compile and start the server, run:

```bash
./gradlew run
```

This will boot the Ktor server on the configured port (default `8080`).

## Connecting and Interacting

Once the server has successfully started, the following GraphQL endpoints are available:

- **GraphQL POST Endpoint**: `http://localhost:8080/graphql`
  - Accepts POST requests with JSON payloads (e.g., `{"query": "query { hello }"}`).
- **GraphQL GET Endpoint**: `http://localhost:8080/graphql`
  - Accepts GET requests.
- **Schema Definition Language (SDL)**: `http://localhost:8080/sdl`
  - Serves the generated GraphQL schema in SDL format.
- **GraphiQL Browser Console**: `http://localhost:8080/graphiql`
  - Open this URL in any web browser to access an interactive IDE for exploring and running queries on the GraphQL server.
