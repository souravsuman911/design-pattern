package internal.designPattern.external.lld.bookMyShow;

enum SeatStatus { AVAILABLE, HELD, BOOKED }
enum BookingStatus { PENDING, CONFIRMED, CANCELLED }
enum HoldStatus { PENDING, EXPIRED, CONFIRMED }

class User {
    String userId;
    String name;
    String email;
}

class Event {
    String eventId;
    String name;
    String description;
}

class Venue {
    String venueId;
    String name;
    int totalSeats;
}

class Showtime {
    String showId;
    String eventId;
    String venueId;
    long startTime;
}

class Seat {
    String seatId;
    SeatStatus status = SeatStatus.AVAILABLE;
}

class SeatHold {
    String holdId;
    String showId;
    String userId;
    List<String> seatIds;
    long expiresAt;
    HoldStatus status = HoldStatus.PENDING;
}

class Booking {
    String bookingId;
    String holdId;
    String userId;
    String showId;
    List<String> seatIds;
    BookingStatus status;
}

@RestController
@RequestMapping("/v1/search")
class SearchController {
    @GetMapping
    List<Event> search(String query, String location, String date) {
        // call SearchService / Repository
        return List.of(); // mocked
    }

    Event getEventDetails(String eventId) {
        return new Event(); // mocked
    }

    List<Showtime> getShowtimes(String eventId) {
        return List.of(); // mocked
    }
}

class SeatController {

    SeatRepository seatRepository;

    List<Seat> getSeats(String showId) {
        // ideally filtered by showId
        return new ArrayList<>(seatRepository.seatMap.values());
    }
}

@RestController
@RequestMapping("/v1/booking")
class BookingController {

    SeatHoldService seatHoldService;
    BookingService bookingService;

    // POST /v1/booking/reserve
    @PostMapping("/reserve")
    SeatHold reserveSeats(String showId, List<String> seatIds, String userId) {
        return seatHoldService.reserveSeats(showId, seatIds, userId);
    }

    // POST /v1/booking/confirm
    Booking confirmBooking(String holdId, String paymentId, String idempotencyKey) {
        // payment validation assumed successful
        return bookingService.confirmBooking(holdId);
    }

    // DELETE /v1/booking/release/{holdId}
    void releaseSeats(String holdId) {
        seatHoldService.releaseHold(holdId);
    }
}

class BookingQueryController {

    BookingService bookingService;

    List<Booking> getUserBookings(String userId) {
        return bookingService.bookings.values()
                .stream()
                .filter(b -> b.userId.equals(userId))
                .toList();
    }

    Booking getBooking(String bookingId) {
        return bookingService.bookings.get(bookingId);
    }
}

class SeatHoldService {
    SeatRepository seatRepo;
    Map<String, SeatHold> holds = new HashMap<>();

    SeatHold reserveSeats(String showId, List<String> seatIds, String userId) {
        synchronized (seatRepo) {
            if (!seatRepo.areSeatsAvailable(seatIds))
                throw new RuntimeException("Seats not available");

            seatRepo.holdSeats(seatIds);

            SeatHold hold = new SeatHold();
            hold.holdId = UUID.randomUUID().toString();
            hold.showId = showId;
            hold.userId = userId;
            hold.seatIds = seatIds;
            hold.expiresAt = System.currentTimeMillis() + 10 * 60 * 1000;

            holds.put(hold.holdId, hold);
            return hold;
        }
    }

    void releaseHold(String holdId) {
        SeatHold hold = holds.get(holdId);
        if (hold != null && hold.status == HoldStatus.PENDING) {
            seatRepo.releaseSeats(hold.seatIds);
            hold.status = HoldStatus.EXPIRED;
        }
    }
}

class BookingService {
    SeatRepository seatRepo;
    SeatHoldService holdService;
    Map<String, Booking> bookings = new HashMap<>();

    Booking confirmBooking(String holdId) {
        SeatHold hold = holdService.holds.get(holdId);

        if (hold == null || hold.expiresAt < System.currentTimeMillis())
            throw new RuntimeException("Hold expired");

        synchronized (seatRepo) {
            seatRepo.bookSeats(hold.seatIds);
            hold.status = HoldStatus.CONFIRMED;

            Booking booking = new Booking();
            booking.bookingId = UUID.randomUUID().toString();
            booking.holdId = holdId;
            booking.userId = hold.userId;
            booking.showId = hold.showId;
            booking.seatIds = hold.seatIds;
            booking.status = BookingStatus.CONFIRMED;

            bookings.put(booking.bookingId, booking);
            return booking;
        }
    }
}

class SeatRepository {
    Map<String, Seat> seatMap = new HashMap<>();

    synchronized boolean areSeatsAvailable(List<String> seatIds) {
        return seatIds.stream()
                .allMatch(id -> seatMap.get(id).status == SeatStatus.AVAILABLE);
    }

    synchronized void holdSeats(List<String> seatIds) {
        seatIds.forEach(id -> seatMap.get(id).status = SeatStatus.HELD);
    }

    synchronized void bookSeats(List<String> seatIds) {
        seatIds.forEach(id -> seatMap.get(id).status = SeatStatus.BOOKED);
    }

    synchronized void releaseSeats(List<String> seatIds) {
        seatIds.forEach(id -> seatMap.get(id).status = SeatStatus.AVAILABLE);
    }
}





public class BookMyShowClient {
    public static void main(String[] args) {

    }
}
