# Event Booking System LLD

This document explains the low-level design for an event booking system similar to BookMyShow, Ticketmaster, or movie ticket booking platforms. The implementation is available in `EventBookingSystemClient.java`.

## Scope

The system allows users to search events, view showtimes, check seat availability, hold seats temporarily, confirm booking after payment, cancel bookings, and release expired holds.

## Functional Requirements Handled

1. **Search Events**
    - Search events by query, location, and date.
    - Fetch event details.
    - Fetch showtimes for an event.

2. **View Seats**
    - View all seats for a specific show.
    - Seat availability is maintained per showtime.
    - Same physical seat can have different status for different shows.

3. **Reserve / Hold Seats**
    - User can temporarily hold one or more seats.
    - Duplicate seat IDs in the same request are rejected.
    - Seat hold has an expiry time, currently 10 minutes.
    - Held seats cannot be reserved by another user.

4. **Confirm Booking**
    - User confirms booking using a valid hold ID and payment ID.
    - Payment is verified before seats are marked as booked.
    - Successful payment changes seats from `HELD` to `BOOKED`.

5. **Payment Failure Handling**
    - If payment fails, held seats are released.
    - Failed booking record is created for audit/debugging.

6. **Idempotent Confirmation**
    - Confirmation API accepts an idempotency key.
    - Repeated confirm requests with the same key return the same booking.
    - Prevents duplicate bookings during client/network retries.

7. **Cancel Booking**
    - Confirmed bookings can be cancelled.
    - Cancelled booking releases seats back to available state.

8. **Query Bookings**
    - Fetch booking by booking ID.
    - Fetch all bookings for a user.

9. **Expired Hold Handling**
    - Expired holds are lazily released during reserve/confirm calls.
    - A background expiry job can also be added in production.

## Non-Functional Requirements Handled

1. **Concurrency Safety**
    - Uses per-show locking with `ReentrantLock`.
    - Multiple users can book different shows concurrently.
    - Same show seat updates are protected from race conditions.

2. **Data Consistency**
    - Seat status transition is controlled: `AVAILABLE -> HELD -> BOOKED`.
    - Duplicate seat IDs are rejected before seat status updates.
    - Failed payment releases held seats.
    - Expired holds release seats.
    - Cancelled bookings release booked seats.

3. **Scalability-Oriented Design**
    - Locking is scoped per show, not global.
    - Read-heavy operations like search and seat view can be cached.
    - In production, repositories can be backed by DB, Redis, or distributed locks.

4. **Fault Tolerance**
    - Idempotency protects against duplicate confirm calls.
    - Booking status tracks successful, failed, and cancelled states.
    - Payment failure path is explicitly handled.

5. **Extensibility**
    - Separate services for seat hold, booking, payment, and repositories.
    - Can easily add pricing, refunds, notifications, coupons, waitlist, and audit logs.

6. **Performance**
    - Uses `ConcurrentHashMap` for thread-safe in-memory storage.
    - Per-show lock reduces contention compared to one global lock.
    - Seat search is show-scoped.

## APIs

### Search Events

```http
GET /v1/search?query={query}&location={location}&date={date}
```

Returns matching events.

### Get Event Details

```http
GET /v1/events/{eventId}
```

Returns event metadata like name and description.

### Get Showtimes

```http
GET /v1/events/{eventId}/showtimes
```

Returns all showtimes for an event.

### Get Seats For Show

```http
GET /v1/shows/{showId}/seats
```

Returns seat layout and status for a show.

### Reserve Seats

```http
POST /v1/booking/reserve
```

Request:

```json
{
  "showId": "show-1",
  "userId": "user-1",
  "seatIds": ["A1", "A2"]
}
```

Response:

```json
{
  "holdId": "hold-123",
  "showId": "show-1",
  "userId": "user-1",
  "seatIds": ["A1", "A2"],
  "expiresAt": 1760000000000,
  "status": "PENDING"
}
```

### Confirm Booking

```http
POST /v1/booking/confirm
```

Request:

```json
{
  "holdId": "hold-123",
  "paymentId": "success-payment-1",
  "idempotencyKey": "confirm-user-1-show-1-A1-A2"
}
```

Response:

```json
{
  "bookingId": "booking-123",
  "holdId": "hold-123",
  "userId": "user-1",
  "showId": "show-1",
  "seatIds": ["A1", "A2"],
  "paymentId": "success-payment-1",
  "status": "CONFIRMED"
}
```

### Release Hold

```http
DELETE /v1/booking/release/{holdId}
```

Releases pending held seats manually.

### Cancel Booking

```http
POST /v1/booking/{bookingId}/cancel
```

Cancels a confirmed booking and releases booked seats.

### Get Booking

```http
GET /v1/booking/{bookingId}
```

Returns booking details.

### Get User Bookings

```http
GET /v1/users/{userId}/bookings
```

Returns all bookings for a user.

## Entities

### User

Represents a customer using the platform.

Fields:
- `userId`
- `name`
- `email`

### Event

Represents an event/movie/concert.

Fields:
- `eventId`
- `name`
- `description`

### Venue

Represents a physical venue.

Fields:
- `venueId`
- `name`
- `totalSeats`

Physical seats belong to a venue, but seat availability belongs to a show. We create show-level seat inventory using showId + seatId, because the same event can
have multiple shows and the same seat can be booked in one show but available in another. Its better representation and help us manage seat in better way for events.


### Showtime

Represents a specific event occurrence at a venue and time.

Fields:
- `showId`
- `eventId`
- `venueId`
- `startTime`

### ShowSeat

Represents seat availability for a specific show.

Fields:
- `showId`
- `seatId`
- `row`
- `number`
- `category`
- `price`
- `status`

### SeatHold

Represents a temporary reservation.

Fields:
- `holdId`
- `showId`
- `userId`
- `seatIds`
- `expiresAt`
- `status`

### Booking

Represents final booking record.

Fields:
- `bookingId`
- `holdId`
- `userId`
- `showId`
- `seatIds`
- `paymentId`
- `status`

### Payment

Represents payment verification result.

Fields:
- `paymentId`
- `status`
- `amount`
- `currency`

## Status Models

### SeatStatus

```text
AVAILABLE -> HELD -> BOOKED
AVAILABLE <- HELD, when hold expires/payment fails/manual release
AVAILABLE <- BOOKED, when booking is cancelled
```

Values:
- `AVAILABLE`
- `HELD`
- `BOOKED`

### HoldStatus

Values:
- `PENDING`
- `EXPIRED`
- `CONFIRMED`
- `RELEASED`

### BookingStatus

Values:
- `PENDING`
- `CONFIRMED`
- `CANCELLED`
- `FAILED`

## Booking Flow

### Happy Path

```text
User searches events
    -> User selects event and showtime
    -> System returns show seats
    -> User selects seats
    -> System locks show
    -> System checks selected seats are AVAILABLE
    -> System marks seats as HELD
    -> System creates SeatHold with expiry
    -> User completes payment
    -> System verifies payment
    -> System marks seats as BOOKED
    -> System marks hold as CONFIRMED
    -> System creates Booking as CONFIRMED
```

### Payment Failure Flow

```text
User holds seats
    -> User attempts payment
    -> Payment fails
    -> System releases held seats
    -> System marks hold as RELEASED
    -> System creates FAILED booking record
```

### Hold Expiry Flow

```text
User holds seats
    -> User does not confirm within expiry time
    -> System detects expired hold
    -> System releases seats
    -> System marks hold as EXPIRED
```

### Cancellation Flow

```text
User has confirmed booking
    -> User cancels booking
    -> System locks show
    -> System releases booked seats
    -> System marks booking as CANCELLED
    -> Refund can be triggered asynchronously
```

## Concurrency Handling

### Problem

Two users may try to reserve the same seat at the same time.

Example:

```text
User A selects show-1 seat A1
User B selects show-1 seat A1
Both click reserve at nearly same time
```

Without locking, both may see the seat as available and both may book it.

### Current LLD Solution

The code uses one lock per show:

```java
private final Map<String, ReentrantLock> showLocks = new ConcurrentHashMap<>();
```

For every reserve, confirm, release, or cancel operation:

```text
Get lock for showId
    -> lock
    -> validate current seat state
    -> update seat state
    -> unlock
```

### Why Per-Show Lock?

Per-show locking is better than a global lock.

```text
User A booking show-1 should not block User B booking show-2.
Users booking the same show must be synchronized to avoid double booking.
```

### Production Alternatives

1. **Database Row Lock**
    - Use `SELECT ... FOR UPDATE` on `show_seats` rows.
    - Best when DB is the source of truth.

2. **Optimistic Locking**
    - Add `version` column to `show_seats`.
    - Update only if version matches.
    - Good for high read, moderate write systems.

3. **Redis Distributed Lock**
    - Lock on `showId` or `showId:seatId`.
    - Useful in distributed application deployments.

4. **Unique Constraints**
    - Add DB constraints to prevent duplicate confirmed seat bookings.
    - Example: unique confirmed booking per `(show_id, seat_id)`.

## HLD Detailed Design

### Main Components

```text
Client / Mobile App / Web App
        |
        v
API Gateway / Load Balancer
        |
        v
Event Search Service
Seat Inventory Service
Booking Service
Payment Service
Notification Service
        |
        v
Database + Cache + Message Queue
```

### Component Responsibilities

#### API Gateway

- Routes requests to internal services.
- Handles authentication, rate limiting, request tracing, and TLS termination.

#### Event Search Service

- Handles event search by location, date, category, and query.
- Uses search index like Elasticsearch/OpenSearch for scalable search.
- Caches popular event listings.

#### Seat Inventory Service

- Maintains seat availability per show.
- Handles seat hold, release, expiry, and status transition.
- Owns concurrency control for seat inventory.

#### Booking Service

- Owns booking lifecycle.
- Converts a valid hold into confirmed booking after payment.
- Handles idempotency and cancellation.

#### Payment Service

- Creates and verifies payments.
- Handles payment gateway callbacks/webhooks.
- Supports refund on cancellation.

#### Notification Service

- Sends email/SMS/push after booking confirmation, cancellation, or refund.
- Consumes async events from message queue.

#### Hold Expiry Worker

- Periodically scans expired holds.
- Releases expired held seats.
- Can use delayed queue, Redis TTL, cron job, or DB scheduled scanner.

### Storage

1. **Relational DB**
    - Stores users, venues, events, shows, seats, holds, bookings, payments.
    - Strong consistency is needed for bookings and seat inventory.

2. **Cache**
    - Stores event listings, seat maps, and popular show details.
    - Seat status cache must be invalidated carefully.

3. **Search Index**
    - Stores searchable event metadata.
    - Supports filtering by city, date, category, venue, and keyword.

4. **Message Queue**
    - Publishes booking confirmed/cancelled/payment events.
    - Used for notifications, analytics, and async refunds.

### HLD Data Flow: Reserve Seats

```text
Client
  -> Booking API
  -> Seat Inventory Service
  -> DB transaction starts
  -> Lock selected show_seats rows
  -> Validate seats are AVAILABLE
  -> Insert seat_hold
  -> Update seats to HELD
  -> Commit
  -> Return holdId
```

### HLD Data Flow: Confirm Booking

```text
Client
  -> Booking API with holdId, paymentId, idempotencyKey
  -> Booking Service checks idempotency table
  -> Payment Service verifies payment
  -> DB transaction starts
  -> Lock hold and selected seats
  -> Validate hold is PENDING and not expired
  -> Update seats to BOOKED
  -> Update hold to CONFIRMED
  -> Insert booking and booking_seats
  -> Commit
  -> Publish BookingConfirmed event
  -> Notification Service sends confirmation
```

### HLD Data Flow: Expire Hold

```text
Hold Expiry Worker
  -> Finds expired PENDING holds
  -> Locks hold and seats
  -> Updates seats from HELD to AVAILABLE
  -> Updates hold to EXPIRED
  -> Commits transaction
```

## Database Schema

### users

```sql
CREATE TABLE users (
    user_id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### events

```sql
CREATE TABLE events (
    event_id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(64),
    language VARCHAR(64),
    duration_minutes INT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### venues

```sql
CREATE TABLE venues (
    venue_id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(128) NOT NULL,
    address TEXT,
    total_seats INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### venue_seats

Master seat layout for a venue.

```sql
CREATE TABLE venue_seats (
    venue_id VARCHAR(64) NOT NULL,
    seat_id VARCHAR(64) NOT NULL,
    seat_row VARCHAR(16) NOT NULL,
    seat_number INT NOT NULL,
    category VARCHAR(64) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (venue_id, seat_id),
    FOREIGN KEY (venue_id) REFERENCES venues(venue_id)
);
```

### showtimes

```sql
CREATE TABLE showtimes (
    show_id VARCHAR(64) PRIMARY KEY,
    event_id VARCHAR(64) NOT NULL,
    venue_id VARCHAR(64) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (event_id) REFERENCES events(event_id),
    FOREIGN KEY (venue_id) REFERENCES venues(venue_id)
);
```

### show_seats

Seat inventory for each show.

```sql
CREATE TABLE show_seats (
    show_id VARCHAR(64) NOT NULL,
    seat_id VARCHAR(64) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(32) NOT NULL,
    version INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (show_id, seat_id),
    FOREIGN KEY (show_id) REFERENCES showtimes(show_id)
);
```

Indexes:

```sql
CREATE INDEX idx_show_seats_show_status ON show_seats(show_id, status);
```

### seat_holds

```sql
CREATE TABLE seat_holds (
    hold_id VARCHAR(64) PRIMARY KEY,
    show_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (show_id) REFERENCES showtimes(show_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

Indexes:

```sql
CREATE INDEX idx_seat_holds_show_status ON seat_holds(show_id, status);
CREATE INDEX idx_seat_holds_expiry ON seat_holds(status, expires_at);
```

### seat_hold_items

```sql
CREATE TABLE seat_hold_items (
    hold_id VARCHAR(64) NOT NULL,
    show_id VARCHAR(64) NOT NULL,
    seat_id VARCHAR(64) NOT NULL,
    PRIMARY KEY (hold_id, seat_id),
    FOREIGN KEY (hold_id) REFERENCES seat_holds(hold_id),
    FOREIGN KEY (show_id, seat_id) REFERENCES show_seats(show_id, seat_id)
);
```

### bookings

```sql
CREATE TABLE bookings (
    booking_id VARCHAR(64) PRIMARY KEY,
    hold_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    show_id VARCHAR(64) NOT NULL,
    payment_id VARCHAR(64),
    idempotency_key VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(32) NOT NULL,
    total_amount DECIMAL(10, 2),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (hold_id) REFERENCES seat_holds(hold_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (show_id) REFERENCES showtimes(show_id)
);
```

Indexes:

```sql
CREATE INDEX idx_bookings_user ON bookings(user_id, created_at);
CREATE INDEX idx_bookings_show ON bookings(show_id, status);
```

### booking_items

```sql
CREATE TABLE booking_items (
    booking_id VARCHAR(64) NOT NULL,
    show_id VARCHAR(64) NOT NULL,
    seat_id VARCHAR(64) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (booking_id, seat_id),
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
    FOREIGN KEY (show_id, seat_id) REFERENCES show_seats(show_id, seat_id)
);
```

### payments

```sql
CREATE TABLE payments (
    payment_id VARCHAR(64) PRIMARY KEY,
    booking_id VARCHAR(64),
    user_id VARCHAR(64) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(16) NOT NULL,
    status VARCHAR(32) NOT NULL,
    gateway_reference_id VARCHAR(128),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

### Important DB Constraints

1. `show_seats` primary key should be `(show_id, seat_id)`.
2. `bookings.idempotency_key` should be unique.
3. `show_seats.version` can support optimistic locking.
4. `seat_holds(status, expires_at)` index helps expiry worker.
5. Booking confirmation should run inside one DB transaction.

## Design Patterns / Principles Used

1. **Service Layer Pattern**
    - `SeatHoldService`, `BookingService`, and `PaymentService` separate business logic.

2. **Repository Pattern**
    - `SeatRepository` abstracts seat storage.

3. **DTO Pattern**
    - `ReserveSeatsRequest` and `ConfirmBookingRequest` separate API input from domain objects.

4. **State Machine**
    - Seat, hold, and booking statuses are modeled as enums.

5. **Idempotency Pattern**
    - Confirmation retry returns the same booking for the same idempotency key.

6. **Lock Striping by Show**
    - Per-show lock reduces contention and prevents double booking.

## Common Interview Questions and Short Answers

### 1. Why is seat status stored per show and not globally?

Because the same physical seat can be available for one show and booked for another show. Availability depends on `showId + seatId`.

### 2. How do you prevent double booking?

Use per-show lock in LLD. In production, use DB row locks, optimistic locking, Redis distributed locks, and unique constraints.

### 3. Why do we need a seat hold?

Payment takes time. Hold temporarily blocks seats so another user cannot book them while the first user is paying.

### 4. What happens when payment fails?

Release held seats, mark hold as released, and create/update booking as failed.

### 5. What happens when user retries confirm API?

The idempotency key ensures the same booking response is returned instead of creating duplicate bookings.

### 6. How are expired holds handled?

Current LLD handles lazy expiry during reserve/confirm. Production systems should also run a background expiry worker.

### 7. Should seat status be cached?

Seat maps can be cached, but live seat status must be invalidated carefully. DB should remain source of truth.

### 8. What lock granularity is best?

Per-show lock is a good balance. Per-seat lock gives more concurrency but is more complex for multi-seat booking.

### 9. How do you handle multi-seat atomicity?

Lock all selected seats or the show, validate all seats, then update all seats in one transaction. If any seat is unavailable, fail the whole request.

### 10. How would you scale search?

Use Elasticsearch/OpenSearch for event search and cache popular queries by city/date/category.

### 11. How would you handle payment webhooks?

Payment webhook should update payment status and then confirm/release booking idempotently based on payment result.

### 12. What happens if booking succeeds but notification fails?

Booking remains confirmed. Notification should be retried asynchronously using a queue.

### 13. What happens if app crashes after payment but before booking confirmation?

Use payment webhook or reconciliation job to complete or refund the booking. Idempotency prevents duplicate processing.

### 14. Why use booking status and hold status separately?

Hold tracks temporary reservation lifecycle. Booking tracks final purchase lifecycle. They are related but not the same.

### 15. Which database is preferred?

Relational DB is preferred for booking and inventory because transactions and consistency are important.
