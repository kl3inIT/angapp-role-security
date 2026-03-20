# Codebase Concerns

**Analysis Date:** 2026-03-20

## Tech Debt

**Custom security stack spread across many layers:**

- Issue: Access control is split across `src/main/java/com/mycompany/core/security/access/AccessManagerImpl.java`, `src/main/java/com/mycompany/core/security/permission/RolePermissionServiceDbImpl.java`, `src/main/java/com/mycompany/core/security/row/RowLevelSpecificationBuilder.java`, `src/main/java/com/mycompany/core/fetch/FetchPlanResolverImpl.java`, `src/main/java/com/mycompany/core/serialize/SecureEntitySerializerImpl.java`, and `src/main/java/com/mycompany/core/data/SecureDataManagerImpl.java`.
- Why: The app is implementing a Jmix-like security model incrementally instead of through a single abstraction.
- Impact: Changes to permission logic, fetch plans, or serialization can break in non-obvious ways across multiple layers.
- Fix approach: Add focused integration tests around the full read/write path and reduce cross-layer duplication where possible.

**Manual DTO and map round-tripping for secured entities:**

- Issue: `src/main/java/com/mycompany/myapp/service/impl/OrganizationServiceImpl.java` and `src/main/java/com/mycompany/myapp/service/impl/DepartmentServiceImpl.java` convert DTOs to entities, save them, then convert secure `Map<String, Object>` results back to DTOs by hand.
- Why: The secure serializer returns filtered maps rather than typed projections.
- Impact: Every new field must be updated in the entity, DTO, service mapping, fetch plan, and permission seed or behavior diverges.
- Fix approach: Consolidate the projection/mapping layer or generate the mapping from a single source of truth.

**Permission seed and code drift:**

- Issue: Permission and fetch-plan definitions are split between code, YAML, and Liquibase in `src/main/resources/fetch-plans.yml` and `src/main/resources/config/liquibase/changelog/20260319*.xml` / `20260320*.xml`.
- Why: Runtime behavior depends on both static YAML and database seed data.
- Impact: A plan or permission can look present in one place and still be ineffective at runtime if the other source disagrees.
- Fix approach: Keep one canonical source for permissions, or add validation that compares YAML, DB seed, and evaluator expectations at startup.

## Known Bugs

**Attribute permissions seeded with a different target casing than the evaluator expects:**

- Symptoms: Attribute `VIEW`/`EDIT` checks can fail unexpectedly even when the changelog appears to grant them.
- Trigger: Loading the seeds in `src/main/resources/config/liquibase/changelog/20260320000001_security_fix_attribute_permission_targets.xml` and then checking permissions through `src/main/java/com/mycompany/core/security/permission/AttributePermissionEvaluatorImpl.java`.
- Workaround: Use consistently upper-cased targets, or normalize both the stored target and the lookup target in the same way.
- Root cause: `AttributePermissionEvaluatorImpl` uppercases `ENTITY.ATTRIBUTE`, while `RolePermissionServiceDbImpl` performs an exact string match against the stored `sec_permission.target`.

**Create flows bypass attribute-level write checks:**

- Symptoms: A user with entity-level `CREATE` permission can submit all fields in the create payload, even if some of those fields would be blocked on update.
- Trigger: `POST /api/organizations` and `POST /api/departments` through `src/main/java/com/mycompany/myapp/web/rest/OrganizationResource.java` and `src/main/java/com/mycompany/myapp/web/rest/DepartmentResource.java`.
- Workaround: None in code; the current create path saves directly through `OrganizationRepository` and `DepartmentRepository`.
- Root cause: `src/main/java/com/mycompany/myapp/service/impl/OrganizationServiceImpl.java` and `src/main/java/com/mycompany/myapp/service/impl/DepartmentServiceImpl.java` call `repository.save(...)` on create instead of going through `SecureDataManager`.

## Security Considerations

**Hardcoded secrets and credentials in config:**

- Risk: JWT secrets and database credentials are stored directly in repo config files.
- Current mitigation: None visible in the code; the values are embedded in `src/main/resources/config/application-dev.yml` and `src/main/resources/config/application-prod.yml`.
- Recommendations: Move secrets to environment variables or a secret manager and fail fast if secret-backed settings are unset.

**Public management info leaks runtime metadata:**

- Risk: `src/main/resources/config/application.yml` enables `management.info.git.mode: full` and `management.info.env.enabled: true`, while `src/main/java/com/mycompany/myapp/config/SecurityConfiguration.java` allows `/management/info` without authentication.
- Current mitigation: Admin-only protection covers most other management endpoints.
- Recommendations: Remove env/git data from the public info endpoint or require authentication for `/management/info`.

**H2 console is deliberately open in dev:**

- Risk: `src/main/resources/config/application-dev.yml` enables the H2 console and `src/main/resources/.h2.server.properties` sets `webAllowOthers=true`; `src/main/java/com/mycompany/myapp/config/SecurityConfiguration.java` permits `/h2-console/**` in development.
- Current mitigation: It is limited to the development profile.
- Recommendations: Keep the dev profile network-isolated and disable `webAllowOthers` unless the console must be reachable remotely.

**User logs can leak activation keys:**

- Risk: `src/main/java/com/mycompany/myapp/domain/User.java` includes `activationKey` in `toString()`, and `src/main/java/com/mycompany/myapp/service/UserService.java` logs full user objects during create/activation flows.
- Current mitigation: None; debug logs can still capture the key.
- Recommendations: Remove sensitive fields from `toString()` and log identifiers only.

**Fetch-plan authorization is wired but not actually enforced yet:**

- Risk: `src/main/java/com/mycompany/core/fetch/FetchPlanResolverImpl.java` applies `FetchPlanAccessContext`, but there is no `AccessConstraint` implementation for that context in `src/main/java/com/mycompany/core/security/access`.
- Current mitigation: None beyond the default permissive path when no matching constraint exists.
- Recommendations: Add a fetch-plan constraint bean before exposing user-controlled plan selection.

## Performance Bottlenecks

**Managed-user listing has an acknowledged N+1 tax on cold cache:**

- Problem: `src/main/java/com/mycompany/myapp/web/rest/UserResource.java` documents that fetching user authorities causes N+1 queries on the first call, and `src/main/java/com/mycompany/myapp/service/UserService.java` returns DTOs from `findAll(...)` rather than a purpose-built projection.
- Measurement: No repo-level benchmark is recorded.
- Cause: Lazy authority loading plus second-level cache dependence.
- Improvement path: Introduce an eager projection or dedicated query for the admin listing path.

**Recursive secure serialization can multiply work on large object graphs:**

- Problem: `src/main/java/com/mycompany/core/serialize/SecureEntitySerializerImpl.java` recursively serializes collections, arrays, and nested references in memory.
- Measurement: No numbers are recorded in the repo.
- Cause: The serializer walks the full graph without streaming or cycle detection.
- Improvement path: Cap graph depth, add cycle detection, and measure payload size / serialization time on realistic entities.

## Fragile Areas

**Constraint ordering is implicit and runtime-driven:**

- Why fragile: `src/main/java/com/mycompany/core/security/access/AccessManagerImpl.java` sorts beans by `getOrder()` and applies them via unchecked casts.
- Common failures: A new constraint with the wrong order or broad `supports()` type can silently change authorization behavior.
- Safe modification: Add tests for constraint ordering and context selection before changing bean registration.
- Test coverage: No direct integration test covers the full constraint chain.

**Only a narrow row-policy subset is implemented:**

- Why fragile: `src/main/java/com/mycompany/core/security/row/RowLevelPolicyProviderDbImpl.java` only handles `PolicyType.SPECIFICATION` and only when the expression matches `field = CURRENT_USER_ID`.
- Common failures: `JPQL` and `JAVA` policy rows are ignored, and unsupported expressions silently return no policy.
- Safe modification: Treat unsupported policy types as explicit errors, not as no-ops.
- Test coverage: No test exercises row-policy loading or expression parsing.

**Fetch-plan parsing is permissive at runtime but strict on specific shapes:**

- Why fragile: `src/main/java/com/mycompany/core/fetch/YamlFetchPlanRepository.java` accepts multiple YAML shapes, reflection-based property discovery, and recursive inheritance.
- Common failures: Small definition mistakes become startup/runtime errors, and cyclic or malformed plan graphs fail only when resolved.
- Safe modification: Add schema validation for fetch-plan YAML and targeted tests for invalid inheritance / nested property cases.
- Test coverage: `src/test/java/com/mycompany/core/fetch/YamlFetchPlanRepositoryTest.java` covers one happy-path YAML case only.

## Missing Critical Features

**Fetch-plan policy enforcement is incomplete:**

- Problem: The changelog seeds `FETCH_PLAN` permissions in `src/main/resources/config/liquibase/changelog/20260320000000_security_seed_fetch_plans_and_attribute_permissions.xml`, but there is no matching constraint implementation to enforce them.
- Current workaround: Service code currently passes constant plan codes for organizations and departments.
- Blocks: User-selected or role-specific fetch-plan restrictions.
- Implementation complexity: Medium.

**Declared row-policy types are not implemented:**

- Problem: `src/main/java/com/mycompany/core/security/domain/SecRowPolicy.java` defines `SPECIFICATION`, `JPQL`, and `JAVA`, but `src/main/java/com/mycompany/core/security/row/RowLevelPolicyProviderDbImpl.java` only supports the specification variant.
- Current workaround: None for policy rows that need JPQL or Java execution.
- Blocks: Richer row-level security rules.
- Implementation complexity: Medium.

## Test Coverage Gaps

**Core security path is under-tested:**

- What’s not tested: `src/main/java/com/mycompany/core/data/SecureDataManagerImpl.java`, `src/main/java/com/mycompany/core/security/access/AccessManagerImpl.java`, `src/main/java/com/mycompany/core/security/row/RowLevelSpecificationBuilder.java`, `src/main/java/com/mycompany/core/fetch/FetchPlanResolverImpl.java`, and `src/main/java/com/mycompany/core/fetch/DbFetchPlanRepository.java`.
- Risk: Permission or fetch-plan regressions could ship without detection.
- Priority: High.
- Difficulty to test: Requires bootstrapping security context, repositories, and seeded metadata together.

**Create-path security regression is not covered:**

- What’s not tested: The create flow in `src/main/java/com/mycompany/myapp/service/impl/OrganizationServiceImpl.java` and `src/main/java/com/mycompany/myapp/service/impl/DepartmentServiceImpl.java`.
- Risk: Attribute-level write bypasses can remain hidden after refactors.
- Priority: High.
- Difficulty to test: Needs an integration test that compares create vs update authorization behavior.

**Management endpoint exposure is not tested:**

- What’s not tested: Public access to `/management/info`, `/management/prometheus`, and the dev H2 console wiring from `src/main/java/com/mycompany/myapp/config/SecurityConfiguration.java` and `src/main/java/com/mycompany/myapp/config/WebConfigurer.java`.
- Risk: Sensitive runtime metadata or database access can be exposed by configuration drift.
- Priority: Medium.
- Difficulty to test: Depends on profile-specific startup configuration.

_Concerns audit: 2026-03-20_
_Update as issues are fixed or new ones discovered_
