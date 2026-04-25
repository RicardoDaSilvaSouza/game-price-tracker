# GamePriceTracker Frontend: Phase 1 Implementation Plan (Basic Routing & Search)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement basic Expo Router navigation and the core search functionality with the GameCard component.

**Architecture:** This phase establishes file-based routing using Expo Router and implements the primary user interaction: searching for games and displaying results.

**Tech Stack:** Expo Router v4, TypeScript, React Native components

---

## Task 1: Configure Expo Router Navigation

**Files:**
- Create: `app/_layout.tsx`
- Modify: `app/(tabs)/index.tsx`

- [ ] **Step 1: Write the failing test for router configuration**

```typescript
// In tests/router.test.ts
import { render } from '@testing-library/react-native';
import { Router } from '@expo/react-native-router';

describe('Expo Router Configuration', () => {
  it('should initialize router with tab navigation', () => {
    const { container } = render(<Router />);
    expect(container).toBeDefined();
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/router.test.ts`
Expected: FAIL, "Router is not defined"

- [ ] **Step 3: Write minimal implementation (_layout.tsx)**

```typescript
// app/_layout.tsx
import { Router } from '@expo/react-native-router';

export default function Layout() {
  return <Router />;
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/router.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add app/_layout.tsx tests/router.test.ts
git commit -m "feat: configure expo router with tab navigation"
```

## Task 2: Implement Search Screen with Input

**Files:**
- Modify: `app/(tabs)/index.tsx`
- Create: `app/components/SearchInput.tsx`

- [ ] **Step 1: Write the failing test for search input**

```typescript
// In tests/search.test.ts
import { render } from '@testing-library/react-native';
import { SearchInput } from '../components/SearchInput';

describe('SearchInput Component', () => {
  it('should call onSearch when text changes', () => {
    const onSearch = jest.fn();
    const { getByTestId } = render(<SearchInput onSearch={onSearch} />);
    const input = getByTestId('search-input');
    fireEvent.changeText(input, 'test-game');
    expect(onSearch).toHaveBeenCalledWith('test-game');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/search.test.ts`
Expected: FAIL, "SearchInput is not defined"

- [ ] **Step 3: Write minimal implementation (SearchInput.tsx)**

```typescript
// app/components/SearchInput.tsx
import { View, TextInput, Text } from 'react-native';
import nativind from 'nativind';

interface SearchInputProps {
  onSearch: (query: string) => void;
}

export default function SearchInput({ onSearch }: SearchInputProps) {
  const [query, setQuery] = useState('');

  const debouncedSearch = useMemo(() => {
    const timer = setTimeout(() => {
      if (query.length > 0) {
        onSearch(query);
      }
    }, 300);
    return () => clearTimeout(timer);
  }, [query]);

  useEffect(() => {
    return debouncedSearch;
  }, [debouncedSearch]);

  return (
    <View style={nativind.style([nativind.colors.background, nativind.spacing.lg])}>
      <TextInput
        testId="search-input"
        value={query}
        onChangeText={setQuery}
        placeholder="Search games..."
        style={nativind.style([nativind.colors.primary, nativind.spacing.md])}
      />
    </View>
  );
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/search.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add app/components/SearchInput.tsx tests/search.test.ts
git commit -m "feat: implement debounced search input component"
```

## Task 3: Create GameCard Component

**Files:**
- Modify: `app/components/GameCard.tsx`
- Create: `app/components/DiscountBadge.tsx`

- [ ] **Step 1: Write the failing test for GameCard**

```typescript
// In tests/GameCard.test.ts
import { render } from '@testing-library/react-native';
import { GameCard } from '../components/GameCard';

describe('GameCard Component', () => {
  it('should display game title and thumbnail', () => {
    const game = {
      id: 'test-id',
      title: 'Test Game',
      boxart: 'test-artwork.png',
      steamAppid: 292030,
    };
    const { getByText } = render(<GameCard game={game} />);
    expect(getByText('Test Game')).toBeDefined();
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/GameCard.test.ts`
Expected: FAIL, "GameCard is not defined"

- [ ] **Step 3: Write minimal implementation (GameCard.tsx)**

```typescript
// app/components/GameCard.tsx
import { View, Text, Image } from 'react-native';
import nativind from 'nativind';

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
  return (
    <View
      testId={`game-card-${game.id}`}
      style={nativind.style([nativind.colors.background, nativind.roundness.large])}
      onPress={() => onCardPress(game.id)}
    >
      {game.boxart && (
        <Image source={{ uri: game.boxart }} style={nativind.style([nativind.roundness.large])} />
      )}
      <Text style={nativind.style([nativind.typography['display-md'], nativind.spacing.md])}>
        {game.title}
      </Text>
    </View>
  );
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/GameCard.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add app/components/GameCard.tsx app/components/DiscountBadge.tsx tests/GameCard.test.ts
git commit -m "feat: implement game card component with thumbnail display"
```

## Task 4: Implement Search Results Display

**Files:**
- Modify: `app/(tabs)/index.tsx`
- Create: `app/components/SearchResults.tsx`

- [ ] **Step 1: Write the failing test for search results**

```typescript
// In tests/search-results.test.ts
import { render } from '@testing-library/react-native';
import { SearchResults } from '../components/SearchResults';

describe('SearchResults Component', () => {
  it('should render list of GameCard components', () => {
    const games = [
      { id: '1', title: 'Game 1' },
      { id: '2', title: 'Game 2' },
    ];
    const { getAllByTestId } = render(<SearchResults games={games} />);
    const cards = getAllByTestId('game-card-1');
    expect(cards.length).toBe(1);
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/search-results.test.ts`
Expected: FAIL, "SearchResults is not defined"

- [ ] **Step 3: Write minimal implementation (SearchResults.tsx)**

```typescript
// app/components/SearchResults.tsx
import { FlatList } from 'react-native';
import { GameCard } from '../components/GameCard';
import nativind from 'nativind';

interface SearchResultsProps {
  games: Array<{
    id: string;
    title: string;
    boxart?: string;
    steamAppid?: number;
  }>;
  onCardPress: (gameId: string) => void;
}

export default function SearchResults({ games, onCardPress }: SearchResultsProps) {
  if (games.length === 0) {
    return (
      <Text style={nativind.style([nativind.typography['body-md'], nativind.spacing.lg])}>
        No results found.
      </Text>
    );
  }

  return (
    <FlatList
      data={games}
      keyExtractor={(item) => item.id}
      renderItem={({ item }) => (
        <GameCard
          game={item}
          onCardPress={onCardPress}
        />
      )}
      style={nativind.style([nativind.spacing.lg])}
    />
  );
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/search-results.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add app/components/SearchResults.tsx tests/search-results.test.ts
git commit -m "feat: implement search results list with game cards"
```

## Task 5: Connect Search to Results

**Files:**
- Modify: `app/(tabs)/index.tsx`

- [ ] **Step 1: Write the failing test for search connection**

```typescript
// In tests/search-connection.test.ts
import { render, fireEvent } from '@testing-library/react-native';
import { default as IndexScreen } from '../(tabs)/index';

describe('Search Connection', () => {
  it('should update results when search is called', () => {
    const { getByText, getByTestId } = render(<IndexScreen />);
    const input = getByTestId('search-input');
    fireEvent.changeText(input, 'test-game');
    expect(getByText('No results found.')).toBeDefined();
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/search-connection.test.ts`
Expected: FAIL, search not connected

- [ ] **Step 3: Write minimal implementation (index.tsx)**

```typescript
// app/(tabs)/index.tsx
import { View, Text } from 'react-native';
import nativind from 'nativind';
import { SearchInput } from '../components/SearchInput';
import { SearchResults } from '../components/SearchResults';

interface Game {
  id: string;
  title: string;
  boxart?: string;
  steamAppid?: number;
}

export default function IndexScreen() {
  const [searchQuery, setSearchQuery] = useState('');
  const [results, setResults] = useState<Game[]>([]);

  const handleSearch = (query: string) => {
    setSearchQuery(query);
    // TODO: Fetch from API
    setResults([]);
  };

  const handleCardPress = (gameId: string) => {
    // TODO: Navigate to game detail
    console.log(`Navigate to game ${gameId}`);
  };

  return (
    <View style={nativind.style([nativind.colors.background])}>
      <SearchInput onSearch={handleSearch} />
      <SearchResults games={results} onCardPress={handleCardPress} />
    </View>
  );
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/search-connection.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add "app/(tabs)/index.tsx" tests/search-connection.test.ts
git commit -m "feat: connect search input to results display"
```

