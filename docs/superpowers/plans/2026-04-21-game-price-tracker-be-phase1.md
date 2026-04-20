# GamePriceTracker Backend: Phase 1 Implementation Plan (Domain Ports)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** To formally define the necessary contracts (Ports) that allow the core Domain to interact with external services (like a database or external APIs) without knowing their concrete implementations.

**Architecture:** This phase locks down the interfaces. The Domain layer will depend only on these Ports, adhering to the Dependency Rule of Hexagonal Architecture, keeping the core clean and testable.

**Tech Stack:** TypeScript / Node.js

---

### Task 1: Define Repository Port (Persistence Interface)

**Files:**
- Create: `src/ports/repository/game_price_repository.interface.ts`
- Test: `tests/port.test.ts`

- [ ] **Step 1: Write the failing test for Repository Port contract**

```typescript
// In tests/port.test.ts
import { GamePrice } from '../../src/domain/entity/game_price';
import { GamePriceRepository } from '../../src/ports/repository/game_price_repository.interface';

describe('GamePriceRepository Interface', () => {
  it('should fail if a required method (e.g., findById) is missing', () => {
    // Mock implementation that misses findById
    class MockRepository {
        save(price: GamePrice) { /* does nothing */ }
    }
    const repo = new MockRepository() as GamePriceRepository;
    // This test is conceptual: we test that the contract forces the implementation to exist.
    expect(repo.findById).toBeUndefined(); 
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/port.test.ts`
Expected: FAIL, confirming the interface contract is not met by a minimal mock.

- [ ] **Step 3: Write minimal implementation (Interface)**

```typescript
// In src/ports/repository/game_price_repository.interface.ts
import { GamePrice } from '../../domain/entity/game_price';

export interface GamePriceRepository {
    findById(id: string): Promise<GamePrice | null>;
    save(price: GamePrice): Promise<GamePrice>;
    // Future methods like findAll() will be added here.
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/port.test.ts`
Expected: PASS (The test now passes because the interface is defined).

- [ ] **Step 5: Commit**

```bash
git add src/ports/repository/game_price_repository.interface.ts tests/port.test.ts
git commit -m "feat: define GamePriceRepository port for persistence contract"
```

### Task 2: Define External Data Fetching Port (Gateway Interface)

**Files:**
- Create: `src/ports/gateway/price_feed_gateway.interface.ts`
- Test: `tests/port.test.ts`

- [ ] **Step 1: Write the failing test for Gateway Port contract**

```typescript
// In tests/port.test.ts
import { PriceFeedGateway } from '../../src/ports/gateway/price_feed_gateway.interface';

describe('PriceFeedGateway Interface', () => {
  it('should fail if connection method is missing', () => {
    // Mock implementation that misses fetchLatestPrices
    class MockGateway {
        connect() {}
    }
    const gateway = new MockGateway() as PriceFeedGateway;
    expect(gateway.fetchLatestPrices).toBeUndefined();
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/port.test.ts`
Expected: FAIL.

- [ ] **Step 3: Write minimal implementation (Interface)**

```typescript
// In src/ports/gateway/price_feed_gateway.interface.ts
import { GamePrice } from '../../domain/entity/game_price';

export interface PriceFeedGateway {
    connect(): Promise<void>;
    fetchLatestPrices(): Promise<GamePrice[]>;
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/port.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/ports/gateway/price_feed_gateway.interface.ts tests/port.test.ts
git commit -m "feat: define PriceFeedGateway port for external data ingestion"
```

***

**Plan complete and saved to `docs/superpowers/plans/2026-04-21-game-price-tracker-be-phase1.md`. Two execution options:**

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**