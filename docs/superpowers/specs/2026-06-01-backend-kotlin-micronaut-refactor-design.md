# Backend Tech-Stack Refactor: Node.js → Kotlin + Micronaut + Postgres

**Date:** 2026-06-01
**Status:** Approved (design)
**Scope:** Backend only. Frontend (Expo) is unaffected.

---

## 1. Context

The backend of `game-price-tracker` was bootstrapped in TypeScript with Node.js + pnpm + Vitest. The current implementation is minimal: two domain types (`Currency`, `GamePrice`) and three Vitest tests. The product spec (`docs/superpowers/specs/gamepricetracker-spec.md` §2) and the phase 0–6 implementation plans assume Node.js + Fastify + Prisma + SQLite.

The owner has decided to migrate the backend to a JVM stack — Kotlin + Micronaut + Gradle + Postgres — for personal preference and to align with the structural patterns used in the reference project `~/workspace/huspy/leads-service`. The frontend stays as-is.

This spec covers a **single refactor pass**: stand up the new build, port the existing two domain types and their tests, and prove the stack boots end-to-end. ITAD/Steam/Nintendo clients, controllers, JPA entities for prices/games/favorites, and caching come in fresh phase plans authored afterwards.

---

## 2. Goals & Non-goals

### Goals
- Replace `game-price-tracker-be/` in place with a Kotlin/Micronaut/Gradle skeleton.
- Mirror the hexagonal multi-module structure of `leads-service` (slim variant — see §3).
- Port `Currency` and `GamePrice` to Kotlin in `core/`, preserving the existing invariants and test cases.
- Prove the build wires end-to-end: `./gradlew test` runs Kotlin/JUnit5 tests; `./gradlew run` boots Micronaut against a Postgres container; Flyway runs.
- Update `gamepricetracker-spec.md` §2 to reflect the new stack so the product spec and reality agree.
- Archive the now-irrelevant Node.js phase plans.

### Non-goals
- ITAD, Steam, Nintendo eShop HTTP clients.
- REST controllers, request/response DTOs, OpenAPI generation.
- Real JPA entities for `Game`, `Price`, `Favorite`.
- Cache TTL logic, scheduled refresh jobs.
- VPS deployment, PM2/Caddy/equivalent.
- Any change to the Expo frontend.
- Re-running the full Phase 0–6 product roadmap in this pass — those will be re-authored as fresh phase plans driven by the updated spec.

---

## 3. Architecture

### 3.1 Module layout (Gradle multi-module)

```
game-price-tracker-be/
├── buildSrc/                 — Gradle convention plugins
│   └── src/main/kotlin/
│       ├── kotlin-conventions.gradle.kts
│       └── micronaut-conventions.gradle.kts
├── application/              — Micronaut entrypoint + wiring + Flyway resources
│   └── src/main/kotlin/com/gamepricetracker/application/Application.kt
│   └── src/main/resources/application.yml
│   └── src/main/resources/db/migration/V1__init.sql
│   └── src/test/kotlin/...  — @MicronautTest smoke test (Testcontainers)
├── core/                     — Pure Kotlin: domain models + ports + use cases
│   └── src/main/kotlin/com/gamepricetracker/core/domain/models/Currency.kt
│   └── src/main/kotlin/com/gamepricetracker/core/domain/models/GamePrice.kt
│   └── src/test/kotlin/com/gamepricetracker/core/domain/models/CurrencyTest.kt
│   └── src/test/kotlin/com/gamepricetracker/core/domain/models/GamePriceTest.kt
├── web/                      — Controllers + DTOs (placeholder package only this pass)
│   └── src/main/kotlin/com/gamepricetracker/web/.gitkeep
├── infra/                    — JPA repositories + outbound HTTP clients (placeholder this pass)
│   └── src/main/kotlin/com/gamepricetracker/infra/.gitkeep
├── docker-compose.yml        — Postgres 18-alpine
├── build.gradle.kts          — Root: subprojects { apply(kotlin-conventions) }
├── settings.gradle.kts       — include("application", "core", "web", "infra")
├── gradle.properties         — Pinned versions
├── gradlew / gradlew.bat     — Gradle 8.x wrapper
└── .gitignore                — JVM/Gradle/IDE patterns
```

### 3.2 Module dependency graph (compile-time enforcement of hexagonal layers)

```
                  ┌──────────────┐
                  │ application  │  ← only place where everything wires
                  └──┬───┬───┬───┘
                     │   │   │
              ┌──────┘   │   └──────┐
              ▼          ▼          ▼
        ┌────────┐  ┌────────┐ ┌────────┐
        │  web   │  │ infra  │ │  core  │
        └───┬────┘  └───┬────┘ └────────┘
            │           │           ▲
            └───────────┴───────────┘
                  depends on core
```

- `core` → no module deps; no Micronaut, no JPA, no Hibernate. Pure Kotlin stdlib + Kotest assertions (test-only).
- `web` → depends on `core` only.
- `infra` → depends on `core` only.
- `application` → depends on `core`, `web`, `infra`.

This makes layer violations a compile error, not a code-review nit.

### 3.3 Tech versions (pinned in `gradle.properties`)

| Component | Version | Notes |
|---|---|---|
| Kotlin | **2.3.21** | Latest stable per Context7 (kotlinlang.org). Compatible with Gradle 8.x kotlin-dsl and JVM target 21. |
| Micronaut Framework | **5.1.x** | Latest stable per Context7. Exact patch (e.g. `5.1.3`) to be confirmed against `micronaut.io/release-notes` immediately before scaffolding. **Verify before pinning.** |
| Micronaut Data | Latest paired with Micronaut 5.1.x | JPA path on Postgres confirmed supported. |
| Gradle | **8.x** (wrapper) | kotlin-dsl. Use whatever 8.x ships with Micronaut Launch defaults. |
| JVM target / toolchain | **Java 21 LTS** | Foojay resolver convention. |
| Annotation processing | **KAPT** | Recommended by Micronaut Data Kotlin docs as of mid-2026; KSP is not yet the documented path for Micronaut Data. |
| Postgres | **18-alpine** | Latest stable major. |
| Flyway | Bundled with `micronaut-flyway` (12.x) | Standard SQL migrations under `application/src/main/resources/db/migration/`. |
| Test runner | **JUnit 5** | Via `useJUnitPlatform()` in `kotlin-conventions`. |
| Test extras | **MockK** + **Kotest assertions** | MockK for mocks; `io.kotest:kotest-assertions-core` for `shouldBe` / `shouldThrow` style. |
| Integration test DB | **Testcontainers Postgres** | Hermetic per-run Postgres container. |

> **Verification note:** Context7 could not produce explicit mid-2026 attestations for the exact patch versions of Micronaut. Before scaffolding, run `./gradlew --refresh-dependencies` against `micronaut.io/release-notes` and pin the precise patches. Treat the versions above as a floor, not a final pin.

### 3.4 Local development infrastructure

A `docker-compose.yml` at the BE root mirrors the leads-service pattern:

```yaml
services:
  postgres:
    image: postgres:18-alpine
    container_name: gpt_postgres
    restart: unless-stopped
    environment:
      POSTGRES_DB: gpt
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 30s
      timeout: 10s
      retries: 5
    ports:
      - "5432:5432"
    volumes:
      - gpt_db_data:/var/lib/postgresql/data

volumes:
  gpt_db_data:
    driver: local
```

`docker compose up -d` brings up Postgres for local runs of `./gradlew run`. Integration tests do not depend on the compose stack — they use Testcontainers.

---

## 4. Component Design

### 4.1 `core/` — Domain models (port of the existing TypeScript)

**`com.gamepricetracker.core.domain.models.Currency`** — Kotlin port of `src/game-price-tracker-be/domain/value_object/currency.ts`:

```kotlin
package com.gamepricetracker.core.domain.models

data class Currency(val amount: Double, val code: String) {
    init {
        require(amount > 0) { "Amount must be positive." }
    }

    fun isValid(): Boolean = amount > 0
}
```

Behavioural parity:
- Construction with `amount <= 0` throws (`IllegalArgumentException` via `require`, matching the TS `throw new Error('Amount must be positive.')`).
- `isValid()` returns true for positive amount.
- `code` is preserved as a free-form `String` for now; ISO-4217 validation is out of scope this pass.

**`com.gamepricetracker.core.domain.models.GamePrice`** — Kotlin port of `src/game-price-tracker-be/domain/entity/game_price.ts`:

```kotlin
package com.gamepricetracker.core.domain.models

data class GamePrice(val name: String, val currency: Currency) {
    init {
        require(currency.isValid()) { "GamePrice requires a valid Currency object." }
    }
}
```

> Note: in this Kotlin port the `isValid()` check is structurally redundant because `Currency`'s own `init` block already rejects invalid amounts. We keep the assertion verbatim to preserve behavioural parity with the TS version, and because future invariants on `Currency` may relax construction-time checks while keeping `isValid()` semantics.

### 4.2 `core/` — Tests

Tests are JUnit 5 with Kotest assertions, no Micronaut context (so they run fast and `core` stays framework-free).

`CurrencyTest` covers the three cases from `tests/domain/currency.test.ts`:
- `new Currency(0, "USD")` throws "Amount must be positive."
- `new Currency(-10, "USD")` throws "Amount must be positive."
- `new Currency(100, "USD")` → `isValid()` is true.

`GamePriceTest` covers the case from `tests/domain/game_price.test.ts`:
- `GamePrice("Bitcoin", Currency(100, "USD"))` constructs; `.name == "Bitcoin"`, `.currency.amount == 100.0`.

The original `tests/structure.test.ts` (which asserted the existence of `domain/value_object`, `domain/entity`, `ports`, `application`, `infrastructure` directories) is **not** ported. Gradle module structure is enforced by `settings.gradle.kts` at build time — a filesystem-assertion test would be redundant and brittle.

### 4.3 `application/` — Entrypoint and wiring

**`Application.kt`** — minimal Micronaut entrypoint:

```kotlin
package com.gamepricetracker.application

import io.micronaut.runtime.Micronaut.run

fun main(args: Array<String>) {
    run(*args)
}
```

**`application.yml`** — datasource + Flyway:

```yaml
micronaut:
  application:
    name: game-price-tracker

datasources:
  default:
    url: jdbc:postgresql://localhost:5432/gpt
    driver-class-name: org.postgresql.Driver
    username: postgres
    password: postgres
    schema-generate: NONE
    dialect: POSTGRES

jpa:
  default:
    properties:
      hibernate:
        hbm2ddl:
          auto: validate
        dialect: org.hibernate.dialect.PostgreSQLDialect

flyway:
  datasources:
    default:
      enabled: true
      locations: classpath:db/migration
```

**`db/migration/V1__init.sql`** — placeholder so Flyway has something to run and the wiring is exercised. A single comment is enough this pass:

```sql
-- V1: Initial schema placeholder. Real tables (game, price, favorite) land in Phase 1.
```

### 4.4 `application/` — Smoke test (`@MicronautTest` + Testcontainers)

A single integration test boots the Micronaut context against a Testcontainers Postgres and asserts:
- The application context starts.
- Flyway successfully applies `V1__init.sql`.

This proves the entire stack — Micronaut, JPA, Hibernate, the Postgres JDBC driver, Flyway, KAPT processing — is wired correctly without depending on `docker compose up`.

### 4.5 `web/` and `infra/` — placeholders only

Both modules exist with their package directories and an empty `.gitkeep`. They compile (no source = no compile work), depend on `core`, and are wired into `application/`'s dependencies so future code can land there without changing the build graph.

---

## 5. Build & Tooling

### 5.1 `buildSrc/` convention plugins

**`kotlin-conventions.gradle.kts`** — applied to all subprojects:
- Apply the Kotlin JVM plugin (version managed at the root via `plugins {}` block in `buildSrc`).
- Set Java toolchain to 21 via the foojay resolver.
- Common compile options: `-Xjsr305=strict`.
- `useJUnitPlatform()` for `Test` tasks.
- `testImplementation` defaults: `org.junit.jupiter:junit-jupiter`, `io.mockk:mockk`, `io.kotest:kotest-assertions-core`.

**`micronaut-conventions.gradle.kts`** — applied only to `application/`, `web/`, `infra/`:
- Apply `io.micronaut.minimal.application` (for `application/`) or `io.micronaut.minimal.library` (for `web/`, `infra/`).
- Add KAPT processors: `micronaut-http-validation`, `micronaut-data-processor` (where relevant).
- Add `micronaut-data-jpa`, `micronaut-flyway`, the Postgres JDBC driver, and Hibernate runtime to the relevant configurations.
- **Not** applied to `core/` — that module must remain framework-free to enforce the dependency rule.

### 5.2 Versions catalog (`gradle.properties`)

```properties
kotlinVersion=2.3.21
micronautVersion=5.1.x          # pin exact patch before scaffolding
postgresDriverVersion=42.x.x    # latest stable JDBC
testcontainersVersion=1.x.x     # latest stable
mockkVersion=1.x.x              # latest stable
kotestVersion=5.x.x             # assertions only

org.gradle.caching=true
org.gradle.parallel=true
org.gradle.daemon=true
kotlin.daemon.jvmargs=-Xmx1g
```

Exact versions for the `x` placeholders are pinned during scaffolding against current changelogs.

---

## 6. Migration Plan

### 6.1 Files removed from `game-price-tracker-be/`

- `package.json`, `pnpm-lock.yaml`, `.pnpmrc`, `node_modules/` (gitignored already), `.pnpm-store/`
- `tsconfig.json`, `vitest.config.ts`, `coverage/`
- `src/game-price-tracker-be/domain/entity/game_price.ts`
- `src/game-price-tracker-be/domain/value_object/currency.ts`
- `tests/structure.test.ts`, `tests/domain/game_price.test.ts`, `tests/domain/currency.test.ts`
- The whole `src/` and `tests/` trees.
- `.gitignore` is rewritten for JVM/Gradle/IDE rather than Node.

### 6.2 Files added to `game-price-tracker-be/`

- All files listed in §3.1.

### 6.3 Documents updated outside the BE folder

- `docs/superpowers/specs/gamepricetracker-spec.md` — §2 (Tech Stack) rewritten to reflect Kotlin/Micronaut/Gradle/Postgres. §2.1 architecture diagram updated. §2.3 routes table preserved (those are still the planned API shape). §2.4 schema section reframed as Postgres + JPA entities (sketch only — concrete entities arrive in Phase 1). §2.5 deployment section reframed for JVM (`./gradlew run` for local; deployment specifics deferred to Phase 3).
- `docs/superpowers/plans/2026-04-21-game-price-tracker-be-phase{0,1,2,3,4,5,6}.md` and `docs/superpowers/plans/2026-04-28-be-jest-to-vitest-migration.md` — moved to `docs/superpowers/plans/archive/` (preserving filenames). Frontend phase plans are untouched.
- `docs/superpowers/specs/2026-04-18-gamepricetracker-be-setup-detailed-design.md` and `docs/superpowers/specs/2026-04-28-be-jest-to-vitest-migration-design.md` — moved to `docs/superpowers/specs/archive/`. Frontend setup design is untouched.

### 6.4 Git hygiene

The refactor lands as a small series of commits (skeleton, port, docs) rather than one monolith — separate commits for: (a) archive old docs, (b) remove old TS sources + add Gradle skeleton, (c) port `Currency`/`GamePrice` + tests, (d) Postgres compose + smoke test, (e) update the master spec §2. The exact commit boundaries are a writing-plans concern, not a design concern.

---

## 7. Risks & Open Questions

| Risk | Mitigation |
|---|---|
| Micronaut 5.1.x patch unverified for mid-2026. | Pin against `micronaut.io/release-notes` immediately before scaffolding. Fall back to the latest 5.x patch with a published release note if a confidently-current pin is unavailable. |
| KAPT vs KSP: Micronaut Data may have promoted KSP by mid-2026. | Check `micronaut-projects/micronaut-data` README at scaffold time; switch to KSP if it's the documented path. KAPT remains a safe default. |
| Testcontainers requires Docker on the dev machine. | Acceptable — owner already runs Docker for the leads-service workflow. The smoke test is the only test that needs it; `core` tests are pure JVM. |
| Postgres 18 dialect class in Hibernate. | `org.hibernate.dialect.PostgreSQLDialect` is version-agnostic and works for 13+ including 18. No version-specific dialect needed. |
| Owner-personal CLAUDE.md rule "Never add co-author trailers" applies. | Commits authored solely by the operator; no `Co-Authored-By:` trailers. |

---

## 8. Acceptance Criteria

This refactor pass is complete when:

1. `cd game-price-tracker-be && ./gradlew clean build` succeeds.
2. `./gradlew test` runs and all ported unit tests (`CurrencyTest`, `GamePriceTest`) pass.
3. With `docker compose up -d` running, `./gradlew run` boots Micronaut, applies `V1__init.sql` via Flyway, and stays up until ^C.
4. The `@MicronautTest` smoke test in `application/` passes against a Testcontainers Postgres (proves the wiring without depending on the compose stack).
5. `docs/superpowers/specs/gamepricetracker-spec.md` §2 reflects the new stack.
6. Old BE phase plans live under `docs/superpowers/plans/archive/`; old BE setup design under `docs/superpowers/specs/archive/`.
7. The frontend (`game-price-tracker-fe`, if present) is untouched.

---

## 9. Out-of-Scope Follow-ups (next phase plans)

These are intentionally deferred. Each will get its own design + plan after this pass lands:

- **Phase 1**: ITAD client + Steam Store client; `Game` and `Price` JPA entities; `GET /api/search`, `GET /api/game/:id/prices` controllers; 6-hour TTL freshness check.
- **Phase 2**: Nintendo eShop client; favorites endpoints; price history.
- **Phase 3**: VPS deployment (replaces the PM2/Caddy plan from the original spec — JVM equivalent TBD).
- **Phase 4+**: CheapShark integration, deal discovery, scheduled refresh jobs (Micronaut `@Scheduled`).
