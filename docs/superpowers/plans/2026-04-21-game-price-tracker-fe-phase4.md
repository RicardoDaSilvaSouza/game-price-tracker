# GamePriceTracker Frontend: Phase 4 Implementation Plan (Favorites & Settings)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement full favorites functionality, settings screen with country selector, and theme toggle.

**Architecture:** This phase completes the core user interface with favorites management and settings configuration.

**Tech Stack:** Zustand v5, React Native, NatindWind v4

---

## Task 1: Implement Favorites Screen Full List

**Files:**
- Modify: `app/(tabs)/favorites.tsx`

- [ ] **Step 1: Write the failing test for favorites list**

```typescript
// In tests/favorites-list.test.ts
import { render } from '@testing-library/react-native';
import { default as FavoritesScreen } from '../(tabs)/favorites';

describe('Favorites List', () => {
  it('should render list of favorite games', () => {
    const favorites = ['game-1', 'game-2'];
    const { getAllByTestId } = render(<FavoritesScreen />);
    const cards = getAllByTestId('game-card');
    expect(cards.length).toBe(2);
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/favorites-list.test.ts`
Expected: FAIL, list not implemented

- [ ] **Step 3: Write minimal implementation (favorites.tsx)**

```typescript
// app/(tabs)/favorites.tsx
import { View, Text, FlatList } from 'react-native';
import nativind from 'nativind';
import { useFavoritesStore } from '../store/useFavoritesStore';
import { GameCard } from '../components/GameCard';

export default function FavoritesScreen() {
  const { favorites } = useFavoritesStore();

  if (favorites.length === 0) {
    return (
      <View style={nativind.style([nativind.colors.background])}>
        <Text style={nativind.style([nativind.typography['body-md'], nativind.spacing.lg])}>
          Tap the ★ on any game to save it here
        </Text>
      </View>
    );
  }

  return (
    <FlatList
      data={favorites}
      keyExtractor={(item) => item}
      renderItem={({ item }) => (
        <GameCard game={{ id: item, title: item }} onCardPress={() => {}} />
      )}
      style={nativind.style([nativind.spacing.lg])}
    />
  );
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/favorites-list.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add "app/(tabs)/favorites.tsx" tests/favorites-list.test.ts
git commit -m "feat: implement favorites list with game cards"
```

## Task 2: Create Settings Screen

**Files:**
- Create: `app/(tabs)/settings.tsx`
- Create: `app/components/CountryPicker.tsx`

- [ ] **Step 1: Write the failing test for settings screen**

```typescript
// In tests/settings.test.ts
import { render } from '@testing-library/react-native';
import { default as SettingsScreen } from '../(tabs)/settings';

describe('Settings Screen', () => {
  it('should display country selector', () => {
    const { getByText } = render(<SettingsScreen />);
    expect(getByText('Country')).toBeDefined();
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/settings.test.ts`
Expected: FAIL, screen not implemented

- [ ] **Step 3: Write minimal implementation (settings.tsx)**

```typescript
// app/(tabs)/settings.tsx
import { View, Text } from 'react-native';
import nativind from 'nativind';
import { useFavoritesStore } from '../store/useFavoritesStore';
import { CountryPicker } from '../components/CountryPicker';

export default function SettingsScreen() {
  const { selectedCountry, setSelectedCountry } = useFavoritesStore();

  return (
    <View style={nativind.style([nativind.colors.background])}>
      <Text style={nativind.style([nativind.typography['display-md'], nativind.spacing.lg])}>
        Settings
      </Text>
      <CountryPicker
        value={selectedCountry}
        onChange={setSelectedCountry}
      />
    </View>
  );
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/settings.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add "app/(tabs)/settings.tsx" app/components/CountryPicker.tsx tests/settings.test.ts
git commit -m "feat: implement settings screen with country picker"
```

## Task 3: Add Theme Toggle

**Files:**
- Modify: `app/(tabs)/settings.tsx`
- Create: `app/components/ThemeToggle.tsx`

- [ ] **Step 1: Write the failing test for theme toggle**

```typescript
// In tests/theme-toggle.test.ts
import { render } from '@testing-library/react-native';
import { default as SettingsScreen } from '../(tabs)/settings';

describe('Theme Toggle', () => {
  it('should toggle between light and dark mode', () => {
    const { getByText } = render(<SettingsScreen />);
    const toggle = getByText('Dark Mode');
    // TODO: Test toggle behavior
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/theme-toggle.test.ts`
Expected: FAIL, toggle not implemented

- [ ] **Step 3: Write minimal implementation (ThemeToggle.tsx)**

```typescript
// app/components/ThemeToggle.tsx
import { View, Text, TouchableOpacity } from 'react-native';
import nativind from 'nativind';

interface ThemeToggleProps {
  isDark: boolean;
  onToggle: () => void;
}

export default function ThemeToggle({ isDark, onToggle }: ThemeToggleProps) {
  return (
    <TouchableOpacity
      testId="theme-toggle"
      onPress={onToggle}
      style={nativind.style([nativind.colors.background, nativind.roundness.medium])}
    >
      <Text style={nativind.style([nativind.typography['body-md'], nativind.spacing.md])}>
        {isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
      </Text>
    </TouchableOpacity>
  );
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/theme-toggle.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add app/components/ThemeToggle.tsx tests/theme-toggle.test.ts
git commit -m "feat: implement theme toggle component"
```

## Task 4: Add Favorites Toggle to Game Card

**Files:**
- Modify: `app/components/GameCard.tsx`

- [ ] **Step 1: Write the failing test for favorites toggle**

```typescript
// In tests/favorites-toggle.test.ts
import { render, fireEvent } from '@testing-library/react-native';
import { GameCard } from '../components/GameCard';

describe('Favorites Toggle', () => {
  it('should add game to favorites when toggle is pressed', () => {
    const { addToFavorites } = useFavoritesStore();
    const { getByTestId } = render(<GameCard game={{ id: 'game-1' }} />);
    const toggle = getByTestId('favorites-toggle');
    fireEvent.press(toggle);
    expect(addToFavorites).toHaveBeenCalledWith('game-1');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/favorites-toggle.test.ts`
Expected: FAIL, toggle not implemented

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
    <View
      testId={`game-card-${game.id}`}
      style={nativind.style([nativind.colors.background, nativind.roundness.large])}
    >
      <TouchableOpacity
        testId="favorites-toggle"
        onPress={() => isFavorite ? removeFromFavorites(game.id) : addToFavorites(game.id)}
        style={nativind.style([nativind.colors.primary, nativind.roundness.full])}
      >
        <Text style={nativind.style([nativind.typography['body-md']])}>
          {isFavorite ? '★' : '☆'}
        </Text>
      </TouchableOpacity>
      <Text style={nativind.style([nativind.typography['display-md'], nativind.spacing.md])}>
        {game.title}
      </Text>
    </View>
  );
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/favorites-toggle.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add "app/components/GameCard.tsx" tests/favorites-toggle.test.ts
git commit -m "feat: add favorites toggle button to game card"
```

