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

**Rate limiting:** The API is rate-limited; exact values are returned in response headers (`X-RateLimit-Remaining`, `X-RateLimit-Reset`). For a personal single-user app, staying within limits by caching responses in Postgres is straightforward.

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

The application follows a two-tier architecture: a **Kotlin + Micronaut backend** running locally or on a personal VPS, and an **Expo (React Native + Web) frontend** that connects to it. The backend talks to a **PostgreSQL 18** database. There is no Redis layer — price data is cached directly in Postgres with a TTL timestamp column. Since only one user accesses the service, contention is a non-issue.

The backend is split into four Gradle modules along clean-architecture boundaries:

| Module | Responsibility |
|---|---|
| `core` | Pure-Kotlin domain model (entities, value objects, ports). Zero framework dependencies. |
| `web` | HTTP controllers, DTOs, request/response mapping. Depends on `core`. |
| `infra` | Adapters: persistence (Hibernate/JDBC), outbound HTTP clients for ITAD/Steam/Nintendo. Depends on `core`. |
| `application` | Composition root: Micronaut `main`, Flyway migrations, `application.yml`. Depends on `core`, `web`, `infra`. |

```
┌─────────────────────────────────────────────────┐
│                   Expo App                      │
│     iOS  ·  Android  ·  Web (same codebase)     │
└────────────────────┬────────────────────────────┘
                     │ HTTP (local network / VPS)
┌────────────────────▼────────────────────────────┐
│        Micronaut 4 API Server (Kotlin / JVM 21)  │
│  ┌──────────────────────────────────────────┐   │
│  │  application  (main · config · Flyway)   │   │
│  └──────┬─────────────┬─────────────┬───────┘   │
│         │             │             │           │
│  ┌──────▼─────┐ ┌─────▼──────┐ ┌────▼──────┐    │
│  │    web     │ │    core    │ │   infra   │    │
│  │ controllers│ │   domain   │ │ persistence│    │
│  └────────────┘ └────────────┘ │ + clients  │    │
│                                └─────┬──────┘    │
│  ┌───────────────────────────────────▼───────┐  │
│  │  PostgreSQL 18 (Hikari + Flyway)          │  │
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

### 2.3 Backend — Kotlin + Micronaut

| Concern | Library / Tool | Notes |
|---|---|---|
| Language | **Kotlin 2.3.x** | Targeting JVM 21 (Micronaut 4 cliff: 5.x requires JVM 25) |
| Runtime | **JVM 21 (Temurin)** | Resolved via Gradle toolchains + foojay-resolver |
| Build | **Gradle 8.x (Kotlin DSL)** | Multi-module: `core`, `web`, `infra`, `application` |
| Framework | **Micronaut 4.10.x** | Netty runtime, JSR-330 DI, compile-time AOT |
| Annotation processing | **kapt** | For Micronaut bean / config processors |
| HTTP server | **Micronaut Netty** (`runtime("netty")`) | Reactive, low-overhead |
| JSON | **Jackson** (`micronaut-jackson-databind`) | Required by Netty for error responses and DTO bind |
| Validation | **Bean Validation (Jakarta Validation 3)** | Wired via Micronaut Validation |
| Database | **PostgreSQL 18** | Run locally via `docker-compose`, in prod via VPS |
| Connection pool | **HikariCP** (`micronaut-jdbc-hikari`) | Default datasource named `default` |
| Migrations | **Flyway** (`micronaut-flyway` + `flyway-database-postgresql`) | SQL migrations under `application/src/main/resources/db/migration` |
| ORM / Persistence | **Hibernate JPA** (`micronaut-data-hibernate-jpa`) — *deferred until first `@Entity`* | Currently absent from `application` deps; will be re-added with `micronaut-data-processor` (kapt) when the first JPA entity is introduced |
| HTTP client | **Micronaut HTTP Client** (declarative `@Client`) | For ITAD / Steam / Nintendo eShop |
| Scheduling | **`@Scheduled`** (Micronaut) | Periodic price refresh jobs (every 6 hours for favourited games) |
| Logging | **SLF4J + Logback** | Logback config in `application/src/main/resources/logback.xml` |
| Tests | **JUnit 5**, **Kotest** matchers, **MockK**, **Micronaut Test (JUnit5)**, **Testcontainers (Postgres)** | Versions pinned in `gradle.properties` |

**Backend API routes:** (unchanged — routes are framework-agnostic)

| Route | Description |
|---|---|
| `GET /api/search?q={title}&country={CC}` | Search games by title, returns results with best price per game |
| `GET /api/game/{id}/prices?country={CC}` | Full price breakdown for one game across all stores |
| `GET /api/game/{id}/info` | Game metadata (artwork, tags, release date) |
| `GET /api/favorites/prices?country={CC}` | Bulk price fetch for all favourited game IDs |
| `GET /api/countries` | List of supported country codes and currency symbols |

The backend never forwards raw third-party API keys to the client. All external API calls are server-side only. API keys live in environment variables (or a non-committed `application-dev.yml`), not in code.

---

### 2.4 Database Schema (PostgreSQL via Flyway)

The schema is managed by **Flyway** SQL migrations under `game-price-tracker-be/application/src/main/resources/db/migration/`. The current state is a single placeholder migration `V1__init.sql` that creates a `schema_version_marker` table; the real domain tables land in Phase 1.

The intended Phase 1 schema (subject to refinement when the first `@Entity` is added) maps to:

```sql
-- Phase 1 target (illustrative, not yet committed)
CREATE TABLE game (
    id           VARCHAR(64) PRIMARY KEY,       -- ITAD UUID
    slug         TEXT        NOT NULL,
    title        TEXT        NOT NULL,
    type         TEXT        NOT NULL,          -- 'game' | 'dlc'
    boxart       TEXT,
    banner300    TEXT,
    steam_appid  INTEGER,
    nintendo_id  TEXT,
    tags         JSONB,
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE price (
    id           BIGSERIAL   PRIMARY KEY,
    game_id      VARCHAR(64) NOT NULL REFERENCES game(id) ON DELETE CASCADE,
    shop         TEXT        NOT NULL,          -- 'Steam', 'GOG', 'Nintendo eShop', ...
    country      CHAR(2)     NOT NULL,          -- ISO 3166-1 alpha-2
    amount       NUMERIC(12,2) NOT NULL,
    currency     CHAR(3)     NOT NULL,
    regular_amt  NUMERIC(12,2) NOT NULL,
    cut_pct      SMALLINT    NOT NULL,          -- 0..100
    history_low  NUMERIC(12,2),
    store_url    TEXT,
    fetched_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (game_id, shop, country)
);
CREATE INDEX price_game_country_idx ON price (game_id, country);
```

**Cache TTL strategy (no Redis):** each `price` row carries a `fetched_at` timestamp. The service layer checks it before deciding whether to call external APIs: if older than 6 hours, re-fetch; otherwise return the cached row.

---

### 2.5 Local Development & Deployment

**Local dev** — start Postgres via docker-compose, then run the Micronaut app:

```bash
cd game-price-tracker-be

# 1) Start Postgres 18 (host port 15432 → container 5432, picked to avoid
#    common 5432 conflicts on the host)
docker compose up -d

# 2) Run the full backend test suite (uses Testcontainers — random ephemeral
#    Postgres port, completely independent of the compose stack)
./gradlew test

# 3) Run the application
./gradlew :application:run     # serves on http://localhost:8080
```

The `application.yml` JDBC URL is `jdbc:postgresql://localhost:15432/gpt` to match the docker-compose port mapping.

**Testcontainers note:** the integration smoke test (`ApplicationSmokeTest`) spins up a fresh `postgres:18-alpine` container per run on a random host port, applies Flyway, and verifies the Micronaut context comes up. It is fully isolated from the docker-compose stack and works under OrbStack as well as Docker Desktop.

**VPS deployment** — build a runnable distribution and run under a process supervisor:

```bash
./gradlew :application:assemble        # produces application/build/distributions/*.tar
# Copy / extract on the VPS, then:
DATABASE_URL=jdbc:postgresql://localhost:5432/gpt \
  ./bin/application
```

Reverse-proxy with **Caddy** for HTTPS if a domain is available; the Expo web build is served as static files from the same Caddy config.

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
- Backend: Kotlin + Micronaut + Hibernate JPA + Postgres with ITAD and Steam APIs wired up
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
