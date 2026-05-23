# Search Filtering and Ranking

## Problem Shape
Find matching entities using filters, sorting, ranking, and pagination.
Examples: hotel search, product search, cab search, job search, event search.

## Core Model
- **SearchCriteria**: User query object.
- **Filter**: Restricts results. Example: price < 1000.
- **Sort**: Deterministic order. Example: price low to high.
- **Ranking**: Score-based order. Example: relevance score.
- **Page/Cursor**: Return results in chunks.

## Deep Concepts With Compact Examples
- **Composable Filters**: Combine city + price + rating.
- **Specification Pattern**: Each filter is reusable predicate.
- **Ranking Strategy**: Score = relevance + popularity + freshness.
- **Pagination**: Cursor avoids duplicate/missing rows.
- **Facets**: Show counts. Example: `4-star hotels: 20`.
- **Indexing**: Precompute searchable fields.

## Search Options
- **Database Query**: Good for simple filters.
- **In-Memory Filter**: Good for small demo.
- **Search Index**: Good for text search and scale.
- **Geo Search**: Use distance filter/ranking.
- **Cursor Pagination**: Best for changing result sets.

## Interview Questions: Short Answers
- **Add new filter?** Add new `Filter`/specification implementation.
- **Sorting/ranking?** Keep ranking behind strategy.
- **Pagination?** Prefer cursor over offset.
- **Location search?** Filter by radius and rank by distance.
- **Scale search?** Use indexes and cache popular queries.

## Implementation Checklist
- Create `SearchCriteria`.
- Add independent filters.
- Add sort/ranking strategy.
- Return paginated result.
- Keep storage/query separate.
- Add indexes for common filters.
