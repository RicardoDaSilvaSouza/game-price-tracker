# GamePriceTracker Backend: Phase 0 Implementation Plan (Foundation & Ports)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** To establish the core architectural contracts for the GamePriceTracker Backend, adhering strictly to Domain-Driven Design (DDD) and Hexagonal Architecture principles, using TypeScript/Node.js conventions.

**Architecture:** The backend will be structured around a clean separation of concerns: Domain, Ports (Interfaces), Application Services (Use Cases), and Infrastructure (Adapters). This ensures that the core business logic (Domain) is independent of external concerns like databases or HTTP protocols.

**Tech Stack:** TypeScript / Node.js

---

### Task 1: Define Core Project Structure and Initial Contracts (Ports)

**Files:**
- Create: `src/domain/`
- Create: `src/ports/`
- Create: `src/application/`
- Create: `src/infrastructure/`
- Create: `tests/`

- [ ] **Step 1: Write the failing test for project structure setup**

```typescript
// In tests/structure.test.ts
import * as fs from 'fs';
import * as path from 'path';

describe('Project Structure Check', () => {
  it('should contain the core architectural directories', () => {
    expect(fs.existsSync(path.join(__dirname, '../src/domain'))).toBe(true);
    expect(fs.existsSync(path.join(__dirname, '../src/ports'))).toBe(true);
    expect(fs.existsSync(path.join(__dirname, '../src/application'))).toBe(true);
    expect(fs.existsSync(path.join(__dirname, '../src/infrastructure'))).toBe(true);
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/structure.test.ts`
Expected: FAIL with "ENOENT" errors for the directory assertions.

- [ ] **Step 3: Write minimal implementation (Directory Creation)**

```bash
# In setup script or initial migration
mkdir -p src/domain src/ports src/application src/infrastructure tests
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/structure.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/domain src/ports src/application src/infrastructure tests/structure.test.ts
git commit -m "feat: initialize project structure for hexagonal architecture"
```

### Task 2: Define Core Domain Value Object (Currency)

**Files:**
- Create: `src/domain/value_object/currency.ts`
- Test: `tests/domain.test.ts`

- [ ] **Step 1: Write the failing test for Currency validation**

```typescript
// In tests/domain.test.ts
import { Currency } from '../../src/domain/value_object/currency';

describe('Currency VO', () => {
  it('should throw an error if amount is not positive', () => {
    // Test case to check that a Currency object cannot be initialized with a zero or negative amount
    expect(() => new Currency(0, "USD")).toThrow('Amount must be positive.');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/domain.test.ts`
Expected: FAIL with the expected validation error.

- [ ] **Step 3: Write minimal implementation (Value Object)**

```typescript
// In src/domain/value_object/currency.ts
export class Currency {
    constructor(public amount: number, public code: string) {
        if (amount <= 0) {
            throw new Error('Amount must be positive.');
        }
    }

    isValid(): boolean {
        return this.amount > 0;
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/domain.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/domain/value_object/currency.ts tests/domain.test.ts
git commit -m "feat: introduce Currency Value Object adhering to domain integrity"
```

### Task 3: Define Primary Domain Entity (GamePrice)

**Files:**
- Create: `src/domain/entity/game_price.ts`
- Test: `tests/domain.test.ts`

- [ ] **Step 1: Write the failing test for GamePrice creation**

```typescript
// In tests/domain.test.ts
import { Currency } from '../../src/domain/value_object/currency';
import { GamePrice } from '../../src/domain/entity/game_price';

describe('GamePrice Entity', () => {
  it('should throw an error if created with an invalid Currency object', () => {
    // Assuming we can instantiate an invalid Currency for testing purposes
    const invalidCurrency = new Currency(0, "USD"); 
    expect(() => new GamePrice("Test Game", invalidCurrency)).toThrow();
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/domain.test.ts`
Expected: FAIL if the constructor validation logic is not yet present or is incomplete.

- [ ] **Step 3: Write minimal implementation (Entity)**

```typescript
// In src/domain/entity/game_price.ts
import { Currency } from '../value_object/currency';

export class GamePrice {
    constructor(public name: string, public currency: Currency) {
        if (!this.currency.isValid()) {
            throw new Error('GamePrice requires a valid Currency object.');
        }
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/domain.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/domain/entity/game_price.ts tests/domain.test.ts
git commit -m "feat: implement GamePrice entity with required Currency VO"
```

***

**Plan complete and saved to `docs/superpowers/plans/2026-04-21-game-price-tracker-be-phase0.md`. Two execution options:**

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**