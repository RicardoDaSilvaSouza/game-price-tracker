# GamePriceTracker Frontend: Phase 2 Implementation Plan (Game Detail & Prices)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement the game detail screen with price breakdown table and country selector.

**Architecture:** This phase adds navigation to the game detail screen and displays comprehensive price information per store.

**Tech Stack:** Expo Router v4, React Native, NatindWind v4

---

## Task 1: Implement Game Detail Screen Navigation

**Files:**
- Modify: `app/(tabs)/index.tsx`
- Create: `app/game/[id].tsx`

- [ ] **Step 1: Write the failing test for navigation**

```typescript
// In tests/navigation.test.ts
import { render } from '@testing-library/react-native';
import { default as IndexScreen } from '../(tabs)/index';
import { router } from '@expo/react-native-router';

describe('Game Detail Navigation', () => {
  it('should navigate to game detail screen on card press', () => {
    const { getByTestId } = render(<IndexScreen />);
    const card = getByTestId('game-card-test-id');
    fireEvent(card, 'press');
    expect(router.current).toHaveBeenCalledWith('game', { id: 'test-id' });
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/navigation.test.ts`
Expected: FAIL, navigation not implemented

- [ ] **Step 3: Write minimal implementation (index.tsx)**

```typescript
// app/(tabs)/index.tsx
import { router } from '@expo/react-native-router';

// ... existing code ...

  const handleCardPress = (gameId: string) => {
    router.goTo('game', { id: gameId });
  };
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/navigation.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add "app/(tabs)/index.tsx" tests/navigation.test.ts
git commit -m "feat: implement navigation to game detail screen"
```

## Task 2: Create Game Detail Screen Skeleton

**Files:**
- Create: `app/game/[id].tsx`

- [ ] **Step 1: Write the failing test for detail screen**

```typescript
// In tests/detail-screen.test.ts
import { render } from '@testing-library/react-native';
import { default as DetailScreen } from '../game/[id]';

describe('Game Detail Screen', () => {
  it('should render game title when mounted', () => {
    const { getByText } = render(<DetailScreen params={{ id: 'test-id' }} />);
    expect(getByText('Game Title')).toBeDefined();
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/detail-screen.test.ts`
Expected: FAIL, screen not implemented

- [ ] **Step 3: Write minimal implementation (detail screen)**

```typescript
// app/game/[id].tsx
import { View, Text } from 'react-native';
import nativind from 'nativind';
import { router } from '@expo/react-native-router';

interface DetailScreenParams {
  id: string;
}

export default function DetailScreen({ params }: { params: DetailScreenParams }) {
  const gameId = params.id;

  return (
    <View style={nativind.style([nativind.colors.background])}>
      <Text style={nativind.style([nativind.typography['display-md'], nativind.spacing.lg])}>
        Game {gameId}
      </Text>
    </View>
  );
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/detail-screen.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add "app/game/[id].tsx" tests/detail-screen.test.ts
git commit -m "feat: create game detail screen skeleton"
```

## Task 3: Implement Price Table Component

**Files:**
- Create: `app/components/PriceTable.tsx`
- Create: `app/components/CountrySelector.tsx`

- [ ] **Step 1: Write the failing test for price table**

```typescript
// In tests/price-table.test.ts
import { render } from '@testing-library/react-native';
import { PriceTable } from '../components/PriceTable';

describe('PriceTable Component', () => {
  it('should display price rows for each store', () => {
    const prices = [
      { shop: 'Steam', amount: 10.5, currency: 'USD' },
      { shop: 'GOG', amount: 15.0, currency: 'EUR' },
    ];
    const { getAllByTestId } = render(<PriceTable prices={prices} />);
    const rows = getAllByTestId('price-row');
    expect(rows.length).toBe(2);
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/price-table.test.ts`
Expected: FAIL, component not defined

- [ ] **Step 3: Write minimal implementation (PriceTable.tsx)**

```typescript
// app/components/PriceTable.tsx
import { View, Text, FlatList } from 'react-native';
import nativind from 'nativind';

interface PriceRowProps {
  shop: string;
  amount: number;
  currency: string;
  cutPct?: number;
}

export default function PriceRow({ shop, amount, currency, cutPct }: PriceRowProps) {
  return (
    <View
      testId="price-row"
      style={nativind.style([nativind.colors.background, nativind.roundness.medium])}
    >
      <Text style={nativind.style([nativind.typography['body-md'], nativind.spacing.md])}>
        {shop}: {amount.toFixed(2)} {currency}
      </Text>
      {cutPct !== undefined && cutPct > 0 && (
        <Text style={nativind.style([nativind.colors.primary, nativind.typography['body-sm']])}>
          -{cutPct}% discount
        </Text>
      )}
    </View>
  );
}

interface PriceTableProps {
  prices: Array<{
    shop: string;
    amount: number;
    currency: string;
    cutPct?: number;
  }>;
}

export default function PriceTable({ prices }: PriceTableProps) {
  return (
    <FlatList
      data={prices}
      keyExtractor={(item) => item.shop}
      renderItem={({ item }) => <PriceRow {...item} />}
      style={nativind.style([nativind.spacing.lg])}
    />
  );
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/price-table.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add app/components/PriceTable.tsx app/components/CountrySelector.tsx tests/price-table.test.ts
git commit -m "feat: implement price table component with country selector"
```

## Task 4: Integrate Price Table into Detail Screen

**Files:**
- Modify: `app/game/[id].tsx`

- [ ] **Step 1: Write the failing test for integration**

```typescript
// In tests/detail-integration.test.ts
import { render } from '@testing-library/react-native';
import { default as DetailScreen } from '../game/[id]';

describe('Detail Screen Integration', () => {
  it('should render price table with prices', () => {
    const prices = [
      { shop: 'Steam', amount: 10.5, currency: 'USD', cutPct: 20 },
    ];
    const { getByText } = render(<DetailScreen params={{ id: 'test-id' }} prices={prices} />);
    expect(getByText('Steam: 10.50 USD')).toBeDefined();
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/detail-integration.test.ts`
Expected: FAIL, prices not integrated

- [ ] **Step 3: Write minimal implementation (detail screen)**

```typescript
// app/game/[id].tsx
import { View, Text } from 'react-native';
import nativind from 'nativind';
import { PriceTable } from '../components/PriceTable';

interface DetailScreenParams {
  id: string;
}

interface Price {
  shop: string;
  amount: number;
  currency: string;
  cutPct?: number;
}

export default function DetailScreen({ params }: { params: DetailScreenParams }) {
  const gameId = params.id;

  // TODO: Fetch prices from API
  const prices: Price[] = [
    { shop: 'Steam', amount: 10.5, currency: 'USD', cutPct: 20 },
  ];

  return (
    <View style={nativind.style([nativind.colors.background])}>
      <Text style={nativind.style([nativind.typography['display-md'], nativind.spacing.lg])}>
        Game {gameId}
      </Text>
      <PriceTable prices={prices} />
    </View>
  );
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/detail-integration.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add "app/game/[id].tsx" tests/detail-integration.test.ts
git commit -m "feat: integrate price table into game detail screen"
```

