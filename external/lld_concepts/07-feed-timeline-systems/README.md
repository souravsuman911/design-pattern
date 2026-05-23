# Feed and Timeline Systems

## Problem Shape
Generate a personalized ordered list of content.
Examples: Facebook feed, Twitter timeline, Instagram feed, YouTube home, recommendations.

## Core Model
- **Producer**: Creates content. Example: followed user.
- **Content**: Post/video/job/product.
- **Relationship**: Follow, subscribe, interest.
- **FeedItem**: Content shown to user.
- **Ranking**: Order of items.

## Deep Concepts With Compact Examples
- **Fanout on Write**: Push post to followers feed when created.
- **Fanout on Read**: Build feed when user opens app.
- **Hybrid Fanout**: Do not push celebrity posts to millions instantly.
- **Ranking**: Score by recency + relevance + engagement.
- **Cursor Pagination**: Continue from last seen item/time.
- **Privacy/Delete Handling**: Remove hidden/deleted content.

## Feed Generation Options
- **Push Model**: Fast reads, expensive writes.
- **Pull Model**: Cheap writes, slower reads.
- **Hybrid Model**: Push normal users, pull celebrities.
- **Precomputed Cache**: Store top feed items per user.
- **Real-Time Merge**: Merge fresh content at read time.

## Interview Questions: Short Answers
- **Generate feed?** Pull, push, or hybrid based on scale.
- **Celebrity users?** Use pull path for celebrity content.
- **Pagination?** Use cursor, not offset.
- **Ads/recommendations?** Merge ranked candidates into feed.
- **Deleted post?** Filter at read and remove from cached feed.

## Implementation Checklist
- Define feed source graph.
- Pick push/pull/hybrid.
- Add ranking strategy.
- Use cursor pagination.
- Cache hot timelines.
- Handle delete/privacy filtering.
