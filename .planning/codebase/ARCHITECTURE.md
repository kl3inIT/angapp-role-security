# Architecture

**Analysis Date:** 2026-03-20

## Pattern Overview

**Overall:** JHipster full-stack monolith with a Spring Boot API backend and an Angular SPA frontend.

**Key Characteristics:**

- One JVM application serves both REST APIs and the Angular client from the same deployment.
- Backend security is stateless and JWT-based, with route protection enforced in `src/main/java/com/mycompany/myapp/config/SecurityConfiguration.java`.
- Custom `com.mycompany.core` infrastructure adds fetch-plan, row-level, and attribute-level access control around entity data.
- Feature code is split by layer on the backend and by entity feature folder on the frontend.

## Layers

**Bootstrap and Runtime Shell:**

- Purpose: Start the JVM app, configure profiles, and mount static resources.
- Contains: `src/main/java/com/mycompany/myapp/AngappApp.java`, `src/main/java/com/mycompany/myapp/config/WebConfigurer.java`, `src/main/java/com/mycompany/myapp/web/filter/SpaWebFilter.java`.
- Depends on: Spring Boot, servlet container, environment/profile config.
- Used by: The whole backend runtime.

**HTTP/API Layer:**

- Purpose: Expose REST endpoints and translate HTTP input/output.
- Contains: `src/main/java/com/mycompany/myapp/web/rest/*.java`, `src/main/java/com/mycompany/myapp/web/rest/errors/*.java`, `src/main/java/com/mycompany/myapp/web/rest/vm/*.java`.
- Depends on: Services, repositories, security helpers, JHipster response utilities.
- Used by: Angular services and external API clients.

**Application Service Layer:**

- Purpose: Implement use-case logic and coordinate persistence.
- Contains: `src/main/java/com/mycompany/myapp/service/*.java`, `src/main/java/com/mycompany/myapp/service/impl/*.java`, `src/main/java/com/mycompany/myapp/service/dto/*.java`, `src/main/java/com/mycompany/myapp/service/mapper/*.java`.
- Depends on: Repositories, DTOs, MapStruct mappers, `SecureDataManager` for secured entity access.
- Used by: REST controllers.

**Persistence and Domain Layer:**

- Purpose: Model entities and access relational data.
- Contains: `src/main/java/com/mycompany/myapp/domain/*.java`, `src/main/java/com/mycompany/myapp/repository/*.java`.
- Depends on: Spring Data JPA, entity mappings, Liquibase-managed schema.
- Used by: Service layer and the custom secure data infrastructure.

**Security and Secure Data Infrastructure:**

- Purpose: Centralize authorization, fetch-plan resolution, row filtering, serialization, and safe updates.
- Contains: `src/main/java/com/mycompany/core/data/*`, `src/main/java/com/mycompany/core/fetch/*`, `src/main/java/com/mycompany/core/merge/*`, `src/main/java/com/mycompany/core/serialize/*`, `src/main/java/com/mycompany/core/security/*`.
- Depends on: Spring Security, Spring Data JPA specifications, repository registry, application properties, YAML/DB metadata.
- Used by: REST resources and service implementations that need controlled entity views.

**Frontend Shell and Feature Modules:**

- Purpose: Render the SPA, route to feature screens, and call REST endpoints.
- Contains: `src/main/webapp/main.ts`, `src/main/webapp/app/app.config.ts`, `src/main/webapp/app/app.routes.ts`, `src/main/webapp/app/entities/**`, `src/main/webapp/app/admin/**`, `src/main/webapp/app/account/**`, `src/main/webapp/app/layouts/**`, `src/main/webapp/app/shared/**`.
- Depends on: Angular router, HTTP interceptors, translation module, auth guards, generated entity services.
- Used by: Browser clients loading `index.html`.

## Data Flow

**HTTP Request / SPA Navigation:**

1. Browser loads `src/main/webapp/index.html`, which bootstraps `src/main/webapp/main.ts`.
2. Angular config in `src/main/webapp/app/app.config.ts` wires router, HTTP client, interceptors, and i18n.
3. Route changes are matched in `src/main/webapp/app/app.routes.ts` and feature route files such as `src/main/webapp/app/entities/entity.routes.ts`.
4. API calls flow through Angular services like `src/main/webapp/app/entities/organization/service/organization.service.ts` to `/api/**`.
5. On the server, `SecurityConfiguration` allows public assets and selected endpoints, then authenticates protected API routes.
6. Controllers such as `src/main/java/com/mycompany/myapp/web/rest/OrganizationResource.java` validate input and enforce CRUD permission checks.
7. Service implementations such as `src/main/java/com/mycompany/myapp/service/impl/OrganizationServiceImpl.java` delegate to `SecureDataManager`.
8. `SecureDataManagerImpl` applies entity permissions, row-level filters, fetch-plan resolution, and safe merge semantics before returning serialized maps or deleting rows.
9. `SecureEntitySerializerImpl` emits only permitted attributes, and the service layer converts that data back to DTOs like `OrganizationDTO`.
10. `ExceptionTranslator` turns thrown exceptions into RFC7807-style responses for the Angular error handlers.

**State Management:**

- Application state is mostly database-backed and request-scoped; the backend is stateless at the HTTP layer.
- Authentication is token-based, while user and domain data live in the database and are migrated through Liquibase changelogs.
- Frontend state is client-side Angular component state plus route state; there is no separate frontend store in this repo.

## Key Abstractions

**SecureDataManager:**

- Purpose: Single entry point for permission-aware entity reads, writes, paging, and deletes.
- Examples: `src/main/java/com/mycompany/core/data/SecureDataManagerImpl.java`.
- Pattern: Facade over access control, fetch-plan resolution, serialization, and repositories.

**FetchPlan:**

- Purpose: Declare which scalar attributes and references should be loaded and serialized.
- Examples: `src/main/java/com/mycompany/core/fetch/FetchPlan.java`, YAML definitions loaded by `src/main/java/com/mycompany/core/fetch/YamlFetchPlanRepository.java`, JSON-backed metadata in `src/main/java/com/mycompany/core/fetch/DbFetchPlanRepository.java`.
- Pattern: Named object graph definition with `_base`, `_local`, and `_instance_name` conventions.

**AccessContext / AccessConstraint:**

- Purpose: Carry permission checks through the core security pipeline.
- Examples: `src/main/java/com/mycompany/core/security/access/CrudEntityContext.java`, `src/main/java/com/mycompany/core/security/access/FetchPlanAccessContext.java`, `src/main/java/com/mycompany/core/security/access/AccessManagerImpl.java`.
- Pattern: Ordered constraint application over a context object.

**Entity and Attribute Permission Evaluators:**

- Purpose: Decide whether the current user may perform CRUD or view/edit individual fields.
- Examples: `src/main/java/com/mycompany/core/security/permission/EntityPermissionEvaluatorImpl.java`, `src/main/java/com/mycompany/core/security/permission/AttributePermissionEvaluatorImpl.java`.
- Pattern: Delegation to role/permission metadata using entity or `ENTITY.ATTRIBUTE` targets.

**Service + DTO + Mapper Trio:**

- Purpose: Keep REST payloads separate from JPA entities.
- Examples: `src/main/java/com/mycompany/myapp/service/dto/OrganizationDTO.java`, `src/main/java/com/mycompany/myapp/service/mapper/OrganizationMapper.java`, `src/main/java/com/mycompany/myapp/service/impl/OrganizationServiceImpl.java`.
- Pattern: MapStruct DTO mapping plus service orchestration.

## Entry Points

**JVM Entry:**

- Location: `src/main/java/com/mycompany/myapp/AngappApp.java`.
- Triggers: `./gradlew bootRun`, packaged JAR/WAR startup, or test bootstraps.
- Responsibilities: Configure the Spring application, apply the default profile, validate profile combinations, and log startup URLs.

**Angular Entry:**

- Location: `src/main/webapp/main.ts`.
- Triggers: Browser load of the SPA bundle.
- Responsibilities: Import bootstrap code and start the client application.

**Angular App Config:**

- Location: `src/main/webapp/app/app.config.ts`.
- Triggers: Angular application initialization.
- Responsibilities: Register router, HTTP client, translation module, date adapter, title strategy, and navigation error handling.

## Error Handling

**Strategy:** Backend exceptions are translated centrally to problem-details responses; frontend route failures are redirected by status code.

**Patterns:**

- `src/main/java/com/mycompany/myapp/web/rest/errors/ExceptionTranslator.java` maps validation, access, and domain errors to RFC7807 payloads.
- `src/main/java/com/mycompany/myapp/web/rest/errors/BadRequestAlertException.java` and the other custom exceptions carry entity-specific headers and messages.
- `src/main/webapp/app/app.config.ts` navigates to `/accessdenied`, `/404`, `/login`, or `/error` when route loading fails.

## Cross-Cutting Concerns

**Security:**

- `src/main/java/com/mycompany/myapp/config/SecurityConfiguration.java` configures JWT resource-server auth, `STATELESS` sessions, and endpoint authorization.
- `src/main/java/com/mycompany/myapp/web/filter/SpaWebFilter.java` forwards unknown non-API paths to `index.html` so client-side routing works.
- `src/main/java/com/mycompany/core/security/**` applies entity, attribute, row, and fetch-plan checks before data is exposed or updated.

**Validation:**

- Bean validation is used at the REST boundary in controllers such as `src/main/java/com/mycompany/myapp/web/rest/OrganizationResource.java` and `src/main/java/com/mycompany/myapp/web/rest/AccountResource.java`.
- Angular feature code relies on generated forms and route resolvers under `src/main/webapp/app/entities/**`.

**Logging and Observability:**

- Server startup and request paths use SLF4J across controllers and services.
- Actuator, Prometheus, and management endpoints are configured through Spring Boot and exposed under `/management/**`.

**Internationalization:**

- Server-side messages live in `src/main/resources/i18n/messages*.properties`.
- Client-side translations live in `src/main/webapp/i18n/{en,vi}/**.json`.

**Static Asset Serving:**

- `src/main/java/com/mycompany/myapp/config/WebConfigurer.java` points the embedded server at built frontend assets during development.
- `src/main/webapp/content/**` contains images, SCSS, and shared static content.

---

_Architecture analysis: 2026-03-20_
_Update when major patterns change_
