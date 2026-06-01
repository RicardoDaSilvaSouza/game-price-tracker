## 2026-04-18‑GamePriceTracker‑BE‑Setup‑Detailed‑Design

### Overview

The backend of the GamePriceTracker is a  and a **DDD + Hexagonal Architecture** with **Clean code** consisting of a **Node.js** Fastify server and a **SQLite** database accessed through **Prisma**. The backend is split into three logical layers:

1. **Core (Domain)** – pure business logic – services, entities, and repository interfaces.
2. **Web (Presentation)** – Fastify route handlers and middleware.
3. **Infrastructure (Persistence & External APIs)** – Prisma client, external HTTP clients (ky), and cron jobs.

### 1. Technology Stack

| Layer | Technology | Version | Purpose |
|------|-----------|--------|--------|
| Runtime | Node.js | 22 LTS | Core runtime |
| Framework | Fastify | 5 | HTTP server |
| Language | TypeScript | Latest | Shared types |
| ORM | Prisma | 6 | SQLite ORM |
| Database | SQLite | Latest | Single‑file data store |
| Validation | Zod | Latest | Input/response schema |
| HTTP Client | ky | Latest | Calls to external APIs |
| Scheduler | node‑cron | Latest | Price refresh jobs |
| Env | dotenv | Latest | API keys |

### 2. Project Structure

```
backend/
├── src/
│   ├── server.ts          # Fastify entry
│   ├── routes/
│   │   ├── search.ts      # GET /api/search
│   │   ├── game.ts      # GET /api/game/:id
│   │   ├── prices.ts    # GET /api/game/:id/prices
│   │   ├── favorites.ts   # GET /api/favorites/prices
│   │   └── countries.ts    # GET /api/countries
│   ├── services/
│   │   ├── priceAggregator.ts
│   │   ├── itadService.ts
│   │   ├── steamService.ts
│   │   └── nintendoService.ts
│   ├── models/
│   │   ├── game.ts
│   │   └── price.ts
│   └── db/
│       └── database.ts  # Prisma client
├── prisma/
│   └── schema.prisma
├── .env.example
├── tsconfig.json
└── package.json
```

### 3. Docker‑Compose for Backend‑Only

The Docker‑Compose snippet added to the BE spec contains only the backend‑specific configuration – ports, volumes, environment variables, and command. The snippet is:

```
services:
  backend:
    build:
      context: .
      dockerfile: Dockerfile
      target: development
    container_name: gamepricetracker
    ports:
      - "3000:3000"    # API port
    environment:
      - NODE_ENV=development
      - PORT=3000
      - ITAD_API_KEY=${ITAD_API_KEY}
      - DATABASE_URL=file:./data/gamepricetracker.db
    volumes:
      - ./data:/app/data
      - ./backend:/app/node_modules
      - ./src:/app/src
      - ./prisma:/app/prisma
    working_dir: /app
    command: npm run dev
    restart: unless-stopped
```

### 4. Verification Checklist

- [ ] Node.js 22 LTS or Bun installed
- [ ] npm dependencies installed
- [ ] Prisma generated and migrated
- [ ] Server starts at http://localhost:3000
- [ ] TypeScript compiles with no errors

### 5. Notes

- The backend uses a `/shared` package for shared types.
- External API keys are stored in .env and never exposed to clients.
- Price data is cached directly in SQLite with a TTL timestamp.
- The backend never exposes raw third‑party API keys to the client.
- The backend is the sole component that accesses the database.

---
