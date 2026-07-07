# Transfer Tourist Ohrid — Backend

Spring Boot REST API for the transfer-booking platform, organized
**package-by-layer** (`config`, `controller`, `service`, `repository`, `entity`,
`dto`, `mapper`, `exception`, `validation`, `security`, `util`, `constants`).
This is the Phase 2 backend the React frontend (`../frontend`) will call once it
is flipped off its mock handlers.

> **Status:** Milestone 2.1 (foundation) — config, global exception handling,
> `/api/v1/ping` liveness, actuator health, Flyway baseline, Swagger, profiles,
> context test. Domain layers arrive in Milestones 2.2+. See
> `../implementation_plan.md` §7.2 and §18.

## Prerequisites

- **JDK 21** (Temurin/Adoptium or equivalent)
- **Maven 3.9+**
- **PostgreSQL 16+** (Docker, or a native instance)

## Stack

- Spring Boot 3.3, Java 21
- Spring Web, Spring Data JPA, Bean Validation
- PostgreSQL + Flyway migrations (`src/main/resources/db/migration`)
- springdoc-openapi (Swagger UI)

## Run locally

Pick **one** database option:

**A) Docker (isolated):**
```bash
docker compose up -d          # Postgres with the transfer/transfer credentials
```

**B) Existing native Postgres:** create the role + DB once:
```bash
psql -U postgres -h localhost -f db/bootstrap.sql
```

Then run the app (dev profile is the default):
```bash
mvn spring-boot:run

# Verify (note: plain HTTP, not HTTPS)
curl http://localhost:8080/api/v1/ping        # -> {"status":"ok",...}
curl http://localhost:8080/actuator/health     # -> {"status":"UP"}
# Swagger UI:  http://localhost:8080/swagger-ui.html
```

## Build & test

```bash
mvn clean verify        # compiles + context-load test (H2, no DB needed)
mvn clean package       # produces target/transfer-tourist-0.0.1-SNAPSHOT.jar
```

## Configuration

| Property / env var            | Default (dev)                                       | Purpose                     |
|-------------------------------|-----------------------------------------------------|-----------------------------|
| `SPRING_PROFILES_ACTIVE`      | `dev`                                               | Active profile (`dev`/`prod`) |
| `DB_URL`                      | `jdbc:postgresql://localhost:5432/transfer_tourist` | JDBC URL                    |
| `DB_USERNAME` / `DB_PASSWORD` | `transfer` / `transfer`                             | DB credentials              |
| `SERVER_PORT`                 | `8080`                                              | HTTP port                   |
| `APP_CORS_ALLOWED_ORIGINS`    | `http://localhost:5173`                             | Comma-separated SPA origins |
