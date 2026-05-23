# Problem Implementation Reference

Use `external/lld/EventBookingSystem` as the reference style for creating similar LLD problem files under `external/lld_concepts`.

## Reference Files
- Code reference: `external/lld/EventBookingSystem/EventBookingSystemClient.java`
- Documentation reference: `external/lld/EventBookingSystem/readme.md`

## Expected Code Structure
For each implemented LLD problem, prefer this simple interview-friendly structure:

- **Enums**: Define statuses and types first.
  - Example: `SeatStatus`, `BookingStatus`, `PaymentStatus`.
- **Domain Models**: Keep core entities clear and small.
  - Example: `User`, `Event`, `ShowSeat`, `SeatHold`, `Booking`.
- **Request DTOs**: Separate API input from domain objects.
  - Example: `ReserveSeatsRequest`, `ConfirmBookingRequest`.
- **Controllers**: Thin layer that delegates to services.
- **Services**: Hold business logic and state transitions.
- **Repositories**: Hide storage details, even if in-memory.
- **Client/Main**: Demonstrate one happy path clearly.

## Expected README Structure
Each problem README should cover:

- **Scope**: What the system handles.
- **Functional Requirements**: Core use cases.
- **Non-Functional Requirements**: Concurrency, consistency, scalability, fault tolerance.
- **APIs**: Simple endpoint-style examples.
- **Entities**: Important fields and responsibilities.
- **Status Models**: State transitions.
- **Main Flows**: Happy path, failure path, cancellation/expiry path if applicable.
- **Concurrency Handling**: Explain race condition and chosen lock/transaction strategy.
- **HLD Notes**: Components, storage, cache, queue, background workers if relevant.
- **Database Schema**: Tables, indexes, and important constraints for production mapping.
- **Design Patterns Used**: Service, repository, DTO, state, strategy, idempotency, etc.
- **Interview Questions**: Short answers for common interviewer follow-ups.

## Minimum Logical Concepts To Include
Every serious LLD implementation should explicitly mention or model:

- **State transitions**: Valid statuses and allowed movements.
- **Concurrency safety**: Locking, atomic update, DB constraint, or optimistic versioning.
- **Idempotency**: Especially for retry-prone operations like payment, booking, order creation.
- **Validation**: Required fields, duplicate input, invalid state, unavailable resource.
- **Failure handling**: Payment failure, expiry, cancellation, retry, partial failure where relevant.
- **Extensibility**: Strategy interfaces or service boundaries for future requirements.

## Java Implementation Style
Keep Java code simple and interview-readable:

- Use one file initially if the problem is for study/demo.
- Prefer clear class names over over-engineering.
- Use `ConcurrentHashMap` for in-memory thread-safe stores.
- Use `ReentrantLock` for explicit critical sections.
- Use `Semaphore` when limiting total capacity.
- Use `BlockingQueue`, `DelayQueue`, or `ScheduledExecutorService` for async/expiry flows when needed.
- Use enums for simple state machines.
- Use strategy interfaces when algorithms vary.

## Quality Checklist Before Marking Complete
- Can the main race condition be explained clearly?
- Are all important statuses modeled?
- Is the happy path demonstrated in `main`?
- Is at least one failure path handled?
- Are duplicate/retry cases handled where needed?
- Does README explain production alternatives?
- Are interview questions answered shortly?

## Reference Principle
The goal is not production-complete code. The goal is interview-ready LLD that shows:

```text
Clear entities + controlled state transitions + concurrency safety + clean extensibility.
```
