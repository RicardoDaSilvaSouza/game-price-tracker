## 2026-04-18‑GamePriceTracker‑FE‑Setup‑Detailed‑Design

### Overview

The frontend of the GamePriceTracker is a single‑code‑base Expo application that runs on iOS, Android, and Web. The code is structured into **Expo** (React Native + web) with a file‑based routing system, a shared **TypeScript** type‑definition package, and **NativeWind** for Tailwind CSS utilities.

### 1. Technology Stack

| Layer | Technology | Version | Purpose |
|------|-----------|--------|--------|
| Framework | Expo SDK | 52 | Cross‑platform UI |
| Router | Expo Router | 4 | File‑based routing |
| Language | TypeScript | Latest | Shared types |
| Styling | NativeWind | 4 | Tailwind utilities |
| State | TanStack Query | 5 | Server‑state caching |
| State | Zustand | 5 | Client‑state persistence |
| Persistence | AsyncStorage | Latest | Async storage |
| Icons | Lucide React Native | Latest | Icon set |
| Build | EAS | Latest | OTA builds |

### 2. Project Structure

```
app/
├── (tabs) /
│   ├── index.tsx          # Home / Search
│   ├── favorites.tsx      # Favorites
│   └── settings.tsx       # Settings |
├── game/
│   └── [id].tsx          # Game Detail |
└── _layout.tsx            # Root layout |
├── components/
│   ├── GameCard.tsx           # Game card |
│   ├── FavoritesSection.tsx   # Favorites strip |
│   ├── PriceTable.tsx         # Price table |
│   ├── CountryPicker.tsx  # Country picker |
│   └── SortBar.tsx           # Sort bar |
├── store/
│   └── useFavoritesStore.ts  # Zustand store |
├── hooks/
│   └── useGamePrices.ts       # TanStack Query hooks |
└── ... |
```

### 3. Docker‑Compose for Frontend‑Only

The Docker‑Compose snippet added to the FE spec contains only the frontend‑specific configuration – ports, volumes, environment variables, and command. The snippet is:

```
services:
  frontend:
    image: node:22-alpine
    working_dir: /app
    command: npm run start:app
    ports:
      - "8080:8080"    # Web UI port
    environment:
      - NODE_ENV=development
    volumes:
      - ./frontend:/app
      - ./data:/app/data
      - ./node_modules:/app/node_modules
``` 

### 4. Verification Checklist

- [ ] Expo CLI installed
- [ ] npm dependencies installed
- [ ] React Native code runs on a device
- [ ] Tailwind classes work in React Native
- [ ] AsyncStorage persists user preferences

### 5. Notes

- The frontend consumes the Fastify API at `http://localhost:3000`.
- All state is managed locally; no server‑side state persistence.
- The frontend never accesses the database directly; only the backend does.
- The frontend only reads from the backend API.

---
