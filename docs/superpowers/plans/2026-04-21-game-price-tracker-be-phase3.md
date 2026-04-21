# Phase 3: Application Services (Business Logic)

**File**: `docs/superpowers/plans/2026-04-21-game-price-tracker-be-phase3.md`

## Overview

Implement concrete service classes that contain business logic, coordinate repositories, and manage application workflows. Pure TypeScript with **no database access** — all data access goes through repository interfaces.

## Current Status

- Phase 2 (Persistence Adapters) completed — in-memory repositories implemented
- Phase 3 plan creation failed due to agent client error
- **Recreating from scratch** based on existing Phase 0/Phase 1 foundations

## Architecture

### Service Layer Pattern

```typescript
interface IService {
  // Business logic methods
}

interface IGameService {
  addGame(game: Game): Promise<void>;
  updateGamePrice(gameId: string, price: number): Promise<void>;
  addReview(gameId: string, review: Review): Promise<void>;
}

interface IPriceService {
  fetchAndStorePrices(gameId: string, prices: PriceData[]): Promise<void>;
  processPriceUpdate(gameId: string, price: number, timestamp: string): Promise<void>;
}

interface IDataFeedService {
  addDataFeed(dataFeed: DataFeed): Promise<void>;
  updateDataFeedStatus(dataFeedId: string, status: FeedStatus): Promise<void>;
}
```

### Files

| File | Purpose |
|------|---------|
| `src/services/gameService.ts` | Game CRUD + price handling + review aggregation |
| `src/services/priceService.ts` | Price fetching, caching, and updates |
| `src/services/dataFeedService.ts` | Data feed management |
| `src/services/index.ts` | Barrel file |

### Business Rules

1. **Price updates** overwrite previous values for a given game
2. **Price history** tracks historical prices (stored as array in Price entity)
3. **Data feed status** updates when prices are stale or missing
4. **Atomic writes** — transaction-like behavior (use try/catch rollback pattern)
5. **Validation** — reject invalid data before processing

## Service Implementations

### Game Service

```typescript
class GameService implements IGameService {
  private readonly _gameRepository: IGameRepository;
  private readonly _priceRepository: IPriceRepository;

  constructor(gameRepository: IGameRepository, priceRepository: IPriceRepository) {
    this._gameRepository = gameRepository;
    this._priceRepository = priceRepository;
  }

  async addGame(game: Game): Promise<void> {
    await this._gameRepository.save(game);
    // Business logic: validate game data, check for duplicates, etc.
  }

  async updateGamePrice(gameId: string, price: number): Promise<void> {
    // Business logic: fetch current game, update price, save
  }

  async addReview(gameId: string, review: Review): Promise<void> {
    // Business logic: aggregate rating, update averages
  }
}
```

### Price Service

```typescript
class PriceService implements IPriceService {
  private readonly _priceRepository: IPriceRepository;
  private readonly _cache: Map<string, Price[]>;

  constructor(priceRepository: IPriceRepository) {
    this._priceRepository = priceRepository;
    this._cache = new Map();
  }

  async fetchAndStorePrices(gameId: string, prices: PriceData[]): Promise<void> {
    // Business logic: validate, transform, store
  }

  async processPriceUpdate(gameId: string, price: number, timestamp: string): Promise<void> {
    // Business logic: compare old vs new, create history entry
  }
}
```

### Data Feed Service

```typescript
class DataFeedService implements IDataFeedService {
  private readonly _dataFeedRepository: IDataFeedRepository;

  constructor(dataFeedRepository: IDataFeedRepository) {
    this._dataFeedRepository = dataFeedRepository;
  }

  async addDataFeed(dataFeed: DataFeed): Promise<void> {
    // Business logic: validate feed, check for duplicates
  }

  async updateDataFeedStatus(dataFeedId: string, status: FeedStatus): Promise<void> {
    // Business logic: update status, timestamps, error tracking
  }
}
```

## Key Decisions

- **Pure business logic**: No database access, no persistence — only repository calls
- **Service composition**: Services depend on repositories, not concrete implementations
- **Validation layer**: Business rules enforced before data persistence
- **No caching in repository**: Services handle any caching needs
- **Atomic operations**: Try/catch patterns for rollback on failure

## Test Files

**Location**: `src/services/`

| File | Coverage |
|------|----------|
| `gameService.spec.ts` | 6 test cases |
| `priceService.spec.ts` | 4 test cases |
| `dataFeedService.spec.ts` | 4 test cases |

### Test Structure

```typescript
describe('GameService', () => {
  it('should add a game to the repository', async () => {
    const service = new GameService(gameRepo, priceRepo);
    const game = Game.create('test', 'Test Game', 19.99, 4.5, 'Standard', '2026-04-01');
    await service.addGame(game);
    const found = await gameRepo.findById('test');
    expect(found).not.toBeNull();
  });

  it('should aggregate average price from multiple updates', async () => {
    const service = new GameService(gameRepo, priceRepo);
    await service.updateGamePrice('test', 29.99);
    await service.updateGamePrice('test', 39.99);
    // Verify price history contains both values
  });
});
```

## Implementation Steps

1. Create `src/services/gameService.ts` with `GameService` class
2. Create `src/services/priceService.ts` with `PriceService` class
3. Create `src/services/dataFeedService.ts` with `DataFeedService` class
4. Create `src/services/index.ts` barrel file
5. Create `src/services/gameService.spec.ts`
6. Create `src/services/priceService.spec.ts`
7. Create `src/services/dataFeedService.spec.ts`
8. Run tests with `npx vitest run src/services/`

## Test Commands

```bash
# Run all service tests
npx vitest run src/services/

# Run specific test file
npx vitest run src/services/gameService.spec.ts

# Run with coverage
npx vitest run --coverage src/services/
```

## Verification

Tests pass: `npx vitest run src/services/`

## What This Provides

- **Business logic layer**: Concrete service classes with domain rules
- **Composition pattern**: Services depend on repository interfaces
- **Validation**: Business rules enforced before data persistence
- **Test foundation**: 14 test cases covering core service functionality

## What This Does NOT Provide

- **Database access**: No direct database calls
- **Caching**: Repository handles persistence, services handle business rules
- **Error handling**: Failures from repositories are passed through
- **Async operations**: All methods are `async` but operations are synchronous

---

**Generated**: 2026-04-21
**Phase**: 3 of 7 (GamePriceTracker BE Implementation Plan)
**Approach**: TypeScript + Vitest, repository composition pattern