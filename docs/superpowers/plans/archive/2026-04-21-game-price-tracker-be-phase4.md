# Phase 4: External API Adapters (Gateway)

**File**: `docs/superpowers/plans/2026-04-21-game-price-tracker-be-phase4.md`

## Overview

Implement `Gateway` classes that fetch real-time game and price data from external API providers (Steam, Epic, GOG, etc.). Pure TypeScript with **no database access** — all data persistence goes through repository interfaces.

## Current Status

- Phase 3 (Application Services) completed — service layer implemented
- Phase 4 plan creation failed due to agent client error
- **Recreating from scratch** based on existing Phase 0/1/2/3 foundations

## Architecture

### Gateway Pattern

```typescript
interface IGateway {
  fetchPrices(gameId: string): Promise<PriceData[]>;
  updatePrices(gameId: string, prices: PriceData[]): Promise<void>;
}

interface ISteamGateway implements IGateway {
  fetchPrices(gameId: string): Promise<PriceData[]>;
  updatePrices(gameId: string, prices: PriceData[]): Promise<void>;
}

interface IEpicGateway implements IGateway {
  fetchPrices(gameId: string): Promise<PriceData[]>;
  updatePrices(gameId: string, prices: PriceData[]): Promise<void>;
}

interface IGOGGateway implements IGateway {
  fetchPrices(gameId: string): Promise<PriceData[]>;
  updatePrices(gameId: string, prices: PriceData[]): Promise<void>;
}
```

### Files

| File | Purpose |
|------|---------|
| `src/gateways/steamGateway.ts` | Steam API integration |
| `src/gateways/epicGateway.ts` | Epic Games Store API integration |
| `src/gateways/gogGateway.ts` | GOG.com API integration |
| `src/gateways/index.ts` | Barrel file |

### Data Sources

| Source | Endpoint | Rate Limit | Refresh Interval |
|--------|----------|------------|------------------|
| Steam | `https://store.steampowered.com/api/steam_appdetails` | 10 req/min | 15 min |
| Epic | `https://www.epicgames.com/store/api/catalog/v2` | 100 req/min | 30 min |
| GOG | `https://www.gog.com/products` | 10 req/min | 60 min |

## Gateway Implementations

### Steam Gateway

```typescript
class SteamGateway implements IGateway {
  private readonly _apiKey: string;
  private readonly _baseUrl: string;
  private readonly _lastFetched: Map<string, number>;

  constructor(apiKey: string, baseUrl: string = 'https://store.steampowered.com/api/steam_appdetails') {
    this._apiKey = apiKey;
    this._baseUrl = baseUrl;
    this._lastFetched = new Map();
  }

  async fetchPrices(gameId: string): Promise<PriceData[]> {
    // HTTP: GET /api/steam_appdetails
    // Headers: {
    //   'X-Auth-Token': '<API_KEY>',
    //   'User-Agent': 'GamePriceTracker/1.0'
    // }
    // Query: { appid: gameId }

    // Response: {
    //   "success": true,
    //   "data": {
    //     "name": "Game Title",
    //     "price_overview": {
    //       "price": 5999,
    //       "discounted": 0,
    //       "original": 5999
    //     },
    //     "categories": [{"id": 1, "description": "Single Player"}]
    //   }
    // }

    // Transform: { gameId, name, price, discount, rating, source: 'steam', timestamp }
  }

  async updatePrices(gameId: string, prices: PriceData[]): Promise<void> {
    // Delegate to priceRepository
  }
}
```

### Epic Gateway

```typescript
class EpicGateway implements IGateway {
  private readonly _lastFetched: Map<string, number>;

  constructor(baseUrl: string = 'https://www.epicgames.com/store/api/catalog/v2') {
    this._lastFetched = new Map();
  }

  async fetchPrices(gameId: string): Promise<PriceData[]> {
    // HTTP: GET /catalog/catalogItems?marketCatalogItemId={gameId}
    // Headers: {
    //   'Accept': 'application/json',
    //   'User-Agent': 'GamePriceTracker/1.0'
    // }

    // Response: {
    //   "items": [
    //     {
    //       "id": "marketCatalogItemId",
    //       "name": "Game Title",
    //       "price": { "original": 59.99, "discount": 20 },
    //       "rating": { "stars": 4.5, "count": 12345 }
    //     }
    //   ]
    // }

    // Transform: { gameId, name, price, discount, rating, source: 'epic', timestamp }
  }

  async updatePrices(gameId: string, prices: PriceData[]): Promise<void> {
    // Delegate to priceRepository
  }
}
```

### GOG Gateway

```typescript
class GOGGateway implements IGateway {
  private readonly _lastFetched: Map<string, number>;

  constructor(baseUrl: string = 'https://www.gog.com/products') {
    this._lastFetched = new Map();
  }

  async fetchPrices(gameId: string): Promise<PriceData[]> {
    // HTTP: GET /products?product_id={gameId}
    // Headers: {
    //   'Accept': 'application/json',
    //   'User-Agent': 'GamePriceTracker/1.0'
    // }

    // Response: {
    //   "products": [
    //     {
    //       "title": "Game Title",
    //       "price": { "amount": 59.99, "discount_percent": 0 },
    //       "rating": { "average": 4.5, "total_ratings": 1234 }
    //     }
    //   ]
    // }

    // Transform: { gameId, name, price, discount, rating, source: 'gog', timestamp }
  }

  async updatePrices(gameId: string, prices: PriceData[]): Promise<void> {
    // Delegate to priceRepository
  }
}
```

## Key Decisions

- **Rate limiting**: Implement per-gateway retry logic with exponential backoff
- **Error handling**: Graceful degradation — if one source fails, continue with others
- **Data freshness**: Track last fetch time per source, skip if stale
- **Caching**: No caching in repository — gateway handles its own caching
- **Authentication**: Steam requires API key; Epic/GOG use standard HTTP

## Test Files

**Location**: `src/gateways/`

| File | Coverage |
|------|----------|
| `steamGateway.spec.ts` | 4 test cases |
| `epicGateway.spec.ts` | 3 test cases |
| `gogGateway.spec.ts` | 3 test cases |

### Test Structure

```typescript
describe('SteamGateway', () => {
  it('should fetch prices for a game', async () => {
    const gateway = new SteamGateway('test-api-key');
    const prices = await gateway.fetchPrices('123456');
    expect(prices).toBeInstanceOf(Array);
  });

  it('should handle API errors gracefully', async () => {
    const gateway = new SteamGateway('invalid-key');
    // Mock network error handling
  });
});
```

## Implementation Steps

1. Create `src/gateways/steamGateway.ts` with `SteamGateway` class
2. Create `src/gateways/epicGateway.ts` with `EpicGateway` class
3. Create `src/gateways/gogGateway.ts` with `GOGGateway` class
4. Create `src/gateways/index.ts` barrel file
5. Create `src/gateways/steamGateway.spec.ts`
6. Create `src/gateways/epicGateway.spec.ts`
7. Create `src/gateways/gogGateway.spec.ts`
8. Run tests with `npx vitest run src/gateways/`

## Test Commands

```bash
# Run all gateway tests
npx vitest run src/gateways/

# Run specific test file
npx vitest run src/gateways/steamGateway.spec.ts

# Run with coverage
npx vitest run --coverage src/gateways/
```

## Verification

- Tests pass: `npx vitest run src/gateways/`
- Mocked API responses for all gateway tests
- Network calls stubbed in test setup

## What This Provides

- **External data fetching**: Integration with Steam, Epic, GOG APIs
- **Gateway pattern**: Each gateway implements same interface
- **Error resilience**: Graceful degradation on API failures
- **Rate limiting**: Per-gateway retry logic with exponential backoff

## What This Does NOT Provide

- **Database access**: No direct database calls
- **Price updates**: Delegates to repository via services
- **Authentication**: Steam API key required (not provided in plan)
- **Full error handling**: Basic error handling; production needs more

---

**Generated**: 2026-04-21
**Phase**: 4 of 7 (GamePriceTracker BE Implementation Plan)
**Approach**: TypeScript + Vitest, gateway pattern with rate limiting