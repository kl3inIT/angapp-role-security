# Coding Conventions

**Analysis Date:** 2026-03-20

## Naming Patterns

**Files**

- Frontend files use feature-based names such as `*.component.ts`, `*.service.ts`, `*.model.ts`, `*.routes.ts`, and `*.spec.ts` under `src/main/webapp/app/`.
- Backend files use layer-based names such as `*Resource.java`, `*Service.java`, `*Repository.java`, `*DTO.java`, `*IT.java`, `*Test.java`, `*Samples.java`, and `*Asserts.java` under `src/test/java/com/mycompany/myapp/`.
- Barrel files are common in Angular folders, for example `src/main/webapp/app/shared/sort/index.ts` and `src/main/webapp/app/entities/organization/organization.routes.ts`.

**Functions**

- camelCase is used for methods and helper functions on both sides of the app.
- Angular lifecycle methods follow framework names such as `ngOnInit`, `ngAfterViewInit`, and `register`.
- Service helpers usually read as verbs: `createRequestOption`, `compareUser`, `addOrganizationToCollectionIfMissing`, `checkCrudPermission`.

**Variables**

- camelCase is used for local variables and fields.
- Java constants use UPPER_SNAKE_CASE, for example `DEFAULT_CODE`, `ENTITY_API_URL`, and `ENTITY_NAME` in `src/test/java/com/mycompany/myapp/web/rest/OrganizationResourceIT.java`.
- Angular tests often use `mock*`, `expectedResult`, `fixture`, and `comp` as short local names.

**Types**

- Angular entity interfaces use an `I` prefix, such as `IOrganization` in `src/main/webapp/app/entities/organization/organization.model.ts` and `IUser` in `src/main/webapp/app/entities/user/user.model.ts`.
- New entity helper types use `New*`, such as `NewOrganization` and `NewDepartment`.
- Java DTOs, entities, and test helpers use PascalCase without prefixes.

## Code Style

**Formatting**

- Prettier is configured in `.prettierrc` with `printWidth: 140`, `singleQuote: true`, `tabWidth: 2`, and `arrowParens: avoid`.
- Java formatting uses 4-space indentation via `.prettierrc` and `.editorconfig`.
- Line endings are LF and trailing whitespace is trimmed by `.editorconfig`.

**Linting**

- ESLint uses the flat config in `eslint.config.mjs`.
- Angular component selectors must use the `jhi` prefix and kebab-case, and directives must use the `jhi` prefix and camelCase.
- `no-console` is enforced except for `warn` and `error`; `console.log` is not a normal code pattern.
- `@typescript-eslint/explicit-function-return-type` is enforced for frontend code under `src/main/webapp/**/*.ts`.

## Import Organization

**Order**

- Imports generally start with framework/external modules, then internal `app/...` aliases or package-local imports, then relative imports.
- TypeScript code relies on the `baseUrl` `src/main/webapp/` in `tsconfig.json`, so imports like `app/core/config/application-config.service` and `app/core/request/request-util` are used instead of deep relative paths.

**Grouping**

- Related imports are grouped with blank lines between external, shared, and local imports.
- Frontend code frequently imports from barrel files such as `app/shared/shared.module` and `app/shared/sort/index.ts`.

## Error Handling

**Patterns**

- Backend REST resources validate request state early and throw `BadRequestAlertException` for invalid IDs or missing entities, as seen in `src/main/java/com/mycompany/myapp/web/rest/OrganizationResource.java`.
- Access checks are performed in controller methods through `AccessManager` and failures are raised as `AccessDeniedException`.
- Global backend translation is centralized in `src/main/java/com/mycompany/myapp/web/rest/errors/ExceptionTranslator.java`, which converts exceptions into RFC7807-style problem details.
- Frontend services typically return `Observable<HttpResponse<...>>`, and components handle failures by inspecting `HttpErrorResponse`, as in `src/main/webapp/app/account/register/register.component.ts`.

**Error Types**

- Domain-specific backend exceptions are custom classes such as `UsernameAlreadyUsedException`, `EmailAlreadyUsedException`, `InvalidPasswordException`, and `BadRequestAlertException`.
- User-facing REST errors are shaped through `HeaderUtil`, `ResponseUtil`, and `PaginationUtil` in resource classes.

## Logging

**Framework**

- Backend logging uses SLF4J with `LoggerFactory.getLogger(...)` and `LOG.debug(...)`.
- Frontend code does not use application logging; it relies on component state and HTTP error handling instead.

**Patterns**

- Log messages are usually short and action-oriented, such as `REST request to save Organization`.
- Logging happens at service/controller boundaries rather than in low-level utilities.

## Comments

**When to Comment**

- Comments are mostly generated JHipster markers such as `// jhipster-needle-*` and short explanations for non-obvious behavior.
- Backend code uses brief notes for entity lifecycle and cache handling, for example in `src/main/java/com/mycompany/myapp/service/UserService.java`.
- `prettier-ignore` is used selectively for generated `toString()` methods in Java entities and DTOs.

**JSDoc/TSDoc**

- Java classes and public methods are often documented with short Javadoc in services, controllers, and test helpers.
- Frontend code relies more on self-describing names than on heavy inline comments.

## Function Design

**Size**

- Methods are usually kept small and focused, especially in service and controller classes.
- Test helpers are extracted into shared utility classes rather than duplicated inline.

**Parameters**

- Angular methods commonly accept a small object or a single DTO rather than many positional arguments.
- Java REST handlers take typed DTOs with Bean Validation annotations such as `@Valid` and `@NotNull`.

**Return Values**

- Backend service methods often return `Optional<T>`, `Page<T>`, or `ResponseEntity<T>` depending on the layer.
- Frontend service methods return typed `Observable`s and avoid side effects beyond HTTP calls.

## Module Design

**Exports**

- Angular components and routes frequently use default exports, for example `src/main/webapp/app/app.component.ts`, `src/main/webapp/app/account/register/register.component.ts`, and `src/main/webapp/app/entities/organization/organization.routes.ts`.
- Services and utility modules usually use named exports.

**Framework-Specific Conventions**

- Angular code is written as standalone components with an `imports` array, `inject(...)`, and route-based lazy loading via `loadComponent`.
- Entity UI layers follow a consistent pattern: `*.model.ts`, `service/*.service.ts`, `list/*.component.ts`, `detail/*.component.ts`, `update/*.component.ts`, and matching `*.spec.ts` files.
- Backend code follows the usual JHipster layering: `domain`, `repository`, `service`, `service.dto`, `service.mapper`, `web.rest`, and `web.rest.errors`.
- JPA entities use chained builder-style setters alongside standard getters/setters, as in `src/main/java/com/mycompany/myapp/domain/Organization.java`.

---

_Convention analysis: 2026-03-20_
_Update when patterns change_
