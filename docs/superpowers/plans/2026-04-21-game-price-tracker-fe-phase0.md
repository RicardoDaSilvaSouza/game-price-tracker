# GamePriceTracker Frontend: Phase 0 Implementation Plan (Project Setup)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Set up the Expo project with all dependencies, configuration, and folder structure required for the cross-platform GamePriceTracker application.

**Architecture:** This phase establishes the foundational layer: Expo SDK 52 with file-based routing, TypeScript strict mode, NativeWind v4 for styling, and the shared TypeScript types. The project will be structured to serve iOS, Android, and Web from a single codebase.

**Tech Stack:** Expo SDK 52, TypeScript, NativeWind v4, TanStack Query v5, Zustand v5, AsyncStorage, Expo Router v4

---

## Task 1: Initialize Expo Project with TypeScript

**Files:**
- Create: `package.json`
- Create: `tsconfig.json`
- Create: `app/ts-compiler-config.json`
- Create: `app/index.tsx`
- Create: `app/_layout.tsx`

- [ ] **Step 1: Write the failing test for project initialization**

```typescript
// In tests/setup.test.ts
import { readFileSync } from 'fs';

describe('Expo Project Initialization', () => {
  it('should have required packages in package.json', () => {
    const pkg = JSON.parse(readFileSync('./package.json', 'utf8'));
    expect(pkg.dependencies.expo).toBe('expo');
    expect(pkg.dependencies['@expo/react-native-router']).toBe('4');
    expect(pkg.dependencies.nativewind).toBe('4');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/setup.test.ts`
Expected: FAIL, "ENOENT" or missing dependencies

- [ ] **Step 3: Write minimal implementation (package.json)**

```json
{
  "name": "game-price-tracker",
  "version": "0.0.0",
  "scripts": {
    "start:app": "expo start",
    "start:web": "expo start --t",
    "build": "eas build",
    "lint": "tsc --noEmit"
  },
  "dependencies": {
    "expo": "52",
    "@expo/react-native-router": "4",
    "nativewind": "4",
    "tanstack-query": "5",
    "zustand": "5",
    "@react-native/async-storage": "latest",
    "lucide-react-native": "latest",
    "react": "17",
    "react-native": "0.73"
  },
  "devDependencies": {
    "@typescript/expos": "5.0.0",
    "@types/react": "17",
    "@types/react-native": "0.73"
  }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/setup.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add package.json tsconfig.json app/index.tsx app/_layout.tsx tests/setup.test.ts
git commit -m "chore: initialize expo project with typescript and dependencies"
```

## Task 2: Configure TypeScript Strict Mode

**Files:**
- Create: `tsconfig.json`
- Create: `app/ts-compiler-config.json`

- [ ] **Step 1: Write the failing test for TypeScript configuration**

```typescript
// In tests/typescript.test.ts
import { readFileSync } from 'fs';

describe('TypeScript Strict Mode', () => {
  it('should enable strict mode flags', () => {
    const tsConfig = JSON.parse(readFileSync('./tsconfig.json', 'utf8'));
    expect(tsConfig.compilerOptions.strict).toBe(true);
    expect(tsConfig.compilerOptions.noImplicitAny).toBe(true);
    expect(tsConfig.compilerOptions.noUnusedParameters).toBe(true);
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/typescript.test.ts`
Expected: FAIL, "ENOENT" or missing strict flags

- [ ] **Step 3: Write minimal implementation (tsconfig.json)**

```json
{
  "compilerOptions": {
    "strict": true,
    "noImplicitAny": true,
    "noUnusedParameters": true,
    "noImplicitReturns": true,
    "noUncheckedCall": true,
    "exactFunctionTypes": true,
    "strictNullChecks": true,
    "skipLibCheck": true,
    "esModuleInterop": true,
    "noEmit": false,
    "jsx": "react-native",
    "sourceMap": true,
    "moduleResolution": "nod",
    "target": "esnext",
    "lib": ["esnext"]
  },
  "extends": "expo/ts-config.json"
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/typescript.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add tsconfig.json app/ts-compiler-config.json tests/typescript.test.ts
git commit -m "feat: configure typescript strict mode for type-safe code"
```

## Task 3: Set Up NativeWind Styling

**Files:**
- Create: `nativewind.config.ts`
- Create: `tailwind.config.ts`
- Create: `src/styles/colors.ts`

- [ ] **Step 1: Write the failing test for NativeWind configuration**

```typescript
// In tests/styling.test.ts
import { readFileSync } from 'fs';

describe('NativWind Configuration', () => {
  it('should configure tailwind with all color variants', () => {
    const config = JSON.parse(readFileSync('./nativewind.config.ts', 'utf8'));
    expect(config.theme.colors.light).toBeDefined();
    expect(config.theme.colors.dark).toBeDefined();
    expect(config.theme.colors.primary).toBeDefined();
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/styling.test.ts`
Expected: FAIL, "ENOENT" or missing configuration

- [ ] **Step 3: Write minimal implementation (nativewind.config.ts)**

```typescript
import { defineConfig } from 'nativewind';

export default defineConfig({
  theme: {
    colors: {
      light: '#ffffff',
      dark: '#242424',
      primary: '#00ff00',
      secondary: '#0000ff',
      tertiary: '#ff0000',
    },
    typography: {
      'display-lg': {
        fontSize: '32px',
        fontWeight: '700',
        lineHeight: '40px',
      },
      'display-md': {
        fontSize: '24px',
        fontWeight: '700',
        lineHeight: '32px',
      },
      'body-md': {
        fontSize: '16px',
        fontWeight: '400',
        lineHeight: '24px',
      },
      'body-sm': {
        fontSize: '12px',
        fontWeight: '400',
        lineHeight: '16px',
      },
    },
    roundness: {
      small: '4px',
      medium: '8px',
      large: '12px',
      full: '9999px',
    },
    spacing: {
      sm: '4px',
      md: '8px',
      lg: '16px',
      xl: '24px',
    },
  },
  darkMode: {
    enabled: true,
    lightBgColor: '#ffffff',
    darkBgColor: '#242424',
  },
});
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/styling.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add nativewind.config.ts tailwind.config.ts src/styles/colors.ts tests/styling.test.ts
git commit -m "feat: configure nativind styling with color and typography scales"
```

## Task 4: Create Shared Types Package

**Files:**
- Create: `shared/types/index.ts`
- Create: `shared/types/Game.ts`
- Create: `shared/types/Price.ts`
- Create: `shared/types/Country.ts`

- [ ] **Step 1: Write the failing test for shared types**

```typescript
// In tests/shared-types.test.ts
import { GamePrice } from '../../shared/types/Price';

describe('Shared Types', () => {
  it('should define GamePrice with required properties', () => {
    const price: GamePrice = {
      id: 1,
      gameId: 'test-id',
      shop: 'Steam',
      country: 'US',
      amount: 10.5,
      currency: 'USD',
      regularAmt: 15.0,
      cutPct: 33,
      fetchedAt: new Date(),
    };
    expect(price.id).toBe(1);
    expect(price.amount).toBe(10.5);
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/shared-types.test.ts`
Expected: FAIL, "GamePrice is not defined"

- [ ] **Step 3: Write minimal implementation (shared/types)**

```typescript
// shared/types/Price.ts
export interface Price {
  id: number;
  gameId: string;
  shop: string;
  country: string;
  amount: number;
  currency: string;
  regularAmt: number;
  cutPct: number;
  fetchedAt: Date;
}

// shared/types/Game.ts
export interface Game {
  id: string;
  slug: string;
  title: string;
  type: 'game' | 'dlc';
  boxart?: string;
  steamAppid?: number;
  nintendoId?: string;
  tags?: string[];
}

// shared/types/Country.ts
export interface Country {
  code: string;
  name: string;
  currencySymbol: string;
}

// shared/types/index.ts
export { Game, Price, Country };
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/shared-types.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add shared/types/index.ts shared/types/Price.ts shared/types/Game.ts shared/types/Country.ts tests/shared-types.test.ts
git commit -m "feat: define shared types for frontend and backend"
```

## Task 5: Set Up Project Folder Structure

**Files:**
- Create: `app/(tabs)/index.tsx`
- Create: `app/(tabs)/favorites.tsx`
- Create: `app/(tabs)/settings.tsx`
- Create: `app/game/[id].tsx`
- Create: `app/components/GameCard.tsx`
- Create: `app/components/PriceTable.tsx`
- Create: `app/components/SortBar.tsx`
- Create: `app/store/useFavoritesStore.ts`
- Create: `app/hooks/useGamePrices.ts`

- [ ] **Step 1: Write the failing test for folder structure**

```typescript
// In tests/structure.test.ts
import { readdirSync } from 'fs';
import { join } from 'path';

describe('Project Structure', () => {
  it('should have required folders', () => {
    const appDir = readdirSync('./app');
    expect(appDir).toContain('(tabs)');
    expect(appDir).toContain('game');
    expect(appDir).toContain('components');
    expect(appDir).toContain('store');
    expect(appDir).toContain('hooks');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npm test tests/structure.test.ts`
Expected: FAIL, folder does not exist

- [ ] **Step 3: Write minimal implementation (folder structure)**

```bash
mkdir -p app/(tabs) app/game app/components app/store app/hooks

each create empty placeholder files:
  touch app/(tabs)/index.tsx
  touch app/(tabs)/favorites.tsx
  touch app/(tabs)/settings.tsx
  touch app/game/[id].tsx
  touch app/components/GameCard.tsx
  touch app/components/PriceTable.tsx
  touch app/components/SortBar.tsx
  touch app/store/useFavoritesStore.ts
  touch app/hooks/useGamePrices.ts
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npm test tests/structure.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add "app/(tabs)/*" app/game/[id].tsx app/components/* app/store/* app/hooks/* tests/structure.test.ts
git commit -m "chore: create project folder structure with placeholder files"
```

