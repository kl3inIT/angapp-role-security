# Testing Patterns

**Analysis Date:** 2026-03-20

## Test Framework

**Runner**

- Frontend tests use Jest 29 with `jest-preset-angular`, configured in `jest.conf.js`.
- Backend tests use JUnit 5 with Spring Boot test support and Gradle tasks defined in `build.gradle`.

**Assertion Library**

- Frontend tests use Jest matchers such as `toBe`, `toEqual`, `toHaveBeenCalledWith`, and `toMatchObject`.
- Backend tests use AssertJ (`assertThat`) and Spring MockMvc assertions.

**Run Commands**

```bash
npm test
npm run test:watch
npm run jest
./gradlew test
./gradlew integrationTest
npm run backend:unit:test
npm run ci:backend:test
npm run ci:frontend:test
```

## Test File Organization

**Location**

- Frontend specs live alongside source files under `src/main/webapp/app/**/*.spec.ts`.
- Backend unit and integration tests live under `src/test/java/com/mycompany/myapp/` with names ending in `Test.java` or `IT.java`.
- Shared backend test resources live in `src/test/resources/`, including `junit-platform.properties`, `fetch-plans-test.yml`, and `config/application-test*.yml`.

**Naming**

- Angular tests generally mirror the source file name, such as `register.component.spec.ts`, `user.service.spec.ts`, and `sort.directive.spec.ts`.
- Backend tests use class names that match the class under test, such as `OrganizationResourceIT.java`, `UserServiceIT.java`, and `OrganizationMapperTest.java`.

**Structure**

```text
src/main/webapp/app/
  entities/
    organization/
      organization.model.ts
      service/organization.service.ts
      service/organization.service.spec.ts
      list/organization.component.spec.ts

src/test/java/com/mycompany/myapp/
  web/rest/
    OrganizationResourceIT.java
  service/
    UserServiceIT.java
  domain/
    OrganizationTest.java
    OrganizationTestSamples.java
    OrganizationAsserts.java
```

## Test Structure

**Suite Organization**

- Frontend specs usually follow `describe(...)` blocks with `beforeEach(...)`, then `it(...)` cases for happy path, invalid input, and state changes.
- Backend tests commonly use `@BeforeEach` for setup, `@AfterEach` for cleanup, and one test method per behavior or endpoint.
- Complex Angular tests sometimes create a host component inside the spec file, for example `src/main/webapp/app/shared/sort/sort.directive.spec.ts`.

**Patterns**

- Angular specs commonly use `TestBed.configureTestingModule`, `waitForAsync`, `fakeAsync`, and `tick`.
- Backend integration tests typically use `@IntegrationTest`, `@Transactional`, `@AutoConfigureMockMvc`, and `@WithMockUser`.
- A lot of backend tests follow arrange/act/assert naming through comments and helper methods.

## Mocking

**Framework**

- Frontend mocking uses Jest (`jest.mock`, `jest.spyOn`, `jest.fn`) plus Angular `HttpTestingController` and `provideHttpClientTesting()`.
- Backend mocking uses Mockito, including Spring’s `@MockitoBean`, as in `src/test/java/com/mycompany/myapp/service/UserServiceIT.java`.

**Patterns**

```typescript
jest.mock('app/core/auth/account.service');
jest.spyOn(service, 'save').mockReturnValue(of({}));
const req = httpMock.expectOne({ method: 'GET' });
req.flush(configProps);
```

```java
when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now()));
```

**What to Mock**

- Frontend tests mock HTTP calls, router navigation, auth/account services, and browser interaction.
- Backend tests mock time providers, auth state, or other external boundaries when a Spring integration test needs deterministic behavior.

**What NOT to Mock**

- Pure collection helpers and DTO/entity mapping helpers are usually tested directly.
- Generated assertion helpers such as `OrganizationAsserts` are used instead of mocking domain objects.

## Fixtures and Factories

**Test Data**

- Frontend entity tests use generated sample factories such as `src/main/webapp/app/entities/user/user.test-samples.ts`, which exports frozen sample objects like `sampleWithRequiredData`.
- Backend tests use factory helpers such as `src/test/java/com/mycompany/myapp/domain/OrganizationTestSamples.java` and assertion helpers such as `src/test/java/com/mycompany/myapp/domain/OrganizationAsserts.java`.
- Some backend tests also use utility helpers from `src/test/java/com/mycompany/myapp/web/rest/TestUtil.java`.

**Location**

- Shared sample data usually lives next to the model or domain class it supports.
- Larger test-only fixtures live in `src/test/resources/`, for example `templates/mail/*.html` and `i18n/messages_*.properties`.

## Coverage

**Requirements**

- Frontend coverage is enabled by `npm test`, which runs `ng test --coverage --log-heap-usage -w=2`.
- Backend coverage is not enforced by an explicit threshold in the visible repo config.

**Configuration**

- Jest coverage output is written to `build/test-results/` via `jest.conf.js`.
- JUnit reports are also written under `build/test-results/` through `jest-junit` and `jest-sonar` reporters.
- JUnit test timeouts are defined in `src/test/resources/junit-platform.properties`.

**View Coverage**

```bash
npm test
./gradlew test
```

## Test Types

**Unit Tests**

- Common on the frontend for services, pipes, directives, and small components, for example `src/main/webapp/app/shared/sort/sort.service.spec.ts`.
- Common on the backend for domain equality, mapping, and small service rules, for example `src/test/java/com/mycompany/myapp/domain/OrganizationTest.java` and `src/test/java/com/mycompany/myapp/service/mapper/OrganizationMapperTest.java`.

**Integration Tests**

- Backend controller and service integration coverage is the main heavy-weight test style, for example `src/test/java/com/mycompany/myapp/web/rest/OrganizationResourceIT.java` and `src/test/java/com/mycompany/myapp/service/UserServiceIT.java`.
- These tests usually hit Spring context, repositories, and MockMvc rather than isolated mocks.

**E2E Tests**

- No dedicated browser E2E harness is visible in the repository.
- There is no `e2e/`, `cypress/`, `playwright/`, or `protractor/` tree in the current codebase.

## Common Patterns

**Async Testing**

- Frontend async tests often use `fakeAsync` and `tick`, especially when component logic chains through observables.
- HTTP tests generally subscribe, flush a mock response, then assert on the emitted result.

**Error Testing**

- Frontend error tests usually assert component flags after a simulated HTTP failure, as in `src/main/webapp/app/account/register/register.component.spec.ts`.
- Backend error tests check returned status codes, validation failures, or repository state changes after invalid requests.

**Snapshot Testing**

- Snapshot testing is not used in the visible repository.
- Assertions are explicit and object-focused instead of snapshot-based.

---

_Testing analysis: 2026-03-20_
_Update when test patterns change_
