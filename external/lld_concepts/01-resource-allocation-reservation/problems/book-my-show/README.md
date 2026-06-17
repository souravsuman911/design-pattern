# Book My Show LLD

Implementation: `BookMyShow.java`

```text
+----------------------+
| BookMyShow           |
+----------------------+
| List<City> cities    |
+----------------------+
| searchMovies()       |
| searchShows()        |
+----------------------+

+----------------------+
| City                 |
+----------------------+
| String name          |
| List<Theatre>        |
+----------------------+

+----------------------+
| Theatre              |
+----------------------+
| String theatreId     |
| String name          |
| Address address      |
| List<Screen> screens |
+----------------------+

+----------------------+
| Screen               |
+----------------------+
| String screenId      |
| String name          |
| List<Seat> seats     |
+----------------------+

+----------------------+
| Movie                |
+----------------------+
| String movieId       |
| String title         |
| int duration         |
| String language      |
+----------------------+

+----------------------+
| Show                 |
+----------------------+
| String showId        |
| Movie movie          |
| Screen screen        |
| DateTime startTime   |
| DateTime endTime     |
| ShowSeat[] seats     |
+----------------------+
| getAvailableSeats()  |
+----------------------+

+----------------------+
| Seat                 |
+----------------------+
| String seatId        |
| SeatType type        |
| int row              |
| int number           |
+----------------------+

+----------------------+
| ShowSeat             |
+----------------------+
| Seat seat            |
| SeatStatus status    |
| double price         |
+----------------------+

+----------------------+
| User                 |
+----------------------+
| String userId        |
| String name          |
| String email         |
+----------------------+

+----------------------+
| Booking              |
+----------------------+
| String bookingId     |
| User user            |
| Show show            |
| List<ShowSeat> seats |
| BookingStatus status |
+----------------------+
| confirmBooking()     |
| cancelBooking()      |
+----------------------+

+----------------------+
| Payment              |
+----------------------+
| String paymentId     |
| double amount        |
| PaymentStatus status |
+----------------------+
| processPayment()     |
+----------------------+
```

## Category
Resource Allocation and Reservation

## Scope
Interview-ready low-level design for a **minimal BookMyShow seat-booking flow**. The current implementation focuses on one show, seat availability, concurrent booking attempts, and atomic confirmation inside a synchronized booking section.

## Functional Requirements
- Create a movie and one show with seats.
- Allow a user to request multiple seat IDs for a show.
- Validate that each requested seat exists.
- Reject already booked seats.
- Confirm booking and mark all requested seats as booked atomically.
- Print available seats before booking.
- Demonstrate concurrent booking attempts from multiple users.

## Non-Functional Requirements
- **Consistency**: A seat must not be booked twice.
- **Atomicity**: Multi-seat booking should either fully succeed or fail.
- **Readability**: The model remains intentionally compact for interview explanation.
- **Scalability**: For production, inventory and bookings must move from in-memory maps to persistent storage.

## Main Flow
```text
Create movie, show, users, and seats
 -> display available seats
 -> user requests seat list
 -> enter synchronized block on show
 -> validate all requested seats
 -> reject if any seat is unavailable
 -> mark selected seats as booked
 -> create booking object
 -> release lock
```

## Schema Design
### In-Memory Object Model
- `Movie`: stores movie identity and name.
- `Seat`: stores `seatId` and mutable `SeatStatus`.
- `Show`: stores the movie and `Map<String, Seat>` inventory.
- `User`: stores user identity and name.
- `Booking`: stores booking ID, user, show, and selected seats.
- `BookingService`: validates and confirms seat allocation.

### Production Database Mapping
- `movie`
  - `movie_id` PK
  - `name`
- `show`
  - `show_id` PK
  - `movie_id` FK
  - `screen_id`
  - `start_time`
  - `end_time`
  - `status`
- `seat`
  - `seat_id` PK
  - `screen_id`
  - `seat_label`
- `show_seat`
  - `show_seat_id` PK
  - `show_id` FK
  - `seat_id` FK
  - `status`
  - `price`
  - unique key on `show_id, seat_id`
- `booking`
  - `booking_id` PK
  - `user_id` FK
  - `show_id` FK
  - `status`
  - `created_at`
- `booking_seat`
  - `booking_id` FK
  - `show_seat_id` FK

## Concurrency Notes
- The current code locks on the `Show` object, which serializes all booking attempts for that show.
- This is simple and safe for demo purposes, but it limits concurrency because unrelated seats in the same show cannot be booked in parallel.
- In production, prefer row-level seat locking or a unique constraint on `show_seat` confirmation.

## Design Notes
- The README diagram is broader than the current code. The actual Java implementation is a smaller subset centered on `Movie`, `Show`, `Seat`, `User`, `Booking`, and `BookingService`.
- There is no hold state, payment flow, cancellation, or seat pricing in the current implementation.
- A hold-with-expiry model is the natural next extension for realistic ticket booking.

## Interview Discussion Points
- Synchronizing on `show` is the easiest way to prevent double booking in a single-process demo.
- Multi-seat validation must happen before mutating any seat state to preserve atomicity.
- To scale this, persist `show_seat` rows and confirm seats transactionally.
