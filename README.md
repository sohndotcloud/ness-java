# Ness Backend (`ness-java`)

The API backend for [Ness](https://focus.sohn.cloud) — app with built-in habit tracking. Serves the habit-tracking data (habits, daily logs/streaks) and auth for the [frontend](https://github.com/sohndotcloud/ness-react).

> Repo: [github.com/sohndotcloud/ness-java](https://github.com/sohndotcloud/ness-java)

## Tech Stack

- **Java 21**
- **Spring Boot**
- **PostgreSQL**
- **Flyway**
- **Spring Security**
- **JWT**

## Getting Started

### Prerequisites

- Java 21
- Maven
- A local PostgreSQL instance (or point `spring.datasource.url` at a remote one)

### Local configuration

Local-only values (DB connection, JWT secret) live in `application-local.properties`, which layers on top of and overrides the base `application.properties`. This file is **not committed** — create it yourself:

```
src/main/resources/
├── application.properties
└── application-local.properties   # gitignored — create locally
```

```properties
# application-local.properties
jwt.secret=<your-local-secret>
spring.datasource.url=jdbc:postgresql://localhost:5432/ness_local
```

### Running locally

Activate the `local` profile via whichever method fits your workflow:

```bash
# Maven
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Or as a packaged jar
java -jar target/ness-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=local

# Or an env var
export SPRING_PROFILES_ACTIVE=local
```

In IntelliJ: **Run → Edit Configurations** → your Spring Boot config → **Active profiles** → `local`.

On startup failure, look for the `APPLICATION FAILED TO START` block in the console output — Maven's generic exit-code error at the bottom is never the actual cause, just a summary. Common culprits: an unresolved property (profile not actually active, or a property name mismatch), a port already in use, or the local Postgres instance not running.


## CI/CD

Builds run through GitHub Actions and/or AWS CodeBuild. If a workflow isn't triggering, check the workflow file's `on:` triggers first — pushes to a non-default branch or a path filter that doesn't match your changed files are the usual cause.
