# Rate Limiting and Throttling

## Problem Shape
Limit how often a client can perform an action.
Examples: API rate limiter, OTP limiter, login attempt limiter, message sending limit.

## Core Model
- **Client Key**: Who is limited. Example: `userId`, `ip`, `apiKey`.
- **Rule**: Allowed count/window. Example: `100 requests/min`.
- **Counter/Bucket**: Usage tracker.
- **Decision**: `ALLOW`, `REJECT`, or `RETRY_AFTER`.

## Deep Concepts With Compact Examples
- **Fixed Window**: Count per minute. Example: 100 requests from `10:00-10:01`.
- **Sliding Window**: More accurate rolling window. Example: last 60 seconds.
- **Token Bucket**: Tokens refill over time; bursts allowed.
- **Leaky Bucket**: Requests processed at steady rate.
- **Distributed Limit**: Shared counter across servers.

## Algorithm Options
- **Fixed Window Counter**: Easiest, but boundary burst issue.
- **Sliding Window Log**: Accurate, but stores timestamps.
- **Sliding Window Counter**: Balanced accuracy and memory.
- **Token Bucket**: Best for burst-friendly APIs.
- **Leaky Bucket**: Best for smoothing traffic.

## Interview Questions: Short Answers
- **Which algorithm?** Token bucket for common API limits.
- **Distributed system?** Use Redis atomic counters/Lua.
- **Different plans?** Store rules per user/plan/API.
- **Memory cleanup?** Use TTL on keys.
- **Return to client?** Send remaining quota and retry-after.

## Implementation Checklist
- Define rate limit key.
- Add `RateLimitRule`.
- Implement algorithm strategy.
- Make counter update atomic.
- Add TTL cleanup.
- Return allow/reject decision.
