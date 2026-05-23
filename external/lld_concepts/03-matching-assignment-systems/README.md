# Matching and Assignment Systems

## Problem Shape
A requester is matched with a provider based on availability, location, skill, priority, or capacity.
Examples: cab booking, delivery partner, doctor assignment, support ticket routing.

## Core Model
- **Requester**: Person/system needing service. Example: rider.
- **Provider**: Person/resource providing service. Example: driver.
- **Request**: Demand to fulfill. Example: ride from A to B.
- **Candidate List**: Eligible providers.
- **Assignment**: Final selected provider.

## Deep Concepts With Compact Examples
- **Filtering**: Remove unavailable providers. Example: driver offline => not candidate.
- **Ranking**: Sort candidates. Example: nearest driver first.
- **Reservation/Locking**: Avoid assigning same provider twice.
- **Acceptance Timeout**: Driver has `30 sec` to accept.
- **Fallback**: If rejected, offer next provider.
- **Fairness**: Avoid always assigning same top-rated provider.

## Matching Options
- **Nearest First**: Best for cab/delivery.
- **Skill Based**: Best for doctors/support agents.
- **Load Based**: Assign to least busy provider.
- **Priority Queue**: Best when requests have urgency.
- **Scoring Strategy**: Weighted score = distance + rating + cost + SLA.

## Interview Questions: Short Answers
- **Find nearest provider?** Filter available providers and rank by distance.
- **Provider rejects?** Mark rejected and retry next candidate.
- **Avoid double assignment?** Lock provider during assignment.
- **Multiple strategies?** Use `MatchingStrategy` interface.
- **Provider goes offline?** Cancel assignment and rematch.

## Implementation Checklist
- Define `Request`, `Provider`, `Assignment`.
- Model provider availability.
- Add matching strategy.
- Lock provider while assigning.
- Add timeout/retry flow.
- Track assignment history.
