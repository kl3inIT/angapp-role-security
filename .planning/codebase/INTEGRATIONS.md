# External Integrations

**Analysis Date:** 2026-03-20

## APIs & External Services

**External APIs:**

- No third-party business APIs were found in the repository. The app mainly talks to its own Spring Boot REST endpoints from the Angular client.
- Swagger UI is bundled locally via `swagger-ui-dist` and `axios` and loads API docs from `/management/jhiopenapigroups` and `/v3/api-docs` in `src/main/webapp/swagger-ui/index.html`.

## Data Storage

**Databases:**

- Oracle Database - primary production database
  - Connection: `spring.datasource.url` in `src/main/resources/config/application-prod.yml`
  - Container/env wiring: `SPRING_DATASOURCE_URL` and `SPRING_LIQUIBASE_URL` in `src/main/docker/app.yml`
  - Client: Spring Data JPA, Hibernate, and HikariCP from `build.gradle`
  - Migrations: `src/main/resources/config/liquibase/master.xml` plus changelogs under `src/main/resources/config/liquibase/changelog/`
- H2 file database - local development and some tests
  - Connection: `spring.datasource.url` in `src/main/resources/config/application-dev.yml` and `src/test/resources/config/application.yml`
  - H2 console: enabled only in dev by `src/main/resources/config/application-dev.yml` and initialized in `src/main/java/com/mycompany/myapp/config/DatabaseConfiguration.java`
- Testcontainers Oracle XE - integration-test database option
  - Configured in `src/test/resources/config/application-testprod.yml`
  - Notes in file indicate an Oracle image must be provided through `testcontainers.properties`

**File Storage:**

- None detected.

**Caching:**

- No external cache service detected.
- Application-level caching is managed in-process with Spring/JHipster configuration; no Redis or Memcached integration was found.

## Authentication & Identity

**Auth Provider:**

- Custom JWT authentication, not an external IdP
  - Token issuance endpoint: `POST /api/authenticate` in `src/main/java/com/mycompany/myapp/web/rest/AuthenticateController.java`
  - Token validation: `src/main/java/com/mycompany/myapp/config/SecurityJwtConfiguration.java`
  - Security policy: `src/main/java/com/mycompany/myapp/config/SecurityConfiguration.java`
  - Secret source: `jhipster.security.authentication.jwt.base64-secret` in `src/main/resources/config/application.yml` and profile overrides; prod override is documented as an env var in `src/main/resources/config/application-prod.yml`
- Angular client stores the JWT in browser storage key `jhi-authenticationToken` via `src/main/webapp/app/core/auth/state-storage.service.ts`
- Login flow and bearer-token attachment are handled by `src/main/webapp/app/core/auth/auth-jwt.service.ts` and the interceptors under `src/main/webapp/app/core/interceptor/**`

**OAuth Integrations:**

- None detected. The backend uses Spring Security OAuth2 resource-server support for JWT bearer tokens, but there is no Google/Auth0/Okta/Keycloak integration in the repo.

## Monitoring & Observability

**Error Tracking:**

- No Sentry, Datadog, New Relic, or similar external error-tracking service was found.

**Analytics:**

- None detected.

**Logs:**

- Application logs go to stdout/stderr through Spring Boot logging.
- Optional logstash forwarding is present but disabled by default in `src/main/resources/config/application-dev.yml` and `application-prod.yml`.

## CI/CD & Deployment

**Hosting:**

- No cloud hosting provider is configured in-repo.
- Docker image support is provided through Gradle/Jib tasks surfaced in `package.json` and compose files in `src/main/docker/app.yml`.

**CI Pipeline:**

- No checked-in GitHub Actions workflow was found.
- Code quality and test tooling is configured locally through Gradle, Jest, ESLint, Prettier, SonarQube, and the docker compose files under `src/main/docker/`.

## Environment Configuration

**Development:**

- Required touchpoints include `SPRING_PROFILES_ACTIVE`, `SPRING_DATASOURCE_URL`, `SPRING_LIQUIBASE_URL`, and `JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET` in `src/main/docker/app.yml` and `src/main/docker/jhipster-control-center.yml`
- Frontend build/runtime variables come from `webpack/environment.js` (`SERVER_API_URL`, `__VERSION__`) and `src/main/webapp/environments/environment*.ts`
- Mail is configured for local SMTP in `src/main/resources/config/application-dev.yml` and test configs; the from/base URL live under `jhipster.mail.*`

**Production:**

- Production database and JWT secret are expected to be overridden outside the repo, despite default values present in `application-prod.yml`
- `jhipster.mail.base-url` in `src/main/resources/config/application-prod.yml` should match the deployed server URL

## Webhooks & Callbacks

**Incoming:**

- None detected.

**Outgoing:**

- None detected.

## Local Service Stack

- Prometheus scrapes `/management/prometheus` using `src/main/docker/prometheus/prometheus.yml`
- Grafana provisioning is in `src/main/docker/grafana/provisioning/**`
- JHipster Control Center is defined in `src/main/docker/jhipster-control-center.yml`
- SonarQube is defined in `src/main/docker/sonar.yml`

_Integration audit: 2026-03-20_
_Update when adding/removing external services_
