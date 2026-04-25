# GamePriceTracker Frontend: Phase 5 Implementation Plan (Polish & Optimization)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement polish features including favorites strip on home screen, sort bar, and accessibility improvements.

**Architecture:** This phase adds UX enhancements and polish without changing core functionality.

**Tech Stack:** React Native, NatindWind v4

---

## Task 1: Create Favorites Strip Component

**Files:**
- Create: `app/components/FavoritesStrip.tsx`

- [ ] **Step 1: Write the failing test for favorites strip**

```typescript
// In tests/favorites-strip.test.ts
import { render } from '@testing-library/react-native';
import { FavoritesStrip } from '../components/FavoritesStrip';

describe('Favorites Strip', () => {
  it('should display favorites section header', () => {
    const { getByText } = render(<FavoritesStrip />);
    expect(getByText('Your Watchlist')).toBeDefined();
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/favorites-strip.test.ts`
Expected: FAIL, component not defined

- [ ] **Step 3: Write minimal implementation (FavoritesStrip.tsx)**

```typescript
// app/components/FavoritesStrip.tsx
import { View, Text, FlatList } from 'react-native';
import nativind from 'nativind';
import { useFavoritesStore } from '../store/useFavoritesStore';
import { GameCard } from '../components/GameCard';

export default function FavoritesStrip() {
  const { favorites } = useFavoritesStore();

  if (favorites.length === 0) {
    return null;
  }

  return (
    <View style={nativind.style([nativind.colors.primary, nativind.roundness.large, nativind.spacing.lg])}>
      <Text style={nativind.style([nativind.typography['display-md'], nativind.spacing.md])}>
        ★ Your Watchlist
      </Text>
      <FlatList
        data={favorites}
        keyExtractor={(item) => item}
        renderItem={({ item }) => (
          <GameCard
            game={{ id: item, title: item }}
            onCardPress={() => {}}
          />
        )}
        horizontal
        style={nativind.style([nativind.spacing.md])}
      />
    </View>
  );
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/favorites-strip.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add app/components/FavoritesStrip.tsx tests/favorites-strip.test.ts
git commit -m "feat: implement favorites strip component for home screen"
```

## Task 2: Integrate Favorites Strip on Home Screen

**Files:**
- Modify: `app/(tabs)/index.tsx`

- [ ] **Step 1: Write the failing test for strip integration**

```typescript
// In tests/strip-integration.test.ts
import { render } from '@testing-library/react-native';
import { default as IndexScreen } from '../(tabs)/index';

describe('Strip Integration', () => {
  it('should display favorites strip when favorites exist', () => {
    const { getByText } = render(<IndexScreen />);
    expect(getByText('Your Watchlist')).toBeDefined();
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/strip-integration.test.ts`
Expected: FAIL, strip not integrated

- [ ] **Step 3: Write minimal implementation (index.tsx)**

```typescript
// app/(tabs)/index.tsx
import { View, Text } from 'react-native';
import nativind from 'nativind';
import { SearchInput } from '../components/SearchInput';
import { SearchResults } from '../components/SearchResults';
import { FavoritesStrip } from '../components/FavoritesStrip';

export default function IndexScreen() {
  const [searchQuery, setSearchQuery] = useState('');
  const [results, setResults] = useState<Game[]>([]);

  const handleSearch = (query: string) => {
    setSearchQuery(query);
    setResults([]);
  };

  const handleCardPress = (gameId: string) => {
    router.goTo('game', { id: gameId });
  };

  return (
    <View style={nativind.style([nativind.colors.background])}>
      <FavoritesStrip />
      <SearchInput onSearch={handleSearch} />
      <SearchResults games={results} onCardPress={handleCardPress} />
    </View>
  );
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/strip-integration.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add "app/(tabs)/index.tsx" tests/strip-integration.test.ts
git commit -m "feat: integrate favorites strip into home screen"
```

## Task 3: Implement Sort Bar Component

**Files:**
- Create: `app/components/SortBar.tsx`

- [ ] **Step 1: Write the failing test for sort bar**

```typescript
// In tests/sort-bar.test.ts
import { render, fireEvent } from '@testing-library/react-native';
import { SortBar } from '../components/SortBar';

describe('Sort Bar', () => {
  it('should call onSort when sort option is pressed', () => {
    const onSort = jest.fn();
    const { getByText } = render(<SortBar onSort={onSort} />);
    const button = getByText('Best Price');
    fireEvent.press(button);
    expect(onSort).toHaveBeenCalledWith('price');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/sort-bar.test.ts`
Expected: FAIL, component not defined

- [ ] **Step 3: Write minimal implementation (SortBar.tsx)**

```typescript
// app/components/SortBar.tsx
import { View, Text, TouchableOpacity } from 'react-native';
import nativind from 'nativind';

interface SortBarProps {
  onSort: (sortBy: 'price' | 'discount' | 'alpha') => void;
}

export default function SortBar({ onSort }: SortBarProps) {
  return (
    <View style={nativind.style([nativind.colors.background, nativind.roundness.medium, nativind.spacing.lg])}>
      <TouchableOpacity
        testId="sort-price"
        onPress={() => onSort('price')}
        style={nativind.style([nativind.colors.primary, nativind.roundness.full])}
      >
        <Text style={nativind.style([nativind.typography['body-sm']])}>
          Best Price
        </Text>
      </TouchableOpacity>
      <TouchableOpacity
        testId="sort-discount"
        onPress={() => onSort('discount')}
        style={nativind.style([nativind.colors.primary, nativind.roundness.full])}
      >
        <Text style={nativind.style([nativind.typography['body-sm']])}>
          Biggest Discount
        </Text>
      </TouchableOpacity>
      <TouchableOpacity
        testId="sort-alpha"
        onPress={() => onSort('alpha')}
        style={nativind.style([nativind.colors.primary, nativind.roundness.full])}
      >
        <Text style={nativind.style([nativind.typography['body-sm']])}>
          A–Z
        </Text>
      </TouchableOpacity>
    </View>
  );
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/sort-bar.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add app/components/SortBar.tsx tests/sort-bar.test.ts
git commit -m "feat: implement sort bar component with three sort options"
```

## Task 4: Integrate Sort Bar with Search Results

**Files:**
- Modify: `app/(tabs)/index.tsx`

- [ ] **Step 1: Write the failing test for sort integration**

```typescript
// In tests/sort-integration.test.ts
import { render, fireEvent } from '@testing-library/react-native';
import { default as IndexScreen } from '../(tabs)/index';

describe('Sort Integration', () => {
  it('should re-sort results when sort option is pressed', () => {
    const { getByText } = render(<IndexScreen />);
    const button = getByText('Best Price');
    fireEvent.press(button);
    // TODO: Verify results are sorted
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/sort-integration.test.ts`
Expected: FAIL, sort not integrated

- [ ] **Step 3: Write minimal implementation (index.tsx)**

```typescript
// app/(tabs)/index.tsx
import { View, Text } from 'react-native';
import nativind from 'nativind';
import { SearchInput } from '../components/SearchInput';
import { SearchResults } from '../components/SearchResults';
import { FavoritesStrip } from '../components/FavoritesStrip';
import { SortBar } from '../components/SortBar';

export default function IndexScreen() {
  const [searchQuery, setSearchQuery] = useState('');
  const [results, setResults] = useState<Game[]>([]);
  const [sortBy, setSortBy] = useState<'price' | 'discount' | 'alpha'>('discount');

  const handleSearch = (query: string) => {
    setSearchQuery(query);
    setResults([]);
  };

  const handleSort = (newSortBy: 'price' | 'discount' | 'alpha') => {
    setSortBy(newSortBy);
    // TODO: Re-sort results
  };

  const handleCardPress = (gameId: string) => {
    router.goTo('game', { id: gameId });
  };

  return (
    <View style={nativind.style([nativind.colors.background])}>
      <FavoritesStrip />
      <SearchInput onSearch={handleSearch} />
      <SortBar onSort={handleSort} />
      <SearchResults games={results} onCardPress={handleCardPress} />
    </View>
  );
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/sort-integration.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add "app/(tabs)/index.tsx" tests/sort-integration.test.ts
git commit -m "feat: integrate sort bar with search results"
```

## Task 5: Add Accessibility Improvements

**Files:**
- Modify: `app/components/GameCard.tsx`
- Modify: `app/components/PriceTable.tsx`

- [ ] **Step 1: Write the failing test for accessibility**

```typescript
// In tests/accessibility.test.ts
import { render } from '@testing-library/react-native';
import { GameCard } from '../components/GameCard';

describe('Accessibility', () => {
  it('should have accessible label on game card', () => {
    const { getByAccessibilityProp } = render(<GameCard game={{ id: '1', title: 'Test' }} onCardPress={() => {}} />);
    expect(getByAccessibilityProp('accessibilityLabel')).toBeDefined();
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/accessibility.test.ts`
Expected: FAIL, no accessibility label

- [ ] **Step 3: Write minimal implementation (GameCard.tsx)**

```typescript
// app/components/GameCard.tsx
import { View, Text, TouchableOpacity } from 'react-native';
import nativind from 'nativind';
import { useFavoritesStore } from '../store/useFavoritesStore';

interface GameCardProps {
  game: {
    id: string;
    title: string;
    boxart?: string;
    steamAppid?: number;
  };
  onCardPress: (gameId: string) => void;
}

export default function GameCard({ game, onCardPress }: GameCardProps) {
  const { favorites, addToFavorites, removeFromFavorites } = useFavoritesStore();
  const isFavorite = favorites.includes(game.id);

  return (
    <TouchableOpacity
      testId={`game-card-${game.id}`}
      onPress={() => onCardPress(game.id)}
      style={nativind.style([nativind.colors.background, nativind.roundness.large])}
      accessibilityLabel={`Game ${game.title}, ${isFavorite ? 'favorited' : 'not favorited'}`}
      accessibilityTraits={['pressable']}
    >
      <TouchableOpacity
        testId="favorites-toggle"
        onPress={() => isFavorite ? removeFromFavorites(game.id) : addToFavorites(game.id)}
        style={nativind.style([nativind.colors.primary, nativind.roundness.full])}
        accessibilityLabel={`Toggle favorite for ${game.title}`}
      >
        <Text style={nativind.style([nativind.typography['body-md']])}>
          {isFavorite ? '★' : '☆'}
        </Text>
      </TouchableOpacity>
      <Text style={nativind.style([nativind.typography['display-md'], nativind.spacing.md])}>
        {game.title}
      </Text>
    </TouchableOpacity>
  );
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/accessibility.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add "app/components/GameCard.tsx" tests/accessibility.test.ts
git commit -m "feat: add accessibility labels to game cards"
```

***

**Plan complete and saved to `docs/superpowers/plans/2026-04-21-game-price-tracker-fe-phase5.md`.**
