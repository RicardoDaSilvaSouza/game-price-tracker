# Backend Tech-Stack Refactor (Node.js → Kotlin + Micronaut + Postgres) — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the TypeScript/Node.js backend skeleton with a Kotlin + Micronaut + Gradle multi-module project backed by Postgres, porting the existing `Currency` value object and `GamePrice` entity (plus their tests) and proving the stack boots end-to-end.

**Architecture:** Slim hexagonal multi-module Gradle layout (`application/`, `core/`, `web/`, `infra/`) plus `buildSrc/` convention plugins. `core/` stays framework-free (pure Kotlin) and contains the ported domain models. `application/` is the Micronaut entrypoint, owns `application.yml`, and hosts Flyway migrations. `web/` and `infra/` are placeholders this pass.

**Tech Stack:** Kotlin 2.3.21, Micronaut 5.1.x (latest patch — verify before pinning), Gradle 8.x kotlin-dsl, Java 21 LTS, KAPT annotation processing, Micronaut Data JPA + Hibernate, Flyway, Postgres 18-alpine, JUnit 5, MockK, Kotest assertions, Testcontainers.

**Spec:** `docs/superpowers/specs/2026-06-01-backend-kotlin-micronaut-refactor-design.md`

**Working directory for every step:** `/Users/ricardodasilvasouza/workspace/personal/game-price-tracker`

**Pre-flight checklist before Task 1:**
- Local Docker daemon running (Testcontainers + docker-compose need it).
- JDK 21 available on PATH (or trust Gradle's foojay toolchain to fetch it).
- No uncommitted changes in the working tree (`git status` clean except `graphify-out/`).

---

## File Inventory

### Removed
- `game-price-tracker-be/package.json`
- `game-price-tracker-be/pnpm-lock.yaml`
- `game-price-tracker-be/.pnpmrc`
- `game-price-tracker-be/tsconfig.json`
- `game-price-tracker-be/vitest.config.ts`
- `game-price-tracker-be/.gitignore` (rewritten, see Created)
- `game-price-tracker-be/src/` (entire tree)
- `game-price-tracker-be/tests/` (entire tree)
- `game-price-tracker-be/coverage/` (if present, gitignored)
- `game-price-tracker-be/node_modules/` (gitignored)
- `game-price-tracker-be/.pnpm-store/` (gitignored)

### Archived (moved, not deleted)
- `docs/superpowers/plans/2026-04-21-game-price-tracker-be-phase{0,1,2,3,4,5,6}.md` → `docs/superpowers/plans/archive/`
- `docs/superpowers/plans/2026-04-28-be-jest-to-vitest-migration.md` → `docs/superpowers/plans/archive/`
- `docs/superpowers/specs/2026-04-18-gamepricetracker-be-setup-detailed-design.md` → `docs/superpowers/specs/archive/`
- `docs/superpowers/specs/2026-04-28-be-jest-to-vitest-migration-design.md` → `docs/superpowers/specs/archive/`

### Created
- `game-price-tracker-be/.gitignore` (JVM/Gradle/IDE)
- `game-price-tracker-be/settings.gradle.kts`
- `game-price-tracker-be/build.gradle.kts`
- `game-price-tracker-be/gradle.properties`
- `game-price-tracker-be/gradlew`, `gradlew.bat`, `gradle/wrapper/*` (via `gradle wrapper`)
- `game-price-tracker-be/docker-compose.yml`
- `game-price-tracker-be/buildSrc/build.gradle.kts`
- `game-price-tracker-be/buildSrc/settings.gradle.kts`
- `game-price-tracker-be/buildSrc/src/main/kotlin/kotlin-conventions.gradle.kts`
- `game-price-tracker-be/buildSrc/src/main/kotlin/micronaut-conventions.gradle.kts`
- `game-price-tracker-be/core/build.gradle.kts`
- `game-price-tracker-be/core/src/main/kotlin/com/gamepricetracker/core/domain/models/Currency.kt`
- `game-price-tracker-be/core/src/main/kotlin/com/gamepricetracker/core/domain/models/GamePrice.kt`
- `game-price-tracker-be/core/src/test/kotlin/com/gamepricetracker/core/domain/models/CurrencyTest.kt`
- `game-price-tracker-be/core/src/test/kotlin/com/gamepricetracker/core/domain/models/GamePriceTest.kt`
- `game-price-tracker-be/web/build.gradle.kts`
- `game-price-tracker-be/web/src/main/kotlin/com/gamepricetracker/web/.gitkeep`
- `game-price-tracker-be/infra/build.gradle.kts`
- `game-price-tracker-be/infra/src/main/kotlin/com/gamepricetracker/infra/.gitkeep`
- `game-price-tracker-be/application/build.gradle.kts`
- `game-price-tracker-be/application/src/main/kotlin/com/gamepricetracker/application/Application.kt`
- `game-price-tracker-be/application/src/main/resources/application.yml`
- `game-price-tracker-be/application/src/main/resources/logback.xml`
- `game-price-tracker-be/application/src/main/resources/db/migration/V1__init.sql`
- `game-price-tracker-be/application/src/test/kotlin/com/gamepricetracker/application/ApplicationSmokeTest.kt`
- `game-price-tracker-be/application/src/test/resources/application-test.yml`

### Modified
- `docs/superpowers/specs/gamepricetracker-spec.md` (§2 rewritten for the JVM stack; §2.1 diagram updated)

---

## Task 1: Archive legacy BE plans and setup design docs

**Files:**
- Move: `docs/superpowers/plans/2026-04-21-game-price-tracker-be-phase{0..6}.md` → `docs/superpowers/plans/archive/`
- Move: `docs/superpowers/plans/2026-04-28-be-jest-to-vitest-migration.md` → `docs/superpowers/plans/archive/`
- Move: `docs/superpowers/specs/2026-04-18-gamepricetracker-be-setup-detailed-design.md` → `docs/superpowers/specs/archive/`
- Move: `docs/superpowers/specs/2026-04-28-be-jest-to-vitest-migration-design.md` → `docs/superpowers/specs/archive/`

- [ ] **Step 1: Create archive directories**

```bash
mkdir -p docs/superpowers/plans/archive docs/superpowers/specs/archive
```

- [ ] **Step 2: Move legacy BE plan files via `git mv` so history is preserved**

```bash
git mv docs/superpowers/plans/2026-04-21-game-price-tracker-be-phase0.md docs/superpowers/plans/archive/
git mv docs/superpowers/plans/2026-04-21-game-price-tracker-be-phase1.md docs/superpowers/plans/archive/
git mv docs/superpowers/plans/2026-04-21-game-price-tracker-be-phase2.md docs/superpowers/plans/archive/
git mv docs/superpowers/plans/2026-04-21-game-price-tracker-be-phase3.md docs/superpowers/plans/archive/
git mv docs/superpowers/plans/2026-04-21-game-price-tracker-be-phase4.md docs/superpowers/plans/archive/
git mv docs/superpowers/plans/2026-04-21-game-price-tracker-be-phase5.md docs/superpowers/plans/archive/
git mv docs/superpowers/plans/2026-04-21-game-price-tracker-be-phase6.md docs/superpowers/plans/archive/
git mv docs/superpowers/plans/2026-04-28-be-jest-to-vitest-migration.md docs/superpowers/plans/archive/
git mv docs/superpowers/specs/2026-04-18-gamepricetracker-be-setup-detailed-design.md docs/superpowers/specs/archive/
git mv docs/superpowers/specs/2026-04-28-be-jest-to-vitest-migration-design.md docs/superpowers/specs/archive/
```

- [ ] **Step 3: Verify the FE plans and the master spec are untouched**

```bash
ls docs/superpowers/plans/2026-04-21-game-price-tracker-fe-phase*.md | wc -l
ls docs/superpowers/specs/gamepricetracker-spec.md
```
Expected: `7` (seven FE phase plans still in place) and the master spec listed.

- [ ] **Step 4: Verify staged state**

```bash
git status --short
```
Expected: a list of renames (`R  docs/.../old → docs/.../archive/old`) — no untracked files for this task.

- [ ] **Step 5: Commit**

```bash
git commit -m "docs: archive Node.js backend phase plans superseded by Kotlin refactor"
```

---

## Task 2: Remove the Node.js backend sources

**Files:**
- Delete: everything under `game-price-tracker-be/` *except* the directory itself (we keep the dir; new Gradle project lands inside).

- [ ] **Step 1: List what's currently in the backend dir for the record**

```bash
ls -A game-price-tracker-be/
```
Expected output includes: `.pnpm-store`, `.pnpmrc`, `.gitignore`, `coverage`, `node_modules`, `package.json`, `pnpm-lock.yaml`, `src`, `tests`, `tsconfig.json`, `vitest.config.ts`.

- [ ] **Step 2: Remove tracked files via `git rm`**

```bash
git rm game-price-tracker-be/.pnpmrc \
       game-price-tracker-be/.gitignore \
       game-price-tracker-be/package.json \
       game-price-tracker-be/pnpm-lock.yaml \
       game-price-tracker-be/tsconfig.json \
       game-price-tracker-be/vitest.config.ts
git rm -r game-price-tracker-be/src game-price-tracker-be/tests
```

- [ ] **Step 3: Remove untracked build artifacts**

```bash
rm -rf game-price-tracker-be/.pnpm-store \
       game-price-tracker-be/coverage \
       game-price-tracker-be/node_modules
```

- [ ] **Step 4: Verify the directory is empty**

```bash
ls -A game-price-tracker-be/ | wc -l
```
Expected: `0`.

- [ ] **Step 5: Commit**

```bash
git commit -m "chore(be): remove Node.js backend ahead of Kotlin/Micronaut refactor"
```

---

## Task 3: Scaffold the Gradle multi-module skeleton (root files)

**Files:**
- Create: `game-price-tracker-be/.gitignore`
- Create: `game-price-tracker-be/gradle.properties`
- Create: `game-price-tracker-be/settings.gradle.kts`
- Create: `game-price-tracker-be/build.gradle.kts`

- [ ] **Step 1: Create `game-price-tracker-be/.gitignore`**

```gitignore
# Gradle
.gradle/
build/
!gradle/wrapper/gradle-wrapper.jar
!**/src/**/build/
!**/src/**/.gradle/

# IDE
.idea/
*.iml
*.ipr
*.iws
.vscode/

# OS
.DS_Store
Thumbs.db

# Kotlin
.kotlin/

# Local config
.env
.env.local
```

- [ ] **Step 2: Create `game-price-tracker-be/gradle.properties`**

```properties
# JVM toolchain
kotlinVersion=2.3.21
micronautVersion=5.1.3
micronautDataVersion=5.0.0
hibernateVersion=6.6.2.Final
postgresDriverVersion=42.7.4
flywayVersion=10.20.1

# Test stack
junitVersion=5.11.3
mockkVersion=1.13.13
kotestVersion=5.9.1
testcontainersVersion=1.20.3

# Gradle runtime
org.gradle.caching=true
org.gradle.parallel=true
org.gradle.daemon=true
org.gradle.configureOnDemand=true
kotlin.daemon.jvmargs=-Xmx1g
kotlin.incremental=true
```

> **Version verification:** Before committing, run `grep -E "Version=" game-price-tracker-be/gradle.properties` and confirm each version against its official changelog. `micronautVersion=5.1.3` is the floor recommendation; if a newer 5.1.x patch is current per `micronaut.io/release-notes`, bump it here.

- [ ] **Step 3: Create `game-price-tracker-be/settings.gradle.kts`**

```kotlin
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "game-price-tracker-be"

include(
    "application",
    "core",
    "web",
    "infra",
)
```

- [ ] **Step 4: Create `game-price-tracker-be/build.gradle.kts`**

```kotlin
plugins {
    base
}

allprojects {
    group = "com.gamepricetracker"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}
```

- [ ] **Step 5: Verify files exist with the expected content**

```bash
ls -la game-price-tracker-be/
test -f game-price-tracker-be/.gitignore && echo OK
test -f game-price-tracker-be/gradle.properties && echo OK
test -f game-price-tracker-be/settings.gradle.kts && echo OK
test -f game-price-tracker-be/build.gradle.kts && echo OK
```
Expected: four `OK` lines.

- [ ] **Step 6: Commit**

```bash
git add game-price-tracker-be/.gitignore \
        game-price-tracker-be/gradle.properties \
        game-price-tracker-be/settings.gradle.kts \
        game-price-tracker-be/build.gradle.kts
git commit -m "chore(be): scaffold Gradle multi-module root"
```

---

## Task 4: Add the Gradle wrapper

**Files:**
- Create: `game-price-tracker-be/gradlew`
- Create: `game-price-tracker-be/gradlew.bat`
- Create: `game-price-tracker-be/gradle/wrapper/gradle-wrapper.jar`
- Create: `game-price-tracker-be/gradle/wrapper/gradle-wrapper.properties`

- [ ] **Step 1: From inside the BE dir, run the wrapper task using a system Gradle**

The wrapper generates itself. If you have Gradle 8.x installed:

```bash
cd game-price-tracker-be && gradle wrapper --gradle-version 8.10.2 --distribution-type bin
```

If you don't have system Gradle: download the binary distribution from `https://gradle.org/releases/`, unpack it, and invoke its `bin/gradle` with the same command. Do **not** hand-write wrapper files.

- [ ] **Step 2: Verify wrapper files**

```bash
test -x game-price-tracker-be/gradlew && echo OK
test -f game-price-tracker-be/gradle/wrapper/gradle-wrapper.jar && echo OK
test -f game-price-tracker-be/gradle/wrapper/gradle-wrapper.properties && echo OK
```
Expected: three `OK` lines.

- [ ] **Step 3: Smoke-test the wrapper**

```bash
cd game-price-tracker-be && ./gradlew --version
```
Expected: Gradle 8.10.2 listed; JVM 21.x; Kotlin embedded version printed.

- [ ] **Step 4: Stage and commit**

```bash
git add game-price-tracker-be/gradlew game-price-tracker-be/gradlew.bat game-price-tracker-be/gradle
git commit -m "chore(be): add Gradle 8.10.2 wrapper"
```

---

## Task 5: Add `buildSrc/` with the Kotlin convention plugin

**Files:**
- Create: `game-price-tracker-be/buildSrc/settings.gradle.kts`
- Create: `game-price-tracker-be/buildSrc/build.gradle.kts`
- Create: `game-price-tracker-be/buildSrc/src/main/kotlin/kotlin-conventions.gradle.kts`

- [ ] **Step 1: Create `buildSrc/settings.gradle.kts`**

```kotlin
rootProject.name = "buildSrc"
```

- [ ] **Step 2: Create `buildSrc/build.gradle.kts`**

```kotlin
import java.util.Properties

plugins {
    `kotlin-dsl`
}

fun rootProperties(): Properties {
    val props = Properties()
    file("../gradle.properties").inputStream().use { props.load(it) }
    return props
}

val rootProps = rootProperties()
val kotlinVersion: String = rootProps.getProperty("kotlinVersion")
val micronautVersion: String = rootProps.getProperty("micronautVersion")

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-noarg:$kotlinVersion")
    implementation("io.micronaut.gradle:micronaut-gradle-plugin:$micronautVersion")
    implementation("io.micronaut.gradle:micronaut-minimal-application-plugin:$micronautVersion")
    implementation("io.micronaut.gradle:micronaut-minimal-library-plugin:$micronautVersion")
}
```

> Plugin coordinate verification: confirm the Micronaut Gradle plugin coordinates against `https://plugins.gradle.org/search?term=micronaut` before committing. The `io.micronaut.minimal.application` and `io.micronaut.minimal.library` plugin IDs are stable across Micronaut 4.x and 5.x.

- [ ] **Step 3: Create `buildSrc/src/main/kotlin/kotlin-conventions.gradle.kts`**

```kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
}

val kotlinVersion = providers.gradleProperty("kotlinVersion").get()
val junitVersion = providers.gradleProperty("junitVersion").get()
val mockkVersion = providers.gradleProperty("mockkVersion").get()
val kotestVersion = providers.gradleProperty("kotestVersion").get()

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
```

- [ ] **Step 4: Verify `buildSrc/` compiles via Gradle**

```bash
cd game-price-tracker-be && ./gradlew :buildSrc:assemble
```
Expected: BUILD SUCCESSFUL. (This will pull plugin dependencies on first run.)

- [ ] **Step 5: Commit**

```bash
git add game-price-tracker-be/buildSrc
git commit -m "chore(be): add buildSrc with kotlin-conventions plugin"
```

---

## Task 6: Add the `core/` module with a build script

**Files:**
- Create: `game-price-tracker-be/core/build.gradle.kts`
- Create: `game-price-tracker-be/core/src/main/kotlin/.gitkeep`
- Create: `game-price-tracker-be/core/src/test/kotlin/.gitkeep`

- [ ] **Step 1: Create `core/build.gradle.kts`**

```kotlin
plugins {
    id("kotlin-conventions")
}
```

> `core/` deliberately does NOT apply any Micronaut convention plugin. The module must remain framework-free so layer violations from `web/` or `infra/` are compile errors.

- [ ] **Step 2: Create placeholder source/test directories**

```bash
mkdir -p game-price-tracker-be/core/src/main/kotlin/com/gamepricetracker/core/domain/models
mkdir -p game-price-tracker-be/core/src/test/kotlin/com/gamepricetracker/core/domain/models
touch game-price-tracker-be/core/src/main/kotlin/.gitkeep
touch game-price-tracker-be/core/src/test/kotlin/.gitkeep
```

- [ ] **Step 3: Verify the module resolves**

```bash
cd game-price-tracker-be && ./gradlew :core:assemble
```
Expected: BUILD SUCCESSFUL with no source compilation (no `.kt` files yet).

- [ ] **Step 4: Commit**

```bash
git add game-price-tracker-be/core
git commit -m "chore(be): add core module skeleton"
```

---

## Task 7: Port the `Currency` value object with TDD

**Files:**
- Create test: `game-price-tracker-be/core/src/test/kotlin/com/gamepricetracker/core/domain/models/CurrencyTest.kt`
- Create source: `game-price-tracker-be/core/src/main/kotlin/com/gamepricetracker/core/domain/models/Currency.kt`

- [ ] **Step 1: Write the failing test**

`game-price-tracker-be/core/src/test/kotlin/com/gamepricetracker/core/domain/models/CurrencyTest.kt`:

```kotlin
package com.gamepricetracker.core.domain.models

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CurrencyTest {

    @Test
    fun `throws when amount is zero`() {
        val ex = shouldThrow<IllegalArgumentException> {
            Currency(amount = 0.0, code = "USD")
        }
        ex.message shouldBe "Amount must be positive."
    }

    @Test
    fun `throws when amount is negative`() {
        val ex = shouldThrow<IllegalArgumentException> {
            Currency(amount = -10.0, code = "USD")
        }
        ex.message shouldBe "Amount must be positive."
    }

    @Test
    fun `is valid when amount is positive`() {
        val currency = Currency(amount = 100.0, code = "USD")
        currency.isValid() shouldBe true
    }
}
```

- [ ] **Step 2: Run the test to confirm it fails**

```bash
cd game-price-tracker-be && ./gradlew :core:test --tests "com.gamepricetracker.core.domain.models.CurrencyTest"
```
Expected: FAILURE — `Unresolved reference: Currency` (the class does not exist yet).

- [ ] **Step 3: Write the minimal implementation**

`game-price-tracker-be/core/src/main/kotlin/com/gamepricetracker/core/domain/models/Currency.kt`:

```kotlin
package com.gamepricetracker.core.domain.models

data class Currency(val amount: Double, val code: String) {
    init {
        require(amount > 0) { "Amount must be positive." }
    }

    fun isValid(): Boolean = amount > 0
}
```

- [ ] **Step 4: Run the test to confirm it passes**

```bash
cd game-price-tracker-be && ./gradlew :core:test --tests "com.gamepricetracker.core.domain.models.CurrencyTest"
```
Expected: BUILD SUCCESSFUL — 3 tests, 3 passed.

- [ ] **Step 5: Commit**

```bash
git add game-price-tracker-be/core/src/main/kotlin/com/gamepricetracker/core/domain/models/Currency.kt \
        game-price-tracker-be/core/src/test/kotlin/com/gamepricetracker/core/domain/models/CurrencyTest.kt
git commit -m "feat(be): port Currency value object to core/models"
```

---

## Task 8: Port the `GamePrice` entity with TDD

**Files:**
- Create test: `game-price-tracker-be/core/src/test/kotlin/com/gamepricetracker/core/domain/models/GamePriceTest.kt`
- Create source: `game-price-tracker-be/core/src/main/kotlin/com/gamepricetracker/core/domain/models/GamePrice.kt`

- [ ] **Step 1: Write the failing test**

`game-price-tracker-be/core/src/test/kotlin/com/gamepricetracker/core/domain/models/GamePriceTest.kt`:

```kotlin
package com.gamepricetracker.core.domain.models

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class GamePriceTest {

    @Test
    fun `accepts a valid Currency object`() {
        val validCurrency = Currency(amount = 100.0, code = "USD")
        val gamePrice = GamePrice(name = "Bitcoin", currency = validCurrency)

        gamePrice.name shouldBe "Bitcoin"
        gamePrice.currency.amount shouldBe 100.0
    }
}
```

- [ ] **Step 2: Run the test to confirm it fails**

```bash
cd game-price-tracker-be && ./gradlew :core:test --tests "com.gamepricetracker.core.domain.models.GamePriceTest"
```
Expected: FAILURE — `Unresolved reference: GamePrice`.

- [ ] **Step 3: Write the minimal implementation**

`game-price-tracker-be/core/src/main/kotlin/com/gamepricetracker/core/domain/models/GamePrice.kt`:

```kotlin
package com.gamepricetracker.core.domain.models

data class GamePrice(val name: String, val currency: Currency) {
    init {
        require(currency.isValid()) { "GamePrice requires a valid Currency object." }
    }
}
```

- [ ] **Step 4: Run all `core` tests to confirm both classes pass together**

```bash
cd game-price-tracker-be && ./gradlew :core:test
```
Expected: BUILD SUCCESSFUL — 4 tests, 4 passed.

- [ ] **Step 5: Commit**

```bash
git add game-price-tracker-be/core/src/main/kotlin/com/gamepricetracker/core/domain/models/GamePrice.kt \
        game-price-tracker-be/core/src/test/kotlin/com/gamepricetracker/core/domain/models/GamePriceTest.kt
git commit -m "feat(be): port GamePrice entity to core/models"
```

---

## Task 9: Add the Micronaut convention plugin to `buildSrc/`

**Files:**
- Create: `game-price-tracker-be/buildSrc/src/main/kotlin/micronaut-conventions.gradle.kts`

- [ ] **Step 1: Create `buildSrc/src/main/kotlin/micronaut-conventions.gradle.kts`**

```kotlin
plugins {
    id("kotlin-conventions")
    kotlin("kapt")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
}

val micronautVersion = providers.gradleProperty("micronautVersion").get()

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
    annotation("io.micronaut.aop.Around")
}

noArg {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

dependencies {
    "kapt"(platform("io.micronaut.platform:micronaut-platform:$micronautVersion"))
    "kapt"("io.micronaut:micronaut-inject-java")
    "kapt"("io.micronaut.validation:micronaut-validation-processor")

    implementation(platform("io.micronaut.platform:micronaut-platform:$micronautVersion"))
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.validation:micronaut-validation")
    implementation("jakarta.annotation:jakarta.annotation-api")
    runtimeOnly("ch.qos.logback:logback-classic")
}
```

> This convention is applied to `application/`, `web/`, and `infra/` only — **never** to `core/`.

- [ ] **Step 2: Verify `buildSrc/` still compiles**

```bash
cd game-price-tracker-be && ./gradlew :buildSrc:assemble
```
Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
git add game-price-tracker-be/buildSrc/src/main/kotlin/micronaut-conventions.gradle.kts
git commit -m "chore(be): add micronaut-conventions plugin"
```

---

## Task 10: Add the `web/` placeholder module

**Files:**
- Create: `game-price-tracker-be/web/build.gradle.kts`
- Create: `game-price-tracker-be/web/src/main/kotlin/com/gamepricetracker/web/.gitkeep`

- [ ] **Step 1: Create `web/build.gradle.kts`**

```kotlin
plugins {
    id("micronaut-conventions")
}

micronaut {
    version.set(providers.gradleProperty("micronautVersion"))
    runtime("netty")
}

dependencies {
    implementation(project(":core"))
    implementation("io.micronaut:micronaut-http-server-netty")
}
```

- [ ] **Step 2: Create the placeholder package**

```bash
mkdir -p game-price-tracker-be/web/src/main/kotlin/com/gamepricetracker/web
touch game-price-tracker-be/web/src/main/kotlin/com/gamepricetracker/web/.gitkeep
```

- [ ] **Step 3: Verify the module resolves**

```bash
cd game-price-tracker-be && ./gradlew :web:assemble
```
Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Commit**

```bash
git add game-price-tracker-be/web
git commit -m "chore(be): add web module skeleton"
```

---

## Task 11: Add the `infra/` placeholder module

**Files:**
- Create: `game-price-tracker-be/infra/build.gradle.kts`
- Create: `game-price-tracker-be/infra/src/main/kotlin/com/gamepricetracker/infra/.gitkeep`

- [ ] **Step 1: Create `infra/build.gradle.kts`**

```kotlin
plugins {
    id("micronaut-conventions")
}

micronaut {
    version.set(providers.gradleProperty("micronautVersion"))
}

val postgresDriverVersion = providers.gradleProperty("postgresDriverVersion").get()
val flywayVersion = providers.gradleProperty("flywayVersion").get()

dependencies {
    implementation(project(":core"))

    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.flyway:micronaut-flyway")

    runtimeOnly("org.postgresql:postgresql:$postgresDriverVersion")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:$flywayVersion")
}
```

- [ ] **Step 2: Create the placeholder package**

```bash
mkdir -p game-price-tracker-be/infra/src/main/kotlin/com/gamepricetracker/infra
touch game-price-tracker-be/infra/src/main/kotlin/com/gamepricetracker/infra/.gitkeep
```

- [ ] **Step 3: Verify the module resolves**

```bash
cd game-price-tracker-be && ./gradlew :infra:assemble
```
Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Commit**

```bash
git add game-price-tracker-be/infra
git commit -m "chore(be): add infra module skeleton with Data JPA + Flyway deps"
```

---

## Task 12: Add the `application/` module entrypoint

**Files:**
- Create: `game-price-tracker-be/application/build.gradle.kts`
- Create: `game-price-tracker-be/application/src/main/kotlin/com/gamepricetracker/application/Application.kt`
- Create: `game-price-tracker-be/application/src/main/resources/application.yml`
- Create: `game-price-tracker-be/application/src/main/resources/logback.xml`
- Create: `game-price-tracker-be/application/src/main/resources/db/migration/V1__init.sql`

- [ ] **Step 1: Create `application/build.gradle.kts`**

```kotlin
plugins {
    id("micronaut-conventions")
    application
}

application {
    mainClass.set("com.gamepricetracker.application.ApplicationKt")
}

micronaut {
    version.set(providers.gradleProperty("micronautVersion"))
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.gamepricetracker.*")
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":web"))
    implementation(project(":infra"))

    implementation("io.micronaut:micronaut-http-server-netty")
    runtimeOnly("io.micronaut.flyway:micronaut-flyway")
}
```

- [ ] **Step 2: Create the entrypoint**

`game-price-tracker-be/application/src/main/kotlin/com/gamepricetracker/application/Application.kt`:

```kotlin
package com.gamepricetracker.application

import io.micronaut.runtime.Micronaut.run

fun main(args: Array<String>) {
    run(*args)
}
```

- [ ] **Step 3: Create `application.yml`**

`game-price-tracker-be/application/src/main/resources/application.yml`:

```yaml
micronaut:
  application:
    name: game-price-tracker
  server:
    port: 8080

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

- [ ] **Step 4: Create `logback.xml`**

`game-price-tracker-be/application/src/main/resources/logback.xml`:

```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>

    <logger name="com.gamepricetracker" level="DEBUG"/>
</configuration>
```

- [ ] **Step 5: Create the Flyway placeholder migration**

`game-price-tracker-be/application/src/main/resources/db/migration/V1__init.sql`:

```sql
-- V1: Initial schema placeholder.
-- Real tables (game, price, favorite) land in Phase 1.
CREATE TABLE IF NOT EXISTS schema_version_marker (
    id INTEGER PRIMARY KEY,
    note TEXT NOT NULL
);
INSERT INTO schema_version_marker (id, note) VALUES (1, 'V1 placeholder applied')
    ON CONFLICT (id) DO NOTHING;
```

> The marker table gives Flyway something non-trivial to apply so a successful run is visible in `flyway_schema_history`. It will be dropped in Phase 1 when real tables arrive.

- [ ] **Step 6: Verify the module assembles**

```bash
cd game-price-tracker-be && ./gradlew :application:assemble
```
Expected: BUILD SUCCESSFUL. KAPT runs and processes `Application.kt`.

- [ ] **Step 7: Commit**

```bash
git add game-price-tracker-be/application
git commit -m "feat(be): add Micronaut application module with Flyway placeholder migration"
```

---

## Task 13: Add `docker-compose.yml` for local Postgres

**Files:**
- Create: `game-price-tracker-be/docker-compose.yml`

- [ ] **Step 1: Create `docker-compose.yml`**

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
      test: ["CMD-SHELL", "pg_isready -U postgres -d gpt"]
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

- [ ] **Step 2: Smoke-test the compose stack**

```bash
cd game-price-tracker-be && docker compose up -d
docker compose ps
```
Expected: `gpt_postgres` container `Up` and `healthy` within ~30 seconds.

- [ ] **Step 3: Boot the application against the live Postgres**

```bash
cd game-price-tracker-be && ./gradlew :application:run &
APP_PID=$!
sleep 20
curl -fsS http://localhost:8080/health 2>/dev/null || echo "(no /health endpoint configured yet — that's fine)"
kill $APP_PID 2>/dev/null
wait $APP_PID 2>/dev/null
```
Expected: log lines from the run output show:
- `Flyway` applying `V1__init.sql`.
- `Startup completed in <N>ms`.

Then Flyway recorded the migration:

```bash
docker compose exec -T postgres psql -U postgres -d gpt -c "SELECT version, description, success FROM flyway_schema_history;"
```
Expected: one row, `1 | init | t`.

- [ ] **Step 4: Tear down the compose stack**

```bash
cd game-price-tracker-be && docker compose down
```

- [ ] **Step 5: Commit**

```bash
git add game-price-tracker-be/docker-compose.yml
git commit -m "chore(be): add docker-compose for local Postgres 18"
```

---

## Task 14: Add the Testcontainers-backed `@MicronautTest` smoke test

**Files:**
- Create: `game-price-tracker-be/application/src/test/kotlin/com/gamepricetracker/application/ApplicationSmokeTest.kt`
- Create: `game-price-tracker-be/application/src/test/resources/application-test.yml`
- Modify: `game-price-tracker-be/application/build.gradle.kts` (add test deps)

- [ ] **Step 1: Add Testcontainers dependencies to `application/build.gradle.kts`**

Open `game-price-tracker-be/application/build.gradle.kts` and replace its `dependencies` block with:

```kotlin
dependencies {
    implementation(project(":core"))
    implementation(project(":web"))
    implementation(project(":infra"))

    implementation("io.micronaut:micronaut-http-server-netty")
    runtimeOnly("io.micronaut.flyway:micronaut-flyway")

    val testcontainersVersion = providers.gradleProperty("testcontainersVersion").get()

    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
}
```

- [ ] **Step 2: Create the test resource config**

`game-price-tracker-be/application/src/test/resources/application-test.yml`:

```yaml
flyway:
  datasources:
    default:
      enabled: true
      locations: classpath:db/migration
```

This file overrides nothing structural — the test wires the datasource URL programmatically from the Testcontainer in the next step.

- [ ] **Step 3: Write the smoke test**

`game-price-tracker-be/application/src/test/kotlin/com/gamepricetracker/application/ApplicationSmokeTest.kt`:

```kotlin
package com.gamepricetracker.application

import io.kotest.matchers.shouldBe
import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import javax.sql.DataSource

@MicronautTest
@Testcontainers
class ApplicationSmokeTest {

    companion object {
        @Container
        @JvmStatic
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:18-alpine")
            .withDatabaseName("gpt")
            .withUsername("postgres")
            .withPassword("postgres")
            .also { it.start() }

        init {
            System.setProperty("datasources.default.url", postgres.jdbcUrl)
            System.setProperty("datasources.default.username", postgres.username)
            System.setProperty("datasources.default.password", postgres.password)
        }
    }

    @Inject lateinit var ctx: ApplicationContext
    @Inject lateinit var app: EmbeddedApplication<*>
    @Inject lateinit var dataSource: DataSource

    @Test
    fun `Micronaut context starts`() {
        app.isRunning shouldBe true
    }

    @Test
    fun `Flyway applies V1 migration`() {
        dataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery(
                    "SELECT success FROM flyway_schema_history WHERE version = '1'"
                )
                rs.next() shouldBe true
                rs.getBoolean("success") shouldBe true
            }
        }
    }
}
```

- [ ] **Step 4: Run the smoke test**

```bash
cd game-price-tracker-be && ./gradlew :application:test
```
Expected: BUILD SUCCESSFUL — 2 tests, 2 passed. Testcontainers pulls `postgres:18-alpine` on first run (~30 seconds).

- [ ] **Step 5: Commit**

```bash
git add game-price-tracker-be/application/build.gradle.kts \
        game-price-tracker-be/application/src/test
git commit -m "test(be): add Testcontainers-backed Micronaut smoke test"
```

---

## Task 15: Run the full test suite and a build sanity check

- [ ] **Step 1: Clean build the whole project**

```bash
cd game-price-tracker-be && ./gradlew clean build
```
Expected: BUILD SUCCESSFUL. All modules compile; all tests pass (4 in `core`, 2 in `application`).

- [ ] **Step 2: Confirm test counts in the report**

```bash
grep -hE '(tests|tests completed)' game-price-tracker-be/*/build/reports/tests/test/index.html 2>/dev/null | head -20
```
Expected: `core` reports 4 tests, `application` reports 2 tests. (HTML scraping is tolerant; if Gradle output already showed `6 tests, 6 passed`, you can skip this step.)

- [ ] **Step 3: Boot once more against docker-compose Postgres as a final smoke check**

```bash
cd game-price-tracker-be && docker compose up -d && sleep 5
./gradlew :application:run &
APP_PID=$!
sleep 20
docker compose exec -T postgres psql -U postgres -d gpt -c "SELECT COUNT(*) FROM flyway_schema_history;"
kill $APP_PID 2>/dev/null
wait $APP_PID 2>/dev/null
docker compose down
```
Expected: `count` is `1` (V1 applied exactly once).

- [ ] **Step 4: No commit for this task (verification only)**

If everything passes, proceed to Task 16. If something fails, stop and triage — do not modify the plan blindly.

---

## Task 16: Update the master product spec §2 to reflect the new stack

**Files:**
- Modify: `docs/superpowers/specs/gamepricetracker-spec.md` — §2 (Tech Stack), §2.1 (Architecture Overview diagram), §2.3 (Backend), §2.4 (Database Schema), §2.5 (Deployment).

This task does not require TDD — it's documentation. The replacements below are scoped edits, not a rewrite of the whole spec.

- [ ] **Step 1: Replace §2.1 architecture overview prose and diagram**

Open `docs/superpowers/specs/gamepricetracker-spec.md` and locate the heading `### 2.1 Architecture Overview`. Replace the section's prose (the paragraph that starts "The application follows a two-tier architecture...") and the ASCII diagram below it with:

```markdown
### 2.1 Architecture Overview

The application is a two-tier system: a **Kotlin + Micronaut backend** running locally or on a personal VPS, and an **Expo (React Native + Web) frontend** that connects to it. Persistence is **Postgres 18** (run locally via `docker compose up`), accessed through **Micronaut Data JPA** with schema migrations managed by **Flyway**. Since only one user accesses the service, the database lives on the same host as the backend.

```
┌─────────────────────────────────────────────────┐
│                   Expo App                      │
│     iOS  ·  Android  ·  Web (same codebase)     │
└────────────────────┬────────────────────────────┘
                     │ HTTP (local network / VPS)
┌────────────────────▼────────────────────────────┐
│       Micronaut HTTP Server (Kotlin / Java 21)  │
│  ┌──────────────────────────────────────────┐   │
│  │  Price Aggregation Layer                 │   │
│  │  ITAD · Steam Store · Nintendo eShop     │   │
│  └─────────────────┬────────────────────────┘   │
│  ┌──────────────────▼────────────────────────┐  │
│  │  Postgres 18 (via Micronaut Data JPA)     │  │
│  │  games · prices · favorites               │  │
│  └───────────────────────────────────────────┘  │
└─────────────────────────────────────────────────┘
```
```

- [ ] **Step 2: Replace §2.2 — keep this section untouched**

§2.2 covers the frontend; leave it as-is. No edits in this step.

- [ ] **Step 3: Replace §2.3 "Backend" table and the prose around it**

Locate `### 2.3 Backend — Node.js + Fastify`. Replace the heading and the table/prose down to the end of §2.3 with:

```markdown
### 2.3 Backend — Kotlin + Micronaut

| Concern | Library / Tool | Notes |
|---|---|---|
| Runtime | **Java 21 LTS** | Foojay toolchain resolver pins it across machines |
| Language | **Kotlin 2.3.21** | Strict null-safety, data classes for domain models |
| Framework | **Micronaut 5.1.x** | Compile-time DI via KAPT, low memory footprint, Netty server |
| Build | **Gradle 8.10.2** (kotlin-dsl) | Multi-module: `application/`, `core/`, `web/`, `infra/`, plus `buildSrc/` for convention plugins |
| ORM | **Micronaut Data JPA** + Hibernate 6.x | Repository abstraction over JPA, compile-time query checking |
| Database | **Postgres 18** | Run locally via `docker compose up`; same image (`postgres:18-alpine`) used by Testcontainers |
| Migrations | **Flyway** | SQL migrations under `application/src/main/resources/db/migration/` |
| Validation | **Jakarta Bean Validation** (via `micronaut-validation`) | Controller request validation |
| HTTP client | **Micronaut HTTP Client** (declarative `@Client`) | Lightweight client for calling ITAD, Steam, Nintendo APIs |
| Scheduling | **Micronaut `@Scheduled`** | Periodic price refresh jobs (every 6 hours for favorited games) |
| Config | **Micronaut config** (`application.yml`) | API keys via environment variables, never committed |
| Tests | **JUnit 5** + **MockK** + **Kotest assertions** + **Testcontainers Postgres** | Unit tests in `core/`; integration tests in `application/` |

**Backend API routes** (unchanged from the original product spec):

| Route | Description |
|---|---|
| `GET /api/search?q={title}&country={CC}` | Search games by title, returns results with best price per game |
| `GET /api/game/:id/prices?country={CC}` | Full price breakdown for one game across all stores |
| `GET /api/game/:id/info` | Game metadata (artwork, tags, release date) |
| `GET /api/favorites/prices?country={CC}` | Bulk price fetch for all favorited game IDs |
| `GET /api/countries` | List of supported country codes and currency symbols |

The backend never forwards raw third-party API keys to the client. All external API calls are server-side only.
```

- [ ] **Step 4: Replace §2.4 "Database Schema"**

Locate `### 2.4 Database Schema (SQLite via Prisma)`. Replace the heading and the entire Prisma model block plus the cache-TTL paragraph with:

```markdown
### 2.4 Database Schema (Postgres via Micronaut Data JPA)

The schema is owned by Flyway under `application/src/main/resources/db/migration/`. Concrete JPA entities (`Game`, `Price`, `Favorite`) and their migrations land in Phase 1 — only a placeholder migration ships with the refactor pass.

Sketch of the planned schema (Phase 1):

```sql
CREATE TABLE game (
  id           TEXT PRIMARY KEY,         -- ITAD UUID
  slug         TEXT NOT NULL,
  title        TEXT NOT NULL,
  type         TEXT NOT NULL,            -- 'game' | 'dlc'
  boxart       TEXT,
  banner_300   TEXT,
  steam_appid  INTEGER,
  nintendo_id  TEXT,
  tags         JSONB,
  updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE price (
  id           BIGSERIAL PRIMARY KEY,
  game_id      TEXT NOT NULL REFERENCES game(id) ON DELETE CASCADE,
  shop         TEXT NOT NULL,
  country      TEXT NOT NULL,            -- ISO 3166-1 alpha-2
  amount       NUMERIC(10,2) NOT NULL,
  currency     TEXT NOT NULL,
  regular_amt  NUMERIC(10,2) NOT NULL,
  cut_pct      INTEGER NOT NULL,
  history_low  NUMERIC(10,2),
  store_url    TEXT,
  fetched_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (game_id, shop, country)
);
CREATE INDEX price_game_country_idx ON price (game_id, country);
```

**Cache TTL strategy:** Each `price` row carries a `fetched_at` timestamp. The backend service layer checks this before calling external APIs: if `fetched_at` is older than 6 hours, it re-fetches; otherwise it returns the cached row. No Redis layer is needed.
```

- [ ] **Step 5: Replace §2.5 "Deployment"**

Locate `### 2.5 Deployment (Personal VPS or Local)`. Replace its body with:

```markdown
### 2.5 Deployment (Personal VPS or Local)

```bash
# Local development
cd game-price-tracker-be
cp .env.example .env                # ITAD_API_KEY, DATABASE_URL, etc.
docker compose up -d                # Postgres 18 on :5432
./gradlew :application:run          # Micronaut on :8080

# Run tests
./gradlew test                      # unit + Testcontainers integration

# Build a fat JAR for deployment
./gradlew :application:shadowJar
```

For VPS deployment, the JVM-based deployment story (systemd unit + reverse proxy) is finalised in Phase 3. The Expo web build is served as static files from the reverse proxy, alongside the JVM service.
```

- [ ] **Step 6: Sanity-check the spec edits**

```bash
grep -n "Fastify\|Prisma\|SQLite\|node-cron\|TanStack" docs/superpowers/specs/gamepricetracker-spec.md | grep -v "^[0-9]*:#" || echo "No legacy stack references in non-heading lines."
```
Expected: only matches outside §2 (e.g. references to "TanStack Query" in §2.2 frontend, which is intentional). If any §2.x line still mentions Fastify, Prisma, SQLite, or node-cron, fix it before committing.

- [ ] **Step 7: Commit**

```bash
git add docs/superpowers/specs/gamepricetracker-spec.md
git commit -m "docs: update master spec §2 for Kotlin/Micronaut/Postgres backend"
```

---

## Task 17: Final acceptance verification

- [ ] **Step 1: Clean build + test from scratch**

```bash
cd game-price-tracker-be && ./gradlew --stop && ./gradlew clean build
```
Expected: BUILD SUCCESSFUL. Final report shows: 4 tests in `core`, 2 tests in `application`, 0 failures.

- [ ] **Step 2: Verify the directory tree matches the spec**

```bash
find game-price-tracker-be -maxdepth 3 -type d -not -path '*/.gradle*' -not -path '*/build*' | sort
```
Expected directories include: `application/src/{main,test}/{kotlin,resources}`, `core/src/{main,test}/kotlin`, `web/src/main/kotlin`, `infra/src/main/kotlin`, `buildSrc/src/main/kotlin`, `gradle/wrapper`.

- [ ] **Step 3: Verify the master spec reflects the new stack**

```bash
grep -c "Kotlin 2.3.21\|Micronaut 5\|Postgres 18\|Java 21 LTS" docs/superpowers/specs/gamepricetracker-spec.md
```
Expected: `>= 4`.

- [ ] **Step 4: Verify legacy BE plans are archived, FE plans untouched**

```bash
ls docs/superpowers/plans/archive/ | grep -c "game-price-tracker-be-phase"
ls docs/superpowers/plans/ | grep -c "game-price-tracker-fe-phase"
ls docs/superpowers/plans/ | grep -c "game-price-tracker-be-phase" || echo "0 (expected)"
```
Expected: `7`, `7`, `0`.

- [ ] **Step 5: Verify no leftover Node.js artefacts at the BE root**

```bash
test ! -f game-price-tracker-be/package.json && echo "no package.json OK"
test ! -d game-price-tracker-be/node_modules && echo "no node_modules OK"
test ! -f game-price-tracker-be/vitest.config.ts && echo "no vitest.config OK"
```
Expected: three `OK` lines.

- [ ] **Step 6: No commit (verification only)**

If all checks pass, the refactor is complete. Push to remote with `git push` only when explicitly asked.

---

## Done

At this point:
- The backend is a Kotlin + Micronaut + Gradle multi-module project.
- `Currency` and `GamePrice` are ported with 4 passing tests.
- The Micronaut context boots against Postgres 18 (local compose) and Testcontainers Postgres (in tests).
- Flyway is wired and the placeholder `V1__init.sql` is applied.
- The master product spec reflects the new stack.
- Legacy Node.js plans live under `archive/` — `git log --follow` still reaches them.

**Next phase plans** (out of scope for this implementation):
- Phase 1: ITAD + Steam clients, real JPA entities (`Game`, `Price`), `GET /api/search`, `GET /api/game/:id/prices`, 6h TTL caching.
- Phase 2: Nintendo eShop client, favorites endpoints, price history.
- Phase 3: VPS deployment (systemd + reverse proxy).
