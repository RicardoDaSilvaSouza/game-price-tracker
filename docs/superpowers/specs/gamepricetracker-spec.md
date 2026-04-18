# GamePriceTracker — Product Specification

## Overview

GamePriceTracker is a cross-platform application (iOS, Android, and Web) that aggregates game prices across multiple digital storefronts and countries, allowing users to find the best deal for any game at a glance. The app is designed to run on a single personal machine (local or VPS) with no authentication layer in the MVP and no caching infrastructure beyond the database itself.

---

## 1. Free Data Sources & API Reference

### 1.1 Primary Aggregator — IsThereAnyDeal (ITAD)

ITAD is the backbone of the application. It covers Steam, GOG, Epic Games, Humble Store, Fanatical, Green Man Gaming, and many more stores in a single unified API. A free API key is obtained by registering an app at [https://isthereanydeal.com/dev/app/](https://isthereanydeal.com/dev/app/).

All endpoints are under the base URL: `https://api.isthereanydeal.com`

Authentication is via an `?key=YOUR_API_KEY` query parameter on most endpoints (no OAuth needed for read-only price data).

| Endpoint | Method | Description |
|---|---|---|
| `/games/search/v1?title={q}&results=20` | GET | Search games by name. Returns ITAD game IDs, slugs, titles, and artwork URLs. |
| `/games/prices/v3?country={CC}&capacity=5` | POST | Get current prices per game across all covered stores. Body: array of ITAD game UUIDs. `country` = ISO 3166-1 alpha-2 (e.g. `ES`, `US`, `GB`). |
| `/games/overview/v2?country={CC}` | POST | Lighter endpoint: best current price + historical low per game. Ideal for list views. |
| `/games/historylows/v1?country={CC}` | POST | All-time historical low prices per game per country. |
| `/games/history/v2?id={uuid}&country={CC}` | GET | Price change log for a single game (last 3 months by default). |
| `/games/info/v2?id={uuid}` | GET | Full game metadata: release date, tags, developers, Metacritic score. |
| `/games/storelow/v2?country={CC}` | POST | Historical lowest price per store for each game. |
| `/deals/v2?country={CC}&sort=price&limit=50` | GET | Paginated list of current deals, sortable by price or cut %. |
| `/lookup/id/shop/{shopId}/v2` | POST | Resolve ITAD game IDs from store-native IDs (e.g. Steam appids). |

**Covered stores include** (partial list with shop IDs used in the `shops` filter parameter):
- Steam: `61`
- GOG: `35`
- Epic Games Store: `25`
- Humble Store: `5`
- Fanatical: `6`
- Green Man Gaming: `12`
- Nintendo eShop: `45` *(limited coverage)*

**Rate limiting:** The API is rate-limited; exact values are returned in response headers (`X-RateLimit-Remaining`, `X-RateLimit-Reset`). For a personal single-user app, staying within limits by caching responses in SQLite is straightforward.

**Country support:** Any ISO 3166-1 alpha-2 country code. The API automatically returns prices in the local currency for that country.

---

### 1.2 Steam Store API (Direct)

Steam exposes an unofficial but stable price endpoint that has been publicly used for years.

| Endpoint | Method | Description |
|---|---|---|
| `https://store.steampowered.com/api/appdetails?appids={appid}&cc={CC}&filters=price_overview` | GET | Returns current price, discount %, and formatted price string for a Steam app in any country. No key required. |
| `https://store.steampowered.com/api/featuredcategories?cc={CC}` | GET | Returns featured/on-sale games for a given country. |

**Usage note:** Steam's API has no published rate limit but throttles aggressive crawling. For a personal app fetching on demand, it is entirely safe. The `cc` parameter accepts ISO 3166-1 alpha-2 codes.

**Example response** for `appid=292030` (The Witcher 3), `cc=ES`:
```json
{
  "292030": {
    "success": true,
    "data": {
      "price_overview": {
        "currency": "EUR",
        "initial": 2999,
        "final": 599,
        "discount_percent": 80,
        "initial_formatted": "29,99€",
        "final_formatted": "5,99€"
      }
    }
  }
}
```

---

### 1.3 Nintendo eShop API (Unofficial)

Nintendo exposes a price lookup endpoint used by multiple well-known community apps. No authentication is required.

| Endpoint | Method | Description |
|---|---|---|
| `https://api.ec.nintendo.com/v1/price?country={CC}&lang=en&ids={nsuid1},{nsuid2}` | GET | Returns current price, discount, and sale end date for Nintendo Switch game NSUIDs in any country. |

**Country support:** 40+ countries including `ES`, `US`, `GB`, `DE`, `FR`, `JP`, `AU`, etc.

**Getting NSUIDs:** The ITAD `/lookup/id/shop/45/v2` endpoint can resolve ITAD game IDs to Nintendo NSUIDs. Alternatively, the community-maintained npm package `nintendo-eshop-api` provides a full catalog with NSUIDs.

**Example response:**
```json
{
  "prices": [
    {
      "title_id": 70010000012332,
      "sales_status": "onsale",
      "regular_price": { "amount": "59.99", "currency": "EUR", "raw_value": "59.99" },
      "discount_price": {
        "amount": "29.99",
        "currency": "EUR",
        "raw_value": "29.99",
        "end_datetime": "2026-05-01T06:59:59Z"
      }
    }
  ]
}
```

---

### 1.4 CheapShark API

CheapShark is a free, no-auth API focused on PC game deals across stores including Steam, GOG, Fanatical, and Green Man Gaming.

| Endpoint | Method | Description |
|---|---|---|
| `https://www.cheapshark.com/api/1.0/games?title={q}&limit=20` | GET | Search games by title. Returns game IDs, titles, cheapest price, and Steam app ID. |
| `https://www.cheapshark.com/api/1.0/deals?gameID={id}&sortBy=Price` | GET | All current deals for a game across covered stores, sorted by price. |
| `https://www.cheapshark.com/api/1.0/stores` | GET | List of all covered stores with names and IDs. |

**Limitation:** CheapShark is US-centric and does not support multi-country pricing. It serves as a complementary source for deal discovery on PC platforms only.

---

### 1.5 API Coverage Summary

| Store | Free API | Multi-Country | Source |
|---|---|---|---|
| Steam | ✅ Yes (official) | ✅ Yes (~50+ countries) | Steam Store API |
| GOG | ✅ Via ITAD | ✅ Yes | ITAD |
| Epic Games Store | ✅ Via ITAD | ✅ Yes | ITAD |
| Nintendo eShop | ✅ Yes (unofficial) | ✅ Yes (40+ countries) | Nintendo ec API |
| PlayStation Store | ⚠️ Via ITAD (limited) | ⚠️ Limited | ITAD |
| Humble Store | ✅ Via ITAD | ✅ Yes | ITAD |
| Fanatical | ✅ Via ITAD | ✅ Yes | ITAD |
| Xbox / Microsoft | ❌ No free API | ❌ No | — |

---

## 2. Tech Stack

### 2.1 Architecture Overview

The application follows a two-tier architecture: a **Node.js backend** running locally or on a personal VPS, and an **Expo (React Native + Web) frontend** that connects to it. There is no Redis layer — price data is cached directly in SQLite with a TTL timestamp column. Since only one user accesses the service, SQLite's single-writer model is not a constraint.

```
┌─────────────────────────────────────────────────┐
│                   Expo App                      │
│     iOS  ·  Android  ·  Web (same codebase)     │
└────────────────────┬────────────────────────────┘
                     │ HTTP (local network / VPS)
┌────────────────────▼────────────────────────────┐
│             Fastify API Server (Node.js)         │
│  ┌──────────────────────────────────────────┐   │
│  │  Price Aggregation Layer                 │   │
│  │  ITAD · Steam Store · Nintendo eShop     │   │
│  └─────────────────┬────────────────────────┘   │
│  ┌──────────────────▼────────────────────────┐  │
│  │  SQLite (via Prisma)                      │  │
│  │  games · prices · favorites · cache_meta  │  │
│  └───────────────────────────────────────────┘  │
└─────────────────────────────────────────────────┘
```

---

### 2.2 Frontend — Expo + React Native

| Concern | Library / Tool | Notes |
|---|---|---|
| Framework | **Expo SDK 52** + **Expo Router v4** | File-based routing, works on iOS, Android, and Web from one codebase |
| Language | **TypeScript** | Strict mode enabled |
| Styling | **NativeWind v4** | Tailwind CSS utility classes on React Native and Web |
| Server state | **TanStack Query v5** | Caching, background refetch, loading/error states |
| Client state | **Zustand v5** | Lightweight store for favorites list and selected country |
| Persistence | **AsyncStorage** + Zustand `persist` middleware | Stores favorites and user preferences (country selection) locally on device |
| Icons | **Lucide React Native** | Consistent icon set across platforms |
| Navigation | **Expo Router** with tab + stack navigators | Bottom tab bar on mobile, top nav on web |
| Build & OTA | **EAS (Expo Application Services)** | For iOS/Android production builds |

**Project structure:**
```
app/
├── (tabs)/
│   ├── index.tsx          # Home / Search screen
│   ├── favorites.tsx      # Favorites screen
│   └── settings.tsx       # Country selector, theme toggle
├── game/[id].tsx          # Game detail screen (all prices per store)
└── _layout.tsx            # Root layout, TanStack Query provider
components/
├── GameCard.tsx           # Game list card with best price badge
├── FavoritesSection.tsx   # Highlighted favorites strip
├── PriceTable.tsx         # Per-store price breakdown
├── CountryPicker.tsx      # Country selector modal
└── SortBar.tsx            # Sort controls (price asc/desc, discount)
store/
└── useFavoritesStore.ts   # Zustand store: favorites[] + selectedCountry
hooks/
└── useGamePrices.ts       # TanStack Query hooks wrapping backend calls
```

---

### 2.3 Backend — Node.js + Fastify

| Concern | Library / Tool | Notes |
|---|---|---|
| Runtime | **Node.js 22 LTS** | Or Bun as a drop-in for faster cold starts |
| Framework | **Fastify v5** | Fast, low-overhead HTTP server with schema validation |
| Language | **TypeScript** | Shared types with frontend via a `/shared` package |
| ORM | **Prisma v6** | Type-safe access to SQLite; handles migrations |
| Database | **SQLite** (via Prisma) | Single file, zero infrastructure, sufficient for personal use |
| Validation | **Zod** | Request/response schema validation |
| HTTP client | **ky** (or native `fetch`) | Lightweight client for calling ITAD, Steam, Nintendo APIs |
| Scheduling | **node-cron** | Periodic price refresh jobs (every 6 hours for favorited games) |
| Environment | **dotenv** | API keys stored in `.env`, never committed |

**Backend API routes:**

| Route | Description |
|---|---|
| `GET /api/search?q={title}&country={CC}` | Search games by title, returns results with best price per game |
| `GET /api/game/:id/prices?country={CC}` | Full price breakdown for one game across all stores |
| `GET /api/game/:id/info` | Game metadata (artwork, tags, release date) |
| `GET /api/favorites/prices?country={CC}` | Bulk price fetch for all favorited game IDs |
| `GET /api/countries` | List of supported country codes and currency symbols |

The backend never forwards raw third-party API keys to the client. All external API calls are server-side only.

---

### 2.4 Database Schema (SQLite via Prisma)

```prisma
model Game {
  id          String   @id          // ITAD UUID
  slug        String
  title       String
  type        String                // "game" | "dlc"
  boxart      String?
  banner300   String?
  steamAppid  Int?
  nintendoId  String?
  tags        String?               // JSON array stored as string
  updatedAt   DateTime @updatedAt

  prices      Price[]
}

model Price {
  id          Int      @id @default(autoincrement())
  gameId      String
  game        Game     @relation(fields: [gameId], references: [id])
  shop        String                // "Steam", "GOG", "Nintendo eShop", etc.
  country     String                // ISO 3166-1 alpha-2
  amount      Float                 // Current price
  currency    String
  regularAmt  Float                 // Full (non-discounted) price
  cutPct      Int                   // Discount percentage 0–100
  historyLow  Float?                // All-time low for this store+country
  storeUrl    String?
  fetchedAt   DateTime @default(now())

  @@unique([gameId, shop, country])
  @@index([gameId, country])
}
```

**Cache TTL strategy (no Redis):** Each `Price` row carries a `fetchedAt` timestamp. The backend checks this before deciding whether to call external APIs: if `fetchedAt` is older than 6 hours, it re-fetches; otherwise it returns the cached row. This is implemented as a simple conditional in the service layer — no additional infrastructure needed.

---

### 2.5 Deployment (Personal VPS or Local)

```
# Install dependencies
npm install

# Set up environment
cp .env.example .env
# Fill in: ITAD_API_KEY, PORT, DATABASE_URL=file:./dev.db

# Run migrations
npx prisma migrate deploy

# Start backend
npm run start:server     # http://localhost:3000

# Start Expo (mobile + web)
npm run start:app
# Scan QR code with Expo Go on phone
# Web: http://localhost:8081
```

For VPS deployment, the backend runs under **PM2** (`pm2 start dist/server.js --name gametracker`) with a reverse proxy via **Caddy** for HTTPS if a domain is available. The Expo web build is served as static files from the same Caddy config.

---

## 3. Product & Design Requirements

### 3.1 Screens

#### Home / Search Screen
- **Default state:** Shows a paginated list of current deals from ITAD (`/deals/v2`), sorted by highest discount by default
- **Search:** A prominent search bar at the top; typing triggers `GET /api/search?q=` with debounce (300ms). Results replace the deals list while a query is active
- **Sort controls:** A compact sort bar below the search field with options: *Best Price*, *Biggest Discount*, *A–Z*. Tapping a sort option re-sorts the visible list
- **Country selector:** A compact pill/badge in the top-right showing the active country flag + code (e.g. 🇪🇸 ES). Tapping opens the Country Picker modal
- **Empty state:** If no search results, show a friendly message: *"No results for '[query]'. Try a different spelling."* with a clear button

#### Favorites Screen
- Accessible via the bottom tab bar (star icon)
- Shows **only** games the user has marked as favorites
- Same card layout as the search/home screen
- Empty state: animated star icon + *"Tap the ★ on any game to save it here"*

#### Game Detail Screen
- Triggered by tapping any game card
- Top section: game artwork banner, title, developer, release year, Metacritic score (from ITAD game info)
- **Price breakdown table:** One row per store showing store name + logo, current price, discount badge, all-time low, and a "Buy" link button
- Rows are sorted by current price ascending by default
- Favorite toggle button (star) in the top-right of the header
- A "History Low" callout badge highlights if the current price equals the all-time low

#### Settings Screen
- **Country selector:** Searchable list of all supported countries. Changing country immediately invalidates the price cache and triggers a refresh
- **Theme toggle:** Light / Dark / System
- **Clear cache:** Button to force re-fetch all prices from the APIs
- **About:** App version, link to ITAD (required by their terms of service)

---

### 3.2 Favorites — Highlighted Area

On the **Home screen**, if the user has any favorites, they appear in a dedicated horizontal scrollable strip above the main listing. This strip has:
- A section header: *"Your Watchlist"* with a star icon
- Compact horizontal cards showing game thumbnail, title, and current best price
- A gold/amber background tint to distinguish it visually from the main list
- A "See all" link that navigates to the Favorites tab

On the **Favorites tab**, cards are displayed in a full vertical list, identical in size to the main listing.

---

### 3.3 Game Card Design

Each card in the main listing contains:
- **Left:** Game box art thumbnail (80×80px)
- **Center:** Game title (bold, 1 line truncated), store badges (small platform icons: Steam, GOG, Nintendo, Epic), lowest available price with currency
- **Right:** Discount badge (e.g. `-75%`) in green if on sale; no badge if full price
- **Tap:** Opens Game Detail screen
- **Long press / swipe action (mobile):** Adds/removes from favorites
- **Favorite indicator:** Small star icon overlaid on box art corner, filled gold if favorited

---

### 3.4 Country Selection & Currency

- The app defaults to the device locale on first launch (detected via `expo-localization`)
- User can override in Settings at any time
- All prices are displayed in the local currency for the selected country, using the currency code returned by the APIs (e.g. EUR, USD, GBP, JPY)
- Currency is always shown with the amount (e.g. `€9.99` or `$14.99`), never assumed

---

### 3.5 Non-Functional Requirements

| Requirement | Target |
|---|---|
| Search response time | < 1 second for cached results; < 3 seconds for fresh API fetch |
| Price freshness | Maximum 6 hours old; favorites refreshed every 6h via cron |
| Offline behaviour | Show last-cached prices with a "Last updated X hours ago" indicator |
| Accessibility | All interactive elements ≥ 44×44px touch target; screen reader labels on all icons |
| Dark mode | Full dark mode support via NativeWind dark: variant + system preference detection |
| Mobile OS support | iOS 16+ and Android 10+ (Expo SDK 52 targets) |

---

### 3.6 Out of Scope (MVP)

- User authentication and server-side favorites sync
- Push notifications for price drops
- Price drop history charts
- Xbox / Microsoft Store pricing (no free API available)
- Social features (sharing deals)

---

## 4. Development Phases

### Phase 1 — MVP (Local machine)
- Backend: Fastify + Prisma + SQLite with ITAD and Steam APIs wired up
- Frontend: Expo app with Search, Game Detail, and Favorites screens
- Country selector defaulting to device locale
- Favorites persisted in AsyncStorage

### Phase 2 — Nintendo + Polish
- Integrate Nintendo eShop API for Switch pricing
- Price history log on Game Detail screen (line chart using Victory Native)
- Improve search relevance with fuzzy client-side filter on cached results

### Phase 3 — VPS Deploy
- PM2 + Caddy setup for always-on VPS hosting
- EAS build for installable iOS/Android app pointing to VPS
- Automated 6-hour cron job for refreshing favorites prices
