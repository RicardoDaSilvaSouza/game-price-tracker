# Phase 5: Presentation/API Adapter (Controller)

**File**: `docs/superpowers/plans/2026-04-21-game-price-tracker-be-phase5.md`

## Overview

Implement `Controller` classes that orchestrate data flow between users and the application. Pure TypeScript with **no database access** — all data access goes through repository interfaces.

## Current Status

- Phase 4 (Gateway) completed — external API integration implemented
- Phase 5 plan creation failed due to agent client error
- **Recreating from scratch** based on existing Phase 0/1/2/3/4 foundations

## Architecture

### Controller Pattern

```typescript
interface IController {
  // Request handling methods
}

interface IGameController {
  getGameDetails(gameId: string): Promise<GameResponse>;
  getGamePrices(gameId: string): Promise<PriceResponse>;
  addReview(gameId: string, review: Review): Promise<void>;
}

interface IHomeController {
  getAllGames(): Promise<GameListResponse>;
  searchGames(query: string): Promise<SearchResponse>;
}
```

### Files

| File | Purpose |
|------|---------|
| `src/controllers/gameController.ts` | Game CRUD, price updates, reviews |
| `src/controllers/homeController.ts` | Game listing, search, discovery |
| `src/controllers/index.ts` | Barrel file |

### Controller Implementation

```typescript
class GameController implements IGameController {
  private readonly _gameRepository: IGameRepository;
  private readonly _priceRepository: IPriceRepository;
  private readonly _priceService: IPriceService;

  constructor(
    gameRepository: IGameRepository,
    priceRepository: IPriceRepository,
    priceService: IPriceService
  ) {
    this._gameRepository = gameRepository;
    this._priceRepository = priceRepository;
    this._priceService = priceService;
  }

  async getGameDetails(gameId: string): Promise<GameResponse> {
    // 1. Fetch game from repository
    // 2. Fetch latest prices from repository
    // 3. Combine into response
    // 4. Handle not found
  }

  async getGamePrices(gameId: string): Promise<PriceResponse> {
    // 1. Fetch game to get name
    // 2. Fetch prices from repository
    // 3. Transform to PriceResponse
  }

  async addReview(gameId: string, review: Review): Promise<void> {
    // 1. Validate review
    // 2. Save review to repository
    // 3. Update game rating average
  }
}
```

## Key Decisions

- **Controller composition**: Controllers depend on repositories, not concrete implementations
- **Response transformation**: Controllers format data for consumers (frontend/API)
- **Error handling**: Controllers handle NotFound, BadRequest, and ServerError cases
- **No caching**: Controllers are thin wrappers around services and repositories

## Test Files

**Location**: `src/controllers/`

| File | Coverage |
|------|----------|
| `gameController.spec.ts` | 5 test cases |
| `homeController.spec.ts` | 3 test cases |

### Test Structure

```typescript
describe('GameController', () => {
  it('should return game details with prices', async () => {
    const controller = new GameController(gameRepo, priceRepo, priceService);
    const response = await controller.getGameDetails('test');
    expect(response).toHaveProperty('name');
    expect(response).toHaveProperty('currentPrice');
  });

  it('should handle non-existent game', async () => {
    const controller = new GameController(gameRepo, priceRepo, priceService);
    await expect(controller.getGameDetails('nonexistent')).rejects.toThrow();
  });
});
```

## Implementation Steps

1. Create `src/controllers/gameController.ts` with `GameController` class
2. Create `src/controllers/homeController.ts` with `HomeController` class
3. Create `src/controllers/index.ts` barrel file
4. Create `src/controllers/gameController.spec.ts`
5. Create `src/controllers/homeController.spec.ts`
6. Run tests with `npx vitest run src/controllers/`

## Test Commands

```bash
# Run all controller tests
npx vitest run src/controllers/

# Run specific test file
npx vitest run src/controllers/gameController.spec.ts

# Run with coverage
npx vitest run --coverage src/controllers/
```

## Verification

Tests pass: `npx vitest run src/controllers/`

## What This Provides

- **Presentation layer**: Controllers that handle user requests
- **Request orchestration**: Coordinates repositories and services
- **Error handling**: Proper HTTP status codes and error messages
- **Response formatting**: Consistent response structure for consumers

## What This Does NOT Provide

- **Database access**: No direct database calls
- **API server**: No Express/Fastify server setup
- **Authentication**: No user session management
- **Caching**: No response caching

---

**Generated**: 2026-04-21
**Phase**: 5 of 7 (GamePriceTracker BE Implementation Plan)
**Approach**: TypeScript + Vitest, controller pattern with composition