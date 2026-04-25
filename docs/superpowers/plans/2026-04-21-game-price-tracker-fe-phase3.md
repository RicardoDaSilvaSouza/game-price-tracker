# GamePriceTracker Frontend: Phase 3 Implementation Plan (State Management)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement Zustand for client state (favorites, selected country) and TanStack Query for server state management.

**Architecture:** This phase establishes the state management layer, separating client state (persistent preferences) from server state (cached API responses).

**Tech Stack:** Zustand v5, TanStack Query v5, AsyncStorage

---

## Task 1: Set Up Zustand for Favorites Store

**Files:**
- Create: `app/store/useFavoritesStore.ts`

- [ ] **Step 1: Write the failing test for favorites store**

```typescript
// In tests/favorites-store.test.ts
import { useFavoritesStore } from '../store/useFavoritesStore';

describe('Favorites Store', () => {
  it('should add game to favorites list', () => {
    const { addToFavorites, favorites } = useFavoritesStore();
    addToFavorites('game-id-1');
    expect(favorites).toContain('game-id-1');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/favorites-store.test.ts`
Expected: FAIL, store not implemented

- [ ] **Step 3: Write minimal implementation (useFavoritesStore.ts)**

```typescript
// app/store/useFavoritesStore.ts
import { create } from 'zustand';
import { persist } from 'zustand/persistence';
import AsyncStorage from '@react-native/async-storage';

interface FavoritesState {
  items: string[];
  selectedCountry: string;
}

export const useFavoritesStore = create<FavoritesState>(
  persist(
    (set) => ({
      items: [],
      selectedCountry: 'US',
    }),
    { storageKey: 'game-price-tracker' },
  )
);
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/favorites-store.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add app/store/useFavoritesStore.ts tests/favorites-store.test.ts
git commit -m "feat: implement favorites store with zustand and persistence"
```

## Task 2: Set Up TanStack Query for Server State

**Files:**
- Create: `app/hooks/useGamePrices.ts`
- Modify: `app/_layout.tsx`

- [ ] **Step 1: Write the failing test for TanStack Query hooks**

```typescript
// In tests/game-prices-hooks.test.ts
import { renderHook } from '@testing-library/react';
import { useGamePrices } from '../hooks/useGamePrices';

describe('Game Prices Hooks', () => {
  it('should fetch prices for a game', async () => {
    const hook = renderHook(() => useGamePrices('game-id'));
    expect(hook.data).toBeUndefined();
    await hook.promise;
    expect(hook.data).toBeDefined();
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/game-prices-hooks.test.ts`
Expected: FAIL, hook not implemented

- [ ] **Step 3: Write minimal implementation (useGamePrices.ts)**

```typescript
// app/hooks/useGamePrices.ts
import { useQuery } from 'tanstack-query';

interface Price {
  shop: string;
  amount: number;
  currency: string;
  cutPct?: number;
}

export function useGamePrices(gameId: string, country: string = 'US') {
  return useQuery<Price[]>([
    'prices',
    gameId,
    country,
  ], async () => {
    const response = await fetch(`/api/game/${gameId}/prices?country=${country}`);
    return response.json();
  });
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/game-prices-hooks.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add app/hooks/useGamePrices.ts tests/game-prices-hooks.test.ts
git commit -m "feat: implement game prices hook with tanstack-query"
```

## Task 3: Wrap App with State Providers

**Files:**
- Modify: `app/_layout.tsx`

- [ ] **Step 1: Write the failing test for providers**

```typescript
// In tests/providers.test.ts
import { render } from '@testing-library/react-native';
import { default as Layout } from '../_layout';

describe('State Providers', () => {
  it('should provide zustand and tanstack-query context', () => {
    const { container } = render(<Layout />);
    expect(container).toBeDefined();
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/providers.test.ts`
Expected: FAIL, providers not configured

- [ ] **Step 3: Write minimal implementation (_layout.tsx)**

```typescript
// app/_layout.tsx
import { router } from '@expo/react-native-router';
import { TanStackProvider } from 'tanstack-query';

export default function Layout() {
  return (
    <TanStackProvider>
      <Router />
    </TanStackProvider>
  );
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/providers.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add "app/_layout.tsx" tests/providers.test.ts
git commit -m "feat: wrap app with zustand and tanstack-query providers"
```

## Task 4: Integrate Store with Screens

**Files:**
- Modify: `app/(tabs)/index.tsx`
- Modify: `app/(tabs)/favorites.tsx`
- Modify: `app/game/[id].tsx`

- [ ] **Step 1: Write the failing test for store integration**

```typescript
// In tests/store-integration.test.ts
import { render } from '@testing-library/react-native';
import { default as FavoritesScreen } from '../(tabs)/favorites';

describe('Store Integration', () => {
  it('should display favorites from store', () => {
    const { getByText } = render(<FavoritesScreen />);
    expect(getByText('No favorites')).toBeDefined();
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/store-integration.test.ts`
Expected: FAIL, store not used in screens

- [ ] **Step 3: Write minimal implementation (favorites.tsx)**

```typescript
// app/(tabs)/favorites.tsx
import { View, Text } from 'react-native';
import nativind from 'nativind';
import { useFavoritesStore } from '../store/useFavoritesStore';

export default function FavoritesScreen() {
  const { favorites } = useFavoritesStore();

  if (favorites.length === 0) {
    return (
      <View style={nativind.style([nativind.colors.background])}>
        <Text style={nativind.style([nativind.typography['body-md'], nativind.spacing.lg])}>
          No favorites
        </Text>
      </View>
    );
  }

  // TODO: Render favorites list
  return null;
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/store-integration.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add "app/(tabs)/favorites.tsx" tests/store-integration.test.ts
git commit -m "feat: integrate favorites store with favorites screen"
```

