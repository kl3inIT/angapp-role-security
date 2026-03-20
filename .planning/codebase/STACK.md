# Technology Stack

**Analysis Date:** 2026-03-20

## Languages

**Primary:**

- Java 17 - backend application code in `src/main/java/com/mycompany/myapp/**` and tests in `src/test/java/com/mycompany/myapp/**`

**Secondary:**

- TypeScript 5.8.3 - Angular client code in `src/main/webapp/app/**`
- SCSS, HTML, YAML, JSON - UI styles/templates and configuration in `src/main/webapp/**` and `src/main/resources/**`

## Runtime

**Environment:**

- Java 17 - Spring Boot runtime; backend main class is `src/main/java/com/mycompany/myapp/AngappApp.java`
- Node.js >=22.15.0 - frontend/tooling runtime declared in `package.json`
- Browser runtime - Angular SPA delivered from `src/main/webapp/index.html`
- Gradle 8.14 - wrapper defined in `gradle/wrapper/gradle-wrapper.properties`

**Package Manager:**

- npm - package manager used by `angular.json`
- Lockfile: `package-lock.json` present

## Frameworks

**Core:**

- Spring Boot 3.4.5 - server framework configured from `build.gradle` and bootstrapped in `src/main/java/com/mycompany/myapp/AngappApp.java`
- Angular 19.2.9 - client framework in `src/main/webapp/app/**`
- JHipster Framework 8.11.0 - shared runtime conventions via `tech.jhipster:jhipster-framework`

**Testing:**

- JUnit 5, Spring Boot Test, Spring Security Test, Testcontainers, ArchUnit - backend tests in `src/test/java/**`
- Jest 29.7.0 - frontend unit tests via `jest.conf.js`

**Build/Dev:**

- Angular CLI 19.2.10 - app build/serve/test orchestration from `angular.json`
- Custom Webpack - `webpack/webpack.custom.js`, `webpack/proxy.conf.js`, `webpack/environment.js`
- BrowserSync 3.0.4 - dev server proxy/live-reload in `webpack/webpack.custom.js`
- Jib 3.4.5 - container image builds via `buildSrc/gradle/libs.versions.toml`
- Spotless 7.0.3, SonarQube plugin 6.1.0.5360, gradle-node 7.1.0, nohttp 0.0.11 - build-quality/tooling plugins in `buildSrc/gradle/libs.versions.toml`

## Key Dependencies

**Critical:**

- `org.springframework.boot:spring-boot-starter-web`, `spring-boot-starter-security`, `spring-boot-starter-data-jpa`, `spring-boot-starter-actuator`, `spring-boot-starter-oauth2-resource-server` - HTTP API, auth, persistence, and management endpoints
- `tech.jhipster:jhipster-framework` 8.11.0 - JHipster helper/runtime layer
- `org.springdoc:springdoc-openapi-starter-webmvc-api` 2.8.8 - OpenAPI generation and docs
- `org.liquibase:liquibase-core` 4.29.2 - schema/data migrations
- `com.oracle.database.jdbc:ojdbc8` and `com.h2database:h2` - production and dev/test databases

**Infrastructure:**

- `com.zaxxer:HikariCP` - JDBC pool
- `org.hibernate.orm:hibernate-core` and `org.mapstruct:mapstruct` 1.6.3 - ORM and mapping
- Angular runtime libs: `@ng-bootstrap/ng-bootstrap` 18.0.0, `@ngx-translate/core` 16.0.4, `bootstrap` 5.3.6, `rxjs` 7.8.2, `dayjs` 1.11.13, `@fortawesome/angular-fontawesome` 1.0.0

## Configuration

**Environment:**

- Spring profiles and app properties in `src/main/resources/config/application.yml`, `application-dev.yml`, `application-prod.yml`, and `application-tls.yml`
- Angular runtime flags in `src/main/webapp/environments/environment.ts` and `src/main/webapp/environments/environment.development.ts`
- Frontend API/proxy wiring in `webpack/environment.js` and `webpack/proxy.conf.js`

**Build:**

- Backend build in `build.gradle`, `gradle/profile_dev.gradle`, `gradle/profile_prod.gradle`, `gradle/liquibase.gradle`, and `gradle/war.gradle`
- Frontend build/test config in `angular.json`, `tsconfig.json`, `tsconfig.app.json`, `tsconfig.spec.json`, `jest.conf.js`, and `.prettierrc`
- Version catalog for build plugins and shared versions in `buildSrc/gradle/libs.versions.toml`

## Platform Requirements

**Development:**

- Any OS with Java 17 and Node 22.15+; `package.json` scripts and `gradle/wrapper/gradle-wrapper.properties` assume local npm and Gradle wrapper usage
- Docker is optional for local stacks, but compose files are provided in `src/main/docker/**`

**Production:**

- App packages as JAR or WAR from Gradle; Docker image builds are supported via Jib-backed `npm run java:docker*` scripts in `package.json`
- Oracle-backed production profile in `src/main/resources/config/application-prod.yml` currently targets `jdbc:oracle:thin:@localhost:1521:xe`

_Stack analysis: 2026-03-20_
_Update after major dependency changes_
