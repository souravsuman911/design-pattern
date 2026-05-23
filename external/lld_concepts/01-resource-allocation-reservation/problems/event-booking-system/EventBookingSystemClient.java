package internal.designPattern.external.lld_concepts;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

enum SeatStatus { AVAILABLE, HELD, BOOKED }
enum BookingStatus { PENDING, CONFIRMED, CANCELLED, FAILED }
enum HoldStatus { PENDING, EXPIRED, CONFIRMED, RELEASED }
enum PaymentStatus { SUCCESS, FAILED }

class User {
    String userId;
    String name;
    String email;
}

class Event {
    String eventId;
    String name;
    String description;
    List<Venue> venues;
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

class ShowSeat {
    String showId;
    String seatId;
    String row;
    int number;
    String category;
    double price;
    SeatStatus status = SeatStatus.AVAILABLE;

    ShowSeat(String showId, String seatId, String row, int number, String category, double price) {
        this.showId = showId;
        this.seatId = seatId;
        this.row = row;
        this.number = number;
        this.category = category;
        this.price = price;
    }
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
    String paymentId;
    BookingStatus status;
}

class ReserveSeatsRequest {
    String showId;
    String userId;
    List<String> seatIds;
}

class ConfirmBookingRequest {
    String holdId;
    String paymentId;
    String idempotencyKey;
}

class SearchController {
    List<Event> search(String query, String location, String date) {
        return List.of();
    }

    Event getEventDetails(String eventId) {
        return new Event();
    }

    List<Venue> getVenues(String eventId) { return List.of(); }

    List<Showtime> getShowtimes(String eventId) {
        return List.of();
    }
}

class SeatController {
    private final SeatRepository seatRepository;

    SeatController(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    List<ShowSeat> getSeats(String showId) {
        return seatRepository.getSeats(showId);
    }
}

class BookingController {
    private final SeatHoldService seatHoldService;
    private final BookingService bookingService;

    BookingController(SeatHoldService seatHoldService, BookingService bookingService) {
        this.seatHoldService = seatHoldService;
        this.bookingService = bookingService;
    }

    SeatHold reserveSeats(ReserveSeatsRequest request) {
        return seatHoldService.reserveSeats(request.showId, request.seatIds, request.userId);
    }

    Booking confirmBooking(ConfirmBookingRequest request) {
        return bookingService.confirmBooking(request.holdId, request.paymentId, request.idempotencyKey);
    }

    void releaseSeats(String holdId) {
        seatHoldService.releaseHold(holdId);
    }

    Booking cancelBooking(String bookingId) {
        return bookingService.cancelBooking(bookingId);
    }
}

class BookingQueryController {
    private final BookingService bookingService;

    BookingQueryController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    List<Booking> getUserBookings(String userId) {
        return bookingService.getUserBookings(userId);
    }

    Booking getBooking(String bookingId) {
        return bookingService.getBooking(bookingId);
    }
}

class SeatHoldService {
    private static final long HOLD_TTL_MILLIS = 10 * 60 * 1000;

    private final SeatRepository seatRepo;
    private final Map<String, SeatHold> holds = new ConcurrentHashMap<>();

    SeatHoldService(SeatRepository seatRepo) {
        this.seatRepo = seatRepo;
    }

    SeatHold reserveSeats(String showId, List<String> seatIds, String userId) {
        validateReserveRequest(showId, seatIds, userId);
        ReentrantLock lock = seatRepo.lockForShow(showId);
        lock.lock();
        try {
            expirePendingHolds(showId);
            if (!seatRepo.areSeatsAvailable(showId, seatIds)) {
                throw new IllegalStateException("Seats not available");
            }

            seatRepo.holdSeats(showId, seatIds);

            SeatHold hold = new SeatHold();
            hold.holdId = UUID.randomUUID().toString();
            hold.showId = showId;
            hold.userId = userId;
            hold.seatIds = List.copyOf(seatIds);
            hold.expiresAt = System.currentTimeMillis() + HOLD_TTL_MILLIS;
            holds.put(hold.holdId, hold);
            return hold;
        } finally {
            lock.unlock();
        }
    }

    SeatHold getActiveHold(String holdId) {
        SeatHold hold = holds.get(holdId);
        if (hold == null) {
            throw new IllegalArgumentException("Hold not found");
        }
        ReentrantLock lock = seatRepo.lockForShow(hold.showId);
        lock.lock();
        try {
            expireHoldIfRequired(hold);
            if (hold.status != HoldStatus.PENDING) {
                throw new IllegalStateException("Hold is not pending");
            }
            return hold;
        } finally {
            lock.unlock();
        }
    }

    void markConfirmed(SeatHold hold) {
        hold.status = HoldStatus.CONFIRMED;
    }

    void releaseHold(String holdId) {
        SeatHold hold = holds.get(holdId);
        if (hold == null) {
            return;
        }
        ReentrantLock lock = seatRepo.lockForShow(hold.showId);
        lock.lock();
        try {
            if (hold.status == HoldStatus.PENDING) {
                seatRepo.releaseSeats(hold.showId, hold.seatIds);
                hold.status = HoldStatus.RELEASED;
            }
        } finally {
            lock.unlock();
        }
    }

    void expirePendingHolds(String showId) {
        long now = System.currentTimeMillis();
        holds.values().stream()
                .filter(hold -> Objects.equals(hold.showId, showId))
                .filter(hold -> hold.status == HoldStatus.PENDING)
                .filter(hold -> hold.expiresAt <= now)
                .forEach(this::expireHold);
    }

    private void expireHoldIfRequired(SeatHold hold) {
        if (hold.status == HoldStatus.PENDING && hold.expiresAt <= System.currentTimeMillis()) {
            expireHold(hold);
        }
    }

    private void expireHold(SeatHold hold) {
        seatRepo.releaseSeats(hold.showId, hold.seatIds);
        hold.status = HoldStatus.EXPIRED;
    }

    private void validateReserveRequest(String showId, List<String> seatIds, String userId) {
        if (showId == null || showId.isBlank()) {
            throw new IllegalArgumentException("showId is required");
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }
        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("At least one seat is required");
        }
        if (new HashSet<>(seatIds).size() != seatIds.size()) {
            throw new IllegalArgumentException("Duplicate seats are not allowed");
        }
    }
}

class BookingService {
    private final SeatRepository seatRepo;
    private final SeatHoldService holdService;
    private final PaymentService paymentService;
    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();
    private final Map<String, String> idempotencyKeyToBookingId = new ConcurrentHashMap<>();

    BookingService(SeatRepository seatRepo, SeatHoldService holdService, PaymentService paymentService) {
        this.seatRepo = seatRepo;
        this.holdService = holdService;
        this.paymentService = paymentService;
    }

    Booking confirmBooking(String holdId, String paymentId, String idempotencyKey) {
        validateConfirmRequest(holdId, paymentId, idempotencyKey);
        Booking existingBooking = findIdempotentBooking(idempotencyKey);
        if (existingBooking != null) {
            return existingBooking;
        }

        SeatHold hold = holdService.getActiveHold(holdId);
        ReentrantLock lock = seatRepo.lockForShow(hold.showId);
        lock.lock();
        try {
            existingBooking = findIdempotentBooking(idempotencyKey);
            if (existingBooking != null) {
                return existingBooking;
            }

            if (hold.status != HoldStatus.PENDING) {
                throw new IllegalStateException("Hold is not pending");
            }

            if (hold.expiresAt <= System.currentTimeMillis()) {
                holdService.releaseHold(holdId);
                throw new IllegalStateException("Hold expired");
            }

            if (paymentService.verifyPayment(paymentId) != PaymentStatus.SUCCESS) {
                holdService.releaseHold(holdId);
                Booking failedBooking = createFailedBooking(hold, paymentId);
                idempotencyKeyToBookingId.put(idempotencyKey, failedBooking.bookingId);
                return failedBooking;
            }

            seatRepo.bookSeats(hold.showId, hold.seatIds);
            holdService.markConfirmed(hold);

            Booking booking = new Booking();
            booking.bookingId = UUID.randomUUID().toString();
            booking.holdId = holdId;
            booking.userId = hold.userId;
            booking.showId = hold.showId;
            booking.seatIds = List.copyOf(hold.seatIds);
            booking.paymentId = paymentId;
            booking.status = BookingStatus.CONFIRMED;

            bookings.put(booking.bookingId, booking);
            idempotencyKeyToBookingId.put(idempotencyKey, booking.bookingId);
            return booking;
        } finally {
            lock.unlock();
        }
    }

    Booking cancelBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found");
        }
        ReentrantLock lock = seatRepo.lockForShow(booking.showId);
        lock.lock();
        try {
            if (booking.status == BookingStatus.CONFIRMED) {
                seatRepo.releaseSeats(booking.showId, booking.seatIds);
                booking.status = BookingStatus.CANCELLED;
            }
            return booking;
        } finally {
            lock.unlock();
        }
    }

    List<Booking> getUserBookings(String userId) {
        return bookings.values().stream()
                .filter(booking -> Objects.equals(booking.userId, userId))
                .collect(Collectors.toList());
    }

    Booking getBooking(String bookingId) {
        return bookings.get(bookingId);
    }

    private Booking createFailedBooking(SeatHold hold, String paymentId) {
        Booking booking = new Booking();
        booking.bookingId = UUID.randomUUID().toString();
        booking.holdId = hold.holdId;
        booking.userId = hold.userId;
        booking.showId = hold.showId;
        booking.seatIds = List.copyOf(hold.seatIds);
        booking.paymentId = paymentId;
        booking.status = BookingStatus.FAILED;
        bookings.put(booking.bookingId, booking);
        return booking;
    }

    private Booking findIdempotentBooking(String idempotencyKey) {
        String bookingId = idempotencyKeyToBookingId.get(idempotencyKey);
        return bookingId == null ? null : bookings.get(bookingId);
    }

    private void validateConfirmRequest(String holdId, String paymentId, String idempotencyKey) {
        if (holdId == null || holdId.isBlank()) {
            throw new IllegalArgumentException("holdId is required");
        }
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("paymentId is required");
        }
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("idempotencyKey is required");
        }
    }
}

class SeatRepository {
    private final Map<String, ShowSeat> seats = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> showLocks = new ConcurrentHashMap<>();

    void addSeat(ShowSeat seat) {
        seats.put(key(seat.showId, seat.seatId), seat);
    }

    List<ShowSeat> getSeats(String showId) {
        return seats.values().stream()
                .filter(seat -> Objects.equals(seat.showId, showId))
                .collect(Collectors.toList());
    }

    ReentrantLock lockForShow(String showId) {
        return showLocks.computeIfAbsent(showId, ignored -> new ReentrantLock());
    }

    boolean areSeatsAvailable(String showId, List<String> seatIds) {
        return seatIds.stream().allMatch(seatId -> {
            ShowSeat seat = getExistingSeat(showId, seatId);
            return seat.status == SeatStatus.AVAILABLE;
        });
    }

    void holdSeats(String showId, List<String> seatIds) {
        updateStatus(showId, seatIds, SeatStatus.HELD);
    }

    void bookSeats(String showId, List<String> seatIds) {
        updateStatus(showId, seatIds, SeatStatus.BOOKED);
    }

    void releaseSeats(String showId, List<String> seatIds) {
        updateStatus(showId, seatIds, SeatStatus.AVAILABLE);
    }

    private void updateStatus(String showId, List<String> seatIds, SeatStatus status) {
        seatIds.forEach(seatId -> getExistingSeat(showId, seatId).status = status);
    }

    private ShowSeat getExistingSeat(String showId, String seatId) {
        ShowSeat seat = seats.get(key(showId, seatId));
        if (seat == null) {
            throw new IllegalArgumentException("Seat does not exist for show: " + seatId);
        }
        return seat;
    }

    private String key(String showId, String seatId) {
        return showId + ":" + seatId;
    }
}

class PaymentService {
    PaymentStatus verifyPayment(String paymentId) {
        return paymentId.startsWith("success") ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
    }
}

public class EventBookingSystemClient {
    public static void main(String[] args) {
        SeatRepository seatRepository = new SeatRepository();
        seatRepository.addSeat(new ShowSeat("show-1", "A1", "A", 1, "GOLD", 500));
        seatRepository.addSeat(new ShowSeat("show-1", "A2", "A", 2, "GOLD", 500));
        seatRepository.addSeat(new ShowSeat("show-2", "A1", "A", 1, "GOLD", 700));

        SeatHoldService holdService = new SeatHoldService(seatRepository);
        BookingService bookingService = new BookingService(seatRepository, holdService, new PaymentService());
        BookingController bookingController = new BookingController(holdService, bookingService);

        ReserveSeatsRequest reserveRequest = new ReserveSeatsRequest();
        reserveRequest.showId = "show-1";
        reserveRequest.userId = "user-1";
        reserveRequest.seatIds = List.of("A1", "A2");

        SeatHold hold = bookingController.reserveSeats(reserveRequest);

        ConfirmBookingRequest confirmRequest = new ConfirmBookingRequest();
        confirmRequest.holdId = hold.holdId;
        confirmRequest.paymentId = "success-payment-1";
        confirmRequest.idempotencyKey = "confirm-user-1-show-1-A1-A2";

        Booking booking = bookingController.confirmBooking(confirmRequest);
        System.out.println("Booking status: " + booking.status);
        System.out.println("Booked seats: " + booking.seatIds);
    }
}
