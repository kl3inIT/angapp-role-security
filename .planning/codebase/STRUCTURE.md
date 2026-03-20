# Codebase Structure

**Analysis Date:** 2026-03-20

## Directory Layout

```
angapp/
├── .planning/                 # Mapping, roadmap, and project-state docs
│   └── codebase/              # Architecture and structure references
├── build/                     # Gradle build output
├── buildSrc/                  # Custom Gradle convention plugins
├── gradle/                    # Wrapper and shared Gradle scripts
├── node_modules/              # Installed frontend dependencies
├── src/                       # Application sources, resources, frontend, and tests
│   ├── main/
│   │   ├── java/              # Spring Boot backend code
│   │   ├── resources/         # Config, Liquibase, templates, i18n, logging
│   │   ├── webapp/            # Angular SPA source and static assets
│   │   ├── docker/            # Docker Compose and container support files
│   │   └── ...
│   └── test/                  # Backend tests and test resources
├── webpack/                   # Frontend build helpers and proxy config
├── angular.json               # Angular CLI workspace config
├── build.gradle               # Root Gradle build
├── package.json               # Frontend package and scripts
├── settings.gradle            # Gradle project settings
└── README.md                  # Developer usage and build docs
```

## Directory Purposes

**`.planning/`:**

- Purpose: GSD planning and codebase mapping artifacts.
- Contains: `PROJECT.md`, `ROADMAP.md`, `STATE.md`, `ISSUES.md`, and `.planning/codebase/*.md`.
- Key files: `.planning/codebase/ARCHITECTURE.md`, `.planning/codebase/STRUCTURE.md`.
- Subdirectories: `codebase/`, `phases/`.

**`src/main/java/`:**

- Purpose: Java backend sources.
- Contains: Spring Boot app code, controllers, services, repositories, domain models, and custom security/fetch-plan infrastructure.
- Key files: `src/main/java/com/mycompany/myapp/AngappApp.java`, `src/main/java/com/mycompany/myapp/config/SecurityConfiguration.java`, `src/main/java/com/mycompany/myapp/web/rest/*.java`.
- Subdirectories: `com/mycompany/myapp/` for app-specific code, `com/mycompany/core/` for shared secure-data support.

**`src/main/resources/`:**

- Purpose: Runtime configuration and backend assets.
- Contains: `config/*.yml`, Liquibase changelogs, i18n property files, mail templates, logging config, TLS material.
- Key files: `src/main/resources/config/application.yml`, `src/main/resources/config/application-dev.yml`, `src/main/resources/config/application-prod.yml`, `src/main/resources/config/liquibase/master.xml`, `src/main/resources/fetch-plans.yml`.
- Subdirectories: `config/`, `templates/`, `i18n/`.

**`src/main/webapp/`:**

- Purpose: Angular application source and static client assets.
- Contains: app bootstrap files, route definitions, feature folders, shared utilities, translations, styles, and images.
- Key files: `src/main/webapp/main.ts`, `src/main/webapp/app/app.config.ts`, `src/main/webapp/app/app.routes.ts`, `src/main/webapp/index.html`, `src/main/webapp/content/scss/global.scss`.
- Subdirectories: `app/`, `content/`, `i18n/`, `environments/`.

**`src/main/docker/`:**

- Purpose: Docker Compose and observability support.
- Contains: app stack descriptors and monitoring configs.
- Key files: `src/main/docker/app.yml`, `src/main/docker/monitoring.yml`, `src/main/docker/sonar.yml`, `src/main/docker/jhipster-control-center.yml`.
- Subdirectories: `grafana/`, `prometheus/`, `jib/`.

**`src/test/`:**

- Purpose: Backend unit and integration tests plus test-only resources.
- Contains: Java tests, test configs, test i18n, fixtures, and sample templates.
- Key files: `src/test/java/com/mycompany/myapp/IntegrationTest.java`, `src/test/resources/config/application.yml`, `src/test/resources/fetch-plans-test.yml`.
- Subdirectories: `java/`, `resources/`.

**`buildSrc/`:**

- Purpose: Shared Gradle conventions for the build.
- Contains: custom Gradle plugin code and version catalog files.
- Key files: `buildSrc/build.gradle`, `buildSrc/src/main/groovy/jhipster.code-quality-conventions.gradle`, `buildSrc/src/main/groovy/jhipster.docker-conventions.gradle`.

**`webpack/`:**

- Purpose: Frontend-specific build and proxy helpers.
- Contains: custom webpack config and proxy settings.
- Key files: `webpack/webpack.custom.js`, `webpack/proxy.conf.js`, `webpack/environment.js`.

## Key File Locations

**Entry Points:**

- `src/main/java/com/mycompany/myapp/AngappApp.java` - Spring Boot application entry point.
- `src/main/webapp/main.ts` - Angular bootstrap entry point.
- `src/main/webapp/app/app.routes.ts` - Root client routing table.

**Configuration:**

- `build.gradle` - Main Gradle build and dependency wiring.
- `angular.json` - Angular CLI workspace configuration.
- `package.json` - Frontend scripts and package metadata.
- `src/main/resources/config/application.yml` - Default backend configuration.
- `src/main/resources/config/application-dev.yml` - Development profile config.
- `src/main/resources/config/application-prod.yml` - Production profile config.
- `src/main/java/com/mycompany/myapp/config/ApplicationProperties.java` - Typed application config.

**Core Logic:**

- `src/main/java/com/mycompany/myapp/web/rest/*.java` - REST controllers.
- `src/main/java/com/mycompany/myapp/service/*.java` and `src/main/java/com/mycompany/myapp/service/impl/*.java` - Service contracts and implementations.
- `src/main/java/com/mycompany/myapp/domain/*.java` - JPA entities.
- `src/main/java/com/mycompany/myapp/repository/*.java` - Spring Data repositories.
- `src/main/java/com/mycompany/core/**` - Secure-data, fetch-plan, merge, serialization, and security helpers.

**Frontend Features:**

- `src/main/webapp/app/entities/**` - Entity feature modules generated by JHipster.
- `src/main/webapp/app/admin/**` - Admin screens such as users, health, logs, metrics, and docs.
- `src/main/webapp/app/account/**` - Login, register, activation, password reset, and settings flows.
- `src/main/webapp/app/layouts/**` - Shell components like navbar, footer, main layout, and error handling.

**Testing:**

- `src/test/java/com/mycompany/myapp/**` - App-level tests and integration tests.
- `src/test/java/com/mycompany/core/**` - Tests for the custom secure-data infrastructure.
- `src/main/webapp/**/*.spec.ts` - Angular component and service unit tests colocated with source.

**Documentation:**

- `README.md` - Build, run, Docker, and testing instructions.
- `role security.md` - Domain-specific security reference.

## Naming Conventions

**Files:**

- `*.java` for backend classes, typically one public type per file.
- `*.ts`, `*.html`, and `*.scss` for Angular components and services.
- `*.spec.ts` for client tests colocated with the implementation.
- `*.xml` for Liquibase changelogs and backend XML config.
- `*.yml` and `*.yaml` for Spring, Docker, and fetch-plan configuration.

**Directories:**

- Java packages follow the Maven-style package hierarchy under `src/main/java/com/mycompany/...`.
- Angular feature folders use lower-case names such as `organization`, `department`, `sec-role`, and `sec-fetch-plan`.
- Liquibase directories are grouped by purpose under `src/main/resources/config/liquibase/`.

**Special Patterns:**

- `*.route.ts` defines Angular route sets for a feature folder.
- `*-routing-resolve.service.ts` resolves entity data before route activation.
- `*-form.service.ts` encapsulates reactive form setup for update screens.
- `*-dialog.component.ts` and `*-delete-dialog.component.ts` hold modal dialogs.
- `package-info.java` files mark package-level documentation/annotations.

## Where to Add New Code

**New Backend Feature:**

- Primary code: `src/main/java/com/mycompany/myapp/web/rest/`, `src/main/java/com/mycompany/myapp/service/`, `src/main/java/com/mycompany/myapp/domain/`, `src/main/java/com/mycompany/myapp/repository/`.
- Tests: `src/test/java/com/mycompany/myapp/`.
- Config if needed: `src/main/resources/config/` and `src/main/resources/config/liquibase/`.

**New Custom Security / Data Control:**

- Primary code: `src/main/java/com/mycompany/core/security/`, `src/main/java/com/mycompany/core/fetch/`, `src/main/java/com/mycompany/core/data/`, `src/main/java/com/mycompany/core/serialize/`.
- Tests: `src/test/java/com/mycompany/core/`.

**New Angular Feature or Screen:**

- Implementation: `src/main/webapp/app/<feature>/`.
- Routes: `src/main/webapp/app/<feature>/<feature>.routes.ts` or `src/main/webapp/app/<feature>/<feature>/...`.
- Tests: colocated `*.spec.ts` files beside the component/service.

**New Shared Client Utility:**

- Shared helpers: `src/main/webapp/app/shared/` or `src/main/webapp/app/core/`.
- Config/constants: `src/main/webapp/app/config/`.

**New Schema / Seed Data:**

- Migrations: `src/main/resources/config/liquibase/changelog/`.
- Seed data: `src/main/resources/config/liquibase/data/` or `src/main/resources/config/liquibase/fake-data/`.

## Special Directories

**`src/main/resources/config/liquibase/changelog/`:**

- Purpose: Ordered schema and data migrations.
- Source: Hand-maintained changelog XML files such as `00000000000000_initial_schema.xml` and dated entity/security changelogs.
- Committed: Yes.

**`src/main/webapp/content/`:**

- Purpose: Static client assets.
- Source: Hand-maintained SCSS, images, and other content files.
- Committed: Yes.

**`build/`:**

- Purpose: Generated Gradle output.
- Source: Build artifacts, compiled classes, and packaged assets.
- Committed: No.

**`node_modules/`:**

- Purpose: Installed frontend dependencies.
- Source: `npm`/`npmw` install output.
- Committed: No.

---

_Structure analysis: 2026-03-20_
_Update when directory structure changes_
