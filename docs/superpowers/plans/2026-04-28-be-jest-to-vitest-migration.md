# BE Jest-to-Vitest Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace Jest with Vitest in the `game-price-tracker-be/` project, preserving existing test files via Vitest's globals mode.

**Architecture:** Drop jest/ts-jest dependencies, install vitest + coverage-v8, replace `jest.config.js` with `vitest.config.ts`, add `tsconfig.json`, rewrite test files to import `describe`/`it`/`expect` explicitly from `vitest`.

**Tech Stack:** pnpm, TypeScript, Vitest, @vitest/coverage-v8

**Reference Spec:** `docs/superpowers/specs/2026-04-28-be-jest-to-vitest-migration-design.md`

---

## Pre-Flight

All work happens in `/Users/ricardodasilvasouza/workspace/personal/game-price-tracker/game-price-tracker-be/`. Run all `pnpm` commands from that directory.

Existing test files (do NOT modify):
- `tests/structure.test.ts`
- `tests/domain/game_price.test.ts`
- `tests/domain/currency.test.ts`

---

### Task 1: Remove Jest dependencies

**Files:**
- Modify: `game-price-tracker-be/package.json`

- [ ] **Step 1: Uninstall jest, ts-jest, ts-node, @types/jest**

Run from `game-price-tracker-be/`:

```bash
pnpm remove jest ts-jest ts-node @types/jest
```

Expected: pnpm reports the packages removed. `package.json` no longer lists them.

- [ ] **Step 2: Verify package.json state**

After removal, `package.json` should have:
- No `jest`, `ts-jest`, `ts-node` in `dependencies`
- No `@types/jest` in `devDependencies`
- `@types/node` still present in `devDependencies`

- [ ] **Step 3: Commit**

```bash
git add game-price-tracker-be/package.json game-price-tracker-be/pnpm-lock.yaml
git commit -m "chore(be): remove jest and ts-jest dependencies"
```

Note: if `pnpm-lock.yaml` is gitignored at the workspace level, only commit `package.json`. Verify with `git status` first.

---

### Task 2: Install Vitest and coverage provider

**Files:**
- Modify: `game-price-tracker-be/package.json`

- [ ] **Step 1: Install vitest, @vitest/coverage-v8, typescript, ts-node**

Run from `game-price-tracker-be/`:

```bash
pnpm add -D vitest @vitest/coverage-v8 typescript ts-node
```

Expected: pnpm installs all four packages as devDependencies. `package.json` lists them under `devDependencies`.

- [ ] **Step 2: Verify package.json state**

`devDependencies` should now include:
- `@types/node`
- `@vitest/coverage-v8`
- `ts-node`
- `typescript`
- `vitest`

`dependencies` should be empty (or removed entirely).

- [ ] **Step 3: Commit**

```bash
git add game-price-tracker-be/package.json
git commit -m "chore(be): install vitest and coverage-v8"
```

(Add `pnpm-lock.yaml` only if not gitignored.)

---

### Task 3: Update test scripts in package.json

**Files:**
- Modify: `game-price-tracker-be/package.json`

- [ ] **Step 1: Replace the scripts block**

Open `game-price-tracker-be/package.json`. Replace:

```json
"scripts": {
  "test": "NODE_OPTIONS=--no-experimental-webstorage jest"
},
```

With:

```json
"scripts": {
  "test": "vitest run",
  "test:watch": "vitest",
  "test:coverage": "vitest run --coverage"
},
```

- [ ] **Step 2: Commit**

```bash
git add game-price-tracker-be/package.json
git commit -m "chore(be): switch test scripts to vitest"
```

---

### Task 4: Create vitest.config.ts

**Files:**
- Create: `game-price-tracker-be/vitest.config.ts`

- [ ] **Step 1: Write vitest.config.ts**

Create `game-price-tracker-be/vitest.config.ts` with this exact content:

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

`globals` is NOT enabled — test files will import test helpers from `vitest` explicitly.

- [ ] **Step 2: Commit**

```bash
git add game-price-tracker-be/vitest.config.ts
git commit -m "chore(be): add vitest config with globals and v8 coverage"
```

---

### Task 5: Create tsconfig.json

**Files:**
- Create: `game-price-tracker-be/tsconfig.json`

- [ ] **Step 1: Write tsconfig.json**

Create `game-price-tracker-be/tsconfig.json` with this exact content:

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

Test helpers (`describe`, `it`, `expect`) are imported explicitly from `vitest` in each test file (Task 6.5), so no `vitest/globals` types entry is needed.

- [ ] **Step 2: Commit**

```bash
git add game-price-tracker-be/tsconfig.json
git commit -m "chore(be): add tsconfig with vitest globals types"
```

---

### Task 6: Delete jest.config.js

**Files:**
- Delete: `game-price-tracker-be/jest.config.js`

- [ ] **Step 1: Remove the file**

Run from repo root:

```bash
rm game-price-tracker-be/jest.config.js
```

- [ ] **Step 2: Commit**

```bash
git add -A game-price-tracker-be/jest.config.js
git commit -m "chore(be): remove jest config"
```

---

### Task 6.5: Add explicit Vitest imports to test files

**Files:**
- Modify: `game-price-tracker-be/tests/structure.test.ts`
- Modify: `game-price-tracker-be/tests/domain/game_price.test.ts`
- Modify: `game-price-tracker-be/tests/domain/currency.test.ts`

- [ ] **Step 1: Add import to `tests/structure.test.ts`**

Insert this line at the very top of the file (before the existing `import * as fs ...`):

```typescript
import { describe, it, expect } from 'vitest';
```

- [ ] **Step 2: Add import to `tests/domain/game_price.test.ts`**

Insert at the very top (before existing imports):

```typescript
import { describe, it, expect } from 'vitest';
```

- [ ] **Step 3: Add import to `tests/domain/currency.test.ts`**

Insert at the very top (before existing imports):

```typescript
import { describe, it, expect } from 'vitest';
```

- [ ] **Step 4: Commit**

```bash
git add game-price-tracker-be/tests/
git commit -m "refactor(be): use explicit vitest imports in test files"
```

---

### Task 7: Verify migration end-to-end

**Files:** None modified — this is verification.

- [ ] **Step 1: Run tests**

From `game-price-tracker-be/`:

```bash
pnpm test
```

Expected: Vitest runs all 5 tests across 3 files. All pass. No TypeScript errors about missing `describe`/`it`/`expect`.

- [ ] **Step 2: Run watch mode briefly**

```bash
pnpm test:watch
```

Expected: vitest enters watch mode showing "Waiting for file changes". Press `q` (or Ctrl+C) to exit. Confirm watch mode starts cleanly with no errors.

- [ ] **Step 3: Run coverage**

```bash
pnpm test:coverage
```

Expected: All tests pass and a coverage table prints to the terminal. A `coverage/` directory is generated.

- [ ] **Step 4: Confirm coverage/ is gitignored**

Run:

```bash
git status game-price-tracker-be/coverage/
```

Expected: empty output (gitignored). The repo `.gitignore` already lists `coverage/`. No commit needed.

If `coverage/` shows as untracked, add it to `.gitignore` first and commit that change.

---

### Task 8: Index project with jcodemunch

**Files:** None.

- [ ] **Step 1: Index the project**

Use the `mcp__jcodemunch__index_repo` (or `index_folder`) tool, pointing at `/Users/ricardodasilvasouza/workspace/personal/game-price-tracker`.

Expected: Indexing completes successfully and the project becomes searchable via jcodemunch tools.

---

## Done

After Task 8, the migration is complete:
- Vitest is the active test runner
- All 5 tests pass
- Coverage and watch mode work
- Project is indexed in jcodemunch
