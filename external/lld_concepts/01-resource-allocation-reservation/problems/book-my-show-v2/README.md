# Book My Show V2 LLD

Implementation: `BookMyShow.java`

```text
+---------------------------+
| BookMyShowL2              |
+---------------------------+
| List<Show> shows          |
| Map<String, Booking>      |
| bookings                  |
+---------------------------+
| searchShows()             |
| processPayment()          |
| bookSeats()               |
| cancelBooking()           |
+---------------------------+

+---------------------------+
| City                      |
+---------------------------+
| String name               |
| List<Theatre> theatres    |
+---------------------------+

+---------------------------+
| Theatre                   |
+---------------------------+
| String theatreId          |
| String name               |
| List<Screen> screens      |
+---------------------------+

+---------------------------+
| Screen                    |
+---------------------------+
| String screenId           |
| String name               |
| List<ScreenSeat> seats    |
+---------------------------+

+---------------------------+
| Movie implements Event    |
+---------------------------+
| String movieId            |
| String name               |
+---------------------------+

+---------------------------+
| Show                      |
+---------------------------+
| String showId             |
| Event event               |
| Map<String, ShowSeat>     |
| seats                     |
+---------------------------+
| getSeat()                 |
| printAvailableSeats()     |
+---------------------------+

+---------------------------+
| ShowSeat                  |
+---------------------------+
| String seatId             |
| SeatType seatType         |
| SeatStatus status         |
| BigDecimal price          |
| ReentrantLock lock        |
+---------------------------+

+---------------------------+
| Booking                   |
+---------------------------+
| String bookingId          |
| User user                 |
| Show show                 |
| List<ShowSeat> seats      |
| BookingStatus status      |
+---------------------------+

+---------------------------+
| Payment                   |
+---------------------------+
| String paymentId          |
| BigDecimal amount         |
| PaymentStatus status      |
+---------------------------+
```

## Category
Resource Allocation and Reservation

## Scope
Interview-ready low-level design for an **extended BookMyShow flow**. This version adds theatre hierarchy, seat categories, seat-level locking, pricing, booking states, payment handling, show search, and cancellation.

## Functional Requirements
- Model city, theatre, screen, and show hierarchy.
- Support movie-based show search.
- Represent show seats with type, price, and booking status.
- Lock each requested seat during booking.
- Sum selected seat prices and process payment.
- Confirm booking and persist it in an in-memory booking map.
- Cancel an existing booking and release seats.
- Demonstrate concurrent booking attempts on overlapping seats.

## Non-Functional Requirements
- **Consistency**: A seat must not be sold twice.
- **Better concurrency**: Lock only the requested seats, not the whole show.
- **Extensibility**: Event abstraction allows future support for concerts or sports.
- **Scalability**: Search, booking, payment, and inventory should be separable services in production.

## Main Flow
```text
Create show inventory with typed seats
 -> search shows by movie name
 -> user selects seat IDs
 -> lock each requested ShowSeat
 -> validate availability
 -> calculate total amount
 -> process payment
 -> mark seats booked
 -> create confirmed booking
 -> release locks
```

## Cancellation Flow
```text
Receive booking ID
 -> fetch booking
 -> ignore if already cancelled
 -> mark booked seats available again
 -> mark booking cancelled
 -> return inventory to pool
```

## Schema Design
### In-Memory Object Model
- `City`, `Theatre`, `Screen`: venue hierarchy.
- `ScreenSeat`: static seat metadata for a screen.
- `Movie implements Event`: searchable content entity.
- `Show`: event instance with `Map<String, ShowSeat>` inventory.
- `ShowSeat`: seat status, seat type, price, and its own `ReentrantLock`.
- `Booking`: booking details and booking lifecycle state.
- `Payment`: payment amount and payment lifecycle state.
- `BookingService`: search, payment, booking, and cancellation orchestration.

### Production Database Mapping
- `city`
  - `city_id` PK
  - `name`
- `theatre`
  - `theatre_id` PK
  - `city_id` FK
  - `name`
- `screen`
  - `screen_id` PK
  - `theatre_id` FK
  - `name`
- `screen_seat`
  - `screen_seat_id` PK
  - `screen_id` FK
  - `seat_label`
  - `seat_type`
- `event`
  - `event_id` PK
  - `event_type`
  - `name`
- `show`
  - `show_id` PK
  - `event_id` FK
  - `screen_id` FK
  - `start_time`
  - `status`
- `show_seat`
  - `show_seat_id` PK
  - `show_id` FK
  - `screen_seat_id` FK
  - `seat_type`
  - `price`
  - `status`
  - unique key on `show_id, screen_seat_id`
- `booking`
  - `booking_id` PK
  - `user_id` FK
  - `show_id` FK
  - `status`
  - `total_amount`
  - `created_at`
- `booking_seat`
  - `booking_id` FK
  - `show_seat_id` FK
  - composite unique key on `show_seat_id`
- `payment`
  - `payment_id` PK
  - `booking_id` FK
  - `amount`
  - `status`
  - `created_at`

## Concurrency Notes
- This version improves over v1 by locking only the requested seats using `ReentrantLock`.
- Seat-level locking allows unrelated seats in the same show to be booked concurrently.
- In a distributed deployment, local JVM locks are not enough; use DB transactions, optimistic versioning, or distributed locking plus unique constraints.

## Design Notes
- `Event` is a useful abstraction, but the current implementation only provides `Movie`.
- Payment is mocked as immediate success; failure and compensation paths are not modeled yet.
- Cancellation releases seats directly and does not process refunds yet.
- A hold-with-expiry stage would make the payment flow more realistic and safer.
- The sample data currently searches for `Interstellar` while the configured movie is `Dhurandar`, so the demo search result is intentionally `0` for the current code path.

## Interview Discussion Points
- V1 uses coarse show-level locking; V2 moves to fine-grained seat-level locking.
- Booking state and payment state should be modeled separately because payment can fail after seat selection begins.
- For real systems, `show_seat` is the core inventory table and must be protected by transactional constraints.

## Possible extensions include:
- ShowSeat abstraction instead of storing booking state in Seat
- Seat Hold with expiry timer
- Idempotent booking requests
- Payment rollback handling
- Fine-grained seat locking using ReentrantLock
- Deadlock prevention via ordered seat locking
- Dynamic pricing strategies
- Notification and waiting-list support