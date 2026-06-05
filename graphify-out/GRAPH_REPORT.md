# Graph Report - game-price-tracker  (2026-06-06)

## Corpus Check
- 18 files · ~25,940 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 72 nodes · 76 edges · 8 communities detected
- Extraction: 93% EXTRACTED · 7% INFERRED · 0% AMBIGUOUS · INFERRED: 5 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]
- [[_COMMUNITY_Community 6|Community 6]]
- [[_COMMUNITY_Community 7|Community 7]]

## God Nodes (most connected - your core abstractions)
1. `Currency` - 6 edges
2. `g()` - 5 edges
3. `getNthColumn()` - 5 edges
4. `enableUI()` - 5 edges
5. `CurrencyTest` - 4 edges
6. `ApplicationSmokeTest` - 4 edges
7. `makeCurrent()` - 4 edges
8. `Q()` - 4 edges
9. `D()` - 4 edges
10. `y()` - 4 edges

## Surprising Connections (you probably didn't know these)
- None detected - all connections are within the same source files.

## Communities

### Community 0 - "Community 0"
Cohesion: 0.27
Nodes (11): addSortIndicators(), enableUI(), getNthColumn(), getTable(), getTableBody(), getTableHeader(), loadColumns(), loadData() (+3 more)

### Community 1 - "Community 1"
Cohesion: 0.35
Nodes (8): a(), B(), D(), g(), i(), k(), Q(), y()

### Community 2 - "Community 2"
Cohesion: 0.32
Nodes (2): Currency, CurrencyTest

### Community 3 - "Community 3"
Cohesion: 0.4
Nodes (2): GamePrice, GamePriceTest

### Community 4 - "Community 4"
Cohesion: 0.4
Nodes (1): ApplicationSmokeTest

### Community 5 - "Community 5"
Cohesion: 0.7
Nodes (4): goToNext(), goToPrevious(), makeCurrent(), toggleClass()

### Community 6 - "Community 6"
Cohesion: 0.5
Nodes (1): Currency

### Community 7 - "Community 7"
Cohesion: 0.67
Nodes (1): GamePrice

## Knowledge Gaps
- **Thin community `Community 2`** (8 nodes): `Currency.kt`, `CurrencyTest.kt`, `Currency`, `.isValid()`, `CurrencyTest`, `.`is valid when amount is positive`()`, `.`throws when amount is negative`()`, `.`throws when amount is zero`()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 3`** (5 nodes): `GamePrice.kt`, `GamePriceTest.kt`, `GamePrice`, `GamePriceTest`, `.`accepts a valid Currency object`()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 4`** (5 nodes): `ApplicationSmokeTest`, `.`Flyway applies V1 migration`()`, `.getProperties()`, `.`Micronaut context starts`()`, `ApplicationSmokeTest.kt`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 6`** (4 nodes): `currency.ts`, `Currency`, `.constructor()`, `.isValid()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 7`** (3 nodes): `GamePrice`, `.constructor()`, `game_price.ts`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Currency` connect `Community 2` to `Community 3`?**
  _High betweenness centrality (0.019) - this node is a cross-community bridge._
- **Are the 4 inferred relationships involving `Currency` (e.g. with `.`accepts a valid Currency object`()` and `.`throws when amount is zero`()`) actually correct?**
  _`Currency` has 4 INFERRED edges - model-reasoned connections that need verification._