# Api Gateway Throttling LLD

Implementation: `ApiGatewayThrottlingClient.java`

## Category
Rate Limiting and Throttling

## Scope
Interview-ready low-level design for **Api Gateway Throttling**. The code is compact but includes entities, statuses, services, storage, concurrency protection, and one executable happy path.

## Functional Requirements
- Model the main domain entities and request flow.
- Execute the primary happy path.
- Validate unavailable resources or invalid state.
- Keep lifecycle state explicit using enums.
- Provide extension points using strategy/service boundaries.

## Non-Functional Requirements
- **Consistency**: All writes go through service methods.
- **Concurrency**: Critical shared state uses locks, atomics, concurrent maps, or queues.
- **Idempotency**: Retry-prone APIs should use an idempotency key in production.
- **Scalability**: Reads can be indexed/cached; writes should partition by entity key.
- **Fault tolerance**: Side effects like notifications should be async and retryable.

## Core Design
```text
Client -> Service -> Repository/Store -> Domain Entity
               |-> Strategy / Matcher / Validator / Notifier
```

## Main Flow
```text
Request received
 -> validate input
 -> check current state/conflict
 -> lock or atomically update shared state
 -> change status
 -> persist result
 -> publish optional event/notification
```

## Important Concepts
- State machine and valid transitions.
- Race condition location and concurrency protection.
- Request validation and duplicate prevention.
- Failure handling and retry behavior.
- Strategy pattern for algorithms that can change.
- Repository pattern for replacing in-memory storage with DB.

## Production Notes
- Replace maps with DB tables and transactions.
- Add unique constraints for idempotency and duplicate prevention.
- Add indexes on owner, status, time, and search fields.
- Use queue/outbox for notifications and external side effects.
- Add audit/history tables for important state changes.

## Common Interview Questions
### What are the main entities?
Mention the primary resource, user/requester, lifecycle entity, status enum, and service.

### Where can concurrency fail?
Shared resource assignment, stock update, state transition, ledger transfer, or rate counter update.

### How is duplicate processing avoided?
Use idempotency key plus a unique constraint or atomic map insertion.

### How does this scale?
Cache reads, index common queries, partition by entity key, and keep critical writes transactional.

### Which patterns are used?
Service layer, repository, state machine, strategy, DTO/request object, observer/outbox where needed.

## Implementation Checklist
- Read enums first.
- Trace the service method used in `main`.
- Identify the critical section.
- Explain the chosen data structures.
- Discuss DB schema and production constraints verbally.
