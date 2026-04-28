# BE Project: Migrate from Jest to Vitest

**Date**: 2026-04-28
**Scope**: `game-price-tracker-be/` only

## Goal

Replace Jest with Vitest as the test runner for the backend project. Vitest is faster, Vite-native, and integrates better with a modern pnpm-based stack. Adopt Vitest's idiomatic style: each test file imports `describe`, `it`, `expect` explicitly from `vitest`.

## Current State

- 3 test files under `game-price-tracker-be/tests/` (`*.test.ts`)
- Tests currently use jest globals (`describe`, `it`, `expect`) without imports — these will be rewritten to use explicit imports from `vitest`
- `jest.config.js` uses `ts-jest` preset with `--no-experimental-webstorage` workaround
- `package.json` has `jest`, `ts-jest`, `ts-node` in `dependencies` and `@types/jest`, `@types/node` in `devDependencies`
- No `tsconfig.json` exists yet

## Target State

- `vitest` as test runner with `@vitest/coverage-v8` for coverage
- Test files updated to import `describe`/`it`/`expect` from `vitest` explicitly
- Three scripts: `test`, `test:watch`, `test:coverage`
- `tsconfig.json` set up for TypeScript without globals types
- `jest.config.js` removed

## Changes

### 1. `package.json`

**Remove from `dependencies`:**
- `jest`
- `ts-jest`
- `ts-node`

**Remove from `devDependencies`:**
- `@types/jest`

**Add to `devDependencies`:**
- `vitest`
- `@vitest/coverage-v8`
- `ts-node` (moved from `dependencies` — dev-only tool)
- `typescript` (needed for tsconfig.json)

**Keep in `devDependencies`:**
- `@types/node`

**Replace `scripts`:**

```json
"scripts": {
  "test": "vitest run",
  "test:watch": "vitest",
  "test:coverage": "vitest run --coverage"
}
```

The `NODE_OPTIONS=--no-experimental-webstorage` flag is dropped — that workaround was jest-specific.

### 2. Create `vitest.config.ts`

```typescript
import { defineConfig } from 'vitest/config'

export default defineConfig({
  test: {
    include: ['**/*.test.ts'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'html'],
    },
  },
})
```

`globals` is intentionally NOT enabled — tests will import from `vitest` explicitly.

### 3. Create `tsconfig.json`

```json
{
  "compilerOptions": {
    "target": "ES2022",
    "module": "commonjs",
    "strict": true,
    "esModuleInterop": true,
    "skipLibCheck": true,
    "forceConsistentCasingInFileNames": true,
    "types": ["node"]
  },
  "include": ["src/**/*", "tests/**/*", "vitest.config.ts"]
}
```

### 4. Delete `jest.config.js`

### 5. Rewrite test files with explicit Vitest imports

Each of the 3 test files gets a top-line import:

```typescript
import { describe, it, expect } from 'vitest'
```

Files affected:
- `tests/structure.test.ts`
- `tests/domain/game_price.test.ts`
- `tests/domain/currency.test.ts`

No assertion or test logic changes — just the import.

## Verification

After migration:

1. `pnpm install` — installs new dev dependencies
2. `pnpm test` — runs all 5 tests, all should pass
3. `pnpm test:watch` — enters watch mode, reruns on file changes
4. `pnpm test:coverage` — generates coverage report under `coverage/`

## Out of Scope

- Frontend project changes
- CI/CD configuration
- Adding new tests
- Refactoring existing test code

## Post-Migration Step

Index the project with jcodemunch once verification passes.
