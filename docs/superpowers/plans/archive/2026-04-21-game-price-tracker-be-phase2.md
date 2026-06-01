# Phase 2: Persistence Adapters (Repository Implementation)

**File**: `docs/superpowers/plans/2026-04-21-game-price-tracker-be-phase2.md`

## Overview

Implement concrete `Repository` classes for `Game`, `Price`, and `DataFeed` entities that satisfy the `IRepository<T>` interface defined in Phase 1. Pure TypeScript with **in-memory storage only** — no database, no persistence layer, no caching.

## Current Status

Phase 0 (Core Architecture) and Phase 1 (Domain Ports) plans completed. Phase 2 plan creation failed due to agent client error. Re-creating from scratch.

## Architecture

### Storage

```typescript
interface IRepository<T> {
  save(item: T): Promise<void>;
  findById(id: string): Promise<T | null>;
  findAll(): Promise<T[]>;
  delete(id: string): Promise<void>;
}
```

**In-Memory Data Store:**
```typescript
interface InMemoryStore<T> {
  _data: Map<string, T>;
  save(item: T): void;
  findById(id: string): T | null;
  findAll(): T[];
  delete(id: string): boolean;
  clear(): void;
}
```

**Repository Implementation:**
```typescript
interface IGameRepository extends IRepository<Game> {
  // Additional game-specific methods
}

interface IPriceRepository extends IRepository<Price> {
  // Additional price-specific methods
}

interface IDataFeedRepository extends IRepository<DataFeed> {
  // Additional data feed-specific methods
}
```

### Files

| File | Purpose |
|------|---------|
| `src/repositories/gameRepository.ts` | `InMemoryGameRepository` implementation |
| `src/repositories/priceRepository.ts` | `InMemoryPriceRepository` implementation |
| `src/repositories/dataFeedRepository.ts` | `InMemoryDataFeedRepository` implementation |
| `src/repositories/index.ts` | Barrel file |

## Repository Implementations

### Game Repository

```typescript
class InMemoryGameRepository implements IGameRepository {
  private readonly _store = new InMemoryStore<Game>();

  async save(game: Game): Promise<void> {
    await Promise.resolve();
    this._store.save(game);
  }

  async findById(id: string): Promise<Game | null> {
    return await Promise.resolve(this._store.findById(id));
  }

  async findAll(): Promise<Game[]> {
    return await Promise.resolve([...this._store.findAll()]);
  }

  async delete(id: string): Promise<void> {
    await Promise.resolve();
    this._store.delete(id);
  }
}
```

### Price Repository

```typescript
class InMemoryPriceRepository implements IPriceRepository {
  private readonly _store = new InMemoryStore<Price>();

  async save(price: Price): Promise<void> {
    await Promise.resolve();
    this._store.save(price);
  }

  async findById(id: string): Promise<Price | null> {
    return await Promise.resolve(this._store.findById(id));
  }

  async findAll(): Promise<Price[]> {
    return await Promise.resolve([...this._store.findAll()]);
  }

  async delete(id: string): Promise<void> {
    await Promise.resolve();
    this._store.delete(id);
  }
}
```

### DataFeed Repository

```typescript
class InMemoryDataFeedRepository implements IDataFeedRepository {
  private readonly _store = new InMemoryStore<DataFeed>();

  async save(feed: DataFeed): Promise<void> {
    await Promise.resolve();
    this._store.save(feed);
  }

  async findById(id: string): Promise<DataFeed | null> {
    return await Promise.resolve(this._store.findById(id));
  }

  async findAll(): Promise<DataFeed[]> {
    return await Promise.resolve([...this._store.findAll()]);
  }

  async delete(id: string): Promise<void> {
    await Promise.resolve();
    this._store.delete(id);
  }
}
```

## Key Decisions

- **Pure in-memory**: No database, no persistence. Storage resets when process restarts.
- **Synchronous operations**: All methods use `Promise.resolve()` to satisfy async interface.
- **No caching**: Caching explicitly excluded per user request.
- **No validation**: Repository is a dumb data access layer. Validation belongs in domain services.
- **No error handling**: Failures from underlying storage are passed through.

## Test Files

**Location**: `src/repositories/`

| File | Coverage |
|------|----------|
| `gameRepository.spec.ts` | 4 test cases |
| `priceRepository.spec.ts` | 5 test cases |
| `dataFeedRepository.spec.ts` | 3 test cases |

### Test Structure

```typescript
describe('InMemoryGameRepository', () => {
  it('should save and retrieve a game', async () => {
    const repo = new InMemoryGameRepository();
    const game = Game.create('test', 'Pokeémon Scarlet', 59.99, 4.8, 'VGC', '2026-04-15');
    await repo.save(game);
    const found = await repo.findById('test');
    expect(found?.name).toBe('Pokeémon Scarlet');
  });

  it('should return null for non-existent game', async () => {
    const repo = new InMemoryGameRepository();
    const found = await repo.findById('nonexistent');
    expect(found).toBeNull();
  });

  it('should delete a game', async () => {
    const repo = new InMemoryGameRepository();
    await repo.save(Game.create('to-delete', 'Old Game', 19.99, 4.0, 'Standard', '2026-01-01'));
    await repo.delete('to-delete');
    const found = await repo.findById('to-delete');
    expect(found).toBeNull();
  });

  it('should return all games', async () => {
    const repo = new InMemoryGameRepository();
    const game1 = Game.create('game-1', 'Game One', 29.99, 4.5, 'Standard', '2026-02-01');
    const game2 = Game.create('game-2', 'Game Two', 39.99, 4.7, 'Standard', '2026-02-15');
    await repo.save(game1);
    await repo.save(game2);
    const all = await repo.findAll();
    expect(all).toHaveLength(2);
  });
});
```

## Implementation Steps

1. Create `src/repositories/gameRepository.ts` with `InMemoryGameRepository`
2. Create `src/repositories/priceRepository.ts` with `InMemoryPriceRepository`
3. Create `src/repositories/dataFeedRepository.ts` with `InMemoryDataFeedRepository`
4. Create `src/repositories/index.ts` barrel file
5. Create `src/repositories/gameRepository.spec.ts`
6. Create `src/repositories/priceRepository.spec.ts`
7. Create `src/repositories/dataFeedRepository.spec.ts`
8. Run tests with `npx vitest run src/repositories/`

## Test Commands

```bash
# Run all repository tests
npx vitest run src/repositories/

# Run specific test file
npx vitest run src/repositories/gameRepository.spec.ts

# Run with coverage
npx vitest run --coverage src/repositories/
```

## Verification

Tests pass: `npx vitest run src/repositories/`

## What This Provides

- **Repository interface**: Concrete implementations satisfying `IRepository<T>` from Phase 1
- **In-memory storage**: Simple, fast, process-scoped data storage
- **Test foundation**: 12 test cases covering CRUD operations
- **Async compatibility**: Methods are `async` for future database migration

## What This Does NOT Provide

- **Persistence**: Data is lost when process restarts
- **Caching**: No caching layer
- **Validation**: Repository is dumb data access
- **Error handling**: Failures passed through from underlying storage

---

**Generated**: 2026-04-21
**Phase**: 2 of 7 (GamePriceTracker BE Implementation Plan)
**Approach**: TypeScript + Vitest, in-memory storage only