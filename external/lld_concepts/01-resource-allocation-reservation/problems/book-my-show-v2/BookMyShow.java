import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class BookMyShowL2 {

    enum SeatStatus {
        AVAILABLE, BOOKED
    }

    enum SeatType {
        SILVER, GOLD, PLATINUM
    }

    enum BookingStatus {
        AVAILABLE, BOOKED, CANCELLED
    }

    enum PaymentStatus {
        PENDING, SUCCESS, FAILED
    }

    static class City {
        private final String name;
        private List<Theatre> theatres;

        public City(String name) {
            this.name = name;
        }

        public List<Theatre> getTheatres() {
            return theatres;
        }

        public void setTheatres(List<Theatre> theatres) {
            this.theatres = theatres;
        }
    }

    static class Theatre {
        private final String theatreId;
        private final String name;
        private List<Screen> screens;

        public Theatre(String theatreId, String name) {
            this.theatreId = theatreId;
            this.name = name;
        }

        public List<Screen> getScreens() {
            return screens;
        }

        public void setScreens(List<Screen> screens) {
            this.screens = screens;
        }
    }

    static class Screen {
        private final String screenId;
        private final String name;
        private List<ScreenSeat> seats;

        public Screen(String screenId, String name, List<ScreenSeat> seats) {
            this.screenId = screenId;
            this.name = name;
            this.seats = seats;
        }
    }

    static class ScreenSeat {
        private final String seatId;
        private final SeatType seatType;

        public ScreenSeat(String seatId, SeatType seatType) {
            this.seatId = seatId;
            this.seatType = seatType;
        }
    }


    interface Event {
    }

    static class Movie implements Event{
        private final String movieId;
        private final String name;

        public Movie(String movieId, String name) {
            this.movieId = movieId;
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    static class ShowSeat {
        private final String seatId;
        private SeatType seatType;
        private SeatStatus status;
        private BigDecimal price;
        private ReentrantLock lock;

        public ShowSeat(String seatId, SeatType seatType, BigDecimal price) {
            this.seatId = seatId;
            this.seatType = seatType;
            this.price = price;
            this.status = SeatStatus.AVAILABLE;
            this.lock = new ReentrantLock();
        }

        public String getSeatId() {
            return seatId;
        }

        public SeatStatus getStatus() {
            return status;
        }

        public SeatType getSeatType() {
            return seatType;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setStatus(SeatStatus status) {
            this.status = status;
        }
    }

    static class Show {

        private final String showId;
        private final Event event;
        private final Map<String, ShowSeat> seats;

        public Show(String showId, Event event, List<ShowSeat> showSeatList) {
            this.showId = showId;
            this.event = event;
            this.seats = new HashMap<>();

            for (ShowSeat showSeat : showSeatList) {
                seats.put(showSeat.getSeatId(), showSeat);
            }
        }

        public Event getEvent() {
            return event;
        }

        public ShowSeat getSeat(String seatId) {
            return seats.get(seatId);
        }

        public void printAvailableSeats() {
            System.out.println("Available Seats:");

            for (ShowSeat showSeat : seats.values()) {
                if (showSeat.getStatus() == SeatStatus.AVAILABLE) {
                    System.out.print(showSeat.getSeatId() + " ");
                }
            }

            System.out.println();
        }
    }

    static class User {

        private final String userId;
        private final String name;

        public User(String userId, String name) {
            this.userId = userId;
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    static class Payment {
        private String paymentId;
        private BigDecimal amount;
        private PaymentStatus paymentStatus;

        public Payment(String paymentId, BigDecimal amount) {
            this.paymentId = paymentId;
            this.amount = amount;
        }

        public PaymentStatus getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(PaymentStatus paymentStatus) {
            this.paymentStatus = paymentStatus;
        }
    }

    static class Booking {

        private final String bookingId;
        private final User user;
        private final Show show;
        private final List<ShowSeat> showSeats;
        private BookingStatus status;

        public Booking(String bookingId, User user, Show show, List<ShowSeat> showSeats) {
            this.bookingId = bookingId;
            this.user = user;
            this.show = show;
            this.showSeats = showSeats;
        }

        @Override
        public String toString() {

            List<String> seatIds = new ArrayList<>();
            for (ShowSeat showSeat : showSeats) {
                seatIds.add(showSeat.getSeatId());
            }
            return "BookingId : " + bookingId + ", User : " + user.getName() + ", Seats : " + seatIds;
        }

        public BookingStatus getStatus() {
            return status;
        }

        public void setStatus(BookingStatus status) {
            this.status = status;
        }

        public String getBookingId() {
            return bookingId;
        }

        public List<ShowSeat> getSeats() {
            return showSeats;
        }
    }

    static class BookingService {

        private final List<Show> shows;
        private final Map<String, Booking> bookings;

        public BookingService(List<Show> shows) {
            this.shows = shows;
            this.bookings = new HashMap<>();
        }

        public List<Show> searchShows(String movieName) {
            List<Show> result = new ArrayList<>();
            for (Show show : shows) {
                if (show.getEvent() instanceof Movie movie && movie.getName().equalsIgnoreCase(movieName)) {
                    result.add(show);
                }
            }

            return result;
        }

        public Payment processPayment(BigDecimal amount) {
            Payment payment = new Payment(UUID.randomUUID().toString(), amount);
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            return payment;
        }

        public Booking bookSeats(User user, Show show, List<String> seatIds) {
            List<ShowSeat> selectedShowSeats = new ArrayList<>();
            List<ReentrantLock> acquiredLocks = new ArrayList<>();

            try {
                // lock seats
                for (String seatId : seatIds) {
                    ShowSeat showSeat = show.getSeat(seatId);
                    if (showSeat == null) {
                        throw new RuntimeException(seatId + " not found");
                    }

                    showSeat.lock.lock();
                    acquiredLocks.add(showSeat.lock);

                    if (showSeat.getStatus() == SeatStatus.BOOKED) {
                        throw new RuntimeException(seatId + " already booked");
                    }

                    selectedShowSeats.add(showSeat);
                }

                BigDecimal amount = BigDecimal.ZERO;

                for (ShowSeat showSeat : selectedShowSeats) {
                    amount = amount.add(showSeat.getPrice());
                }

                Payment payment = processPayment(amount);

                for (ShowSeat showSeat : selectedShowSeats) {
                    showSeat.setStatus(SeatStatus.BOOKED);
                }

                Booking booking = new Booking(UUID.randomUUID().toString(), user, show, selectedShowSeats);
                booking.setStatus(BookingStatus.CONFIRMED);
                bookings.put(booking.bookingId, booking);

                return booking;

            } finally {
                for (ReentrantLock lock : acquiredLocks) {
                    lock.unlock();
                }
            }
        }

        public void cancelBooking(String bookingId) {
            Booking booking = bookings.get(bookingId);

            if (booking == null) {
                throw new RuntimeException("Booking not found");
            }

            if (booking.getStatus() == BookingStatus.CANCELLED) {
                return;
            }

            for (ShowSeat showSeat : booking.showSeats) {
                showSeat.setStatus(SeatStatus.AVAILABLE);
            }

            booking.setStatus(BookingStatus.CANCELLED);
            System.out.println("Booking cancelled : " + bookingId);
        }
    }

    public static void main(String[] args) {

        Movie movie = new Movie("M1", "Dhurandar");
        List<ShowSeat> showSeats = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            showSeats.add(new ShowSeat("S" + i, SeatType.SILVER, BigDecimal.valueOf(200)));
        }

        for (int i = 6; i <= 8; i++) {
            showSeats.add(new ShowSeat("G" + i, SeatType.GOLD, BigDecimal.valueOf(350)));
        }

        for (int i = 9; i <= 10; i++) {
            showSeats.add(new ShowSeat("P" + i, SeatType.PLATINUM, BigDecimal.valueOf(500)));
        }

        Show show = new Show("SHOW1", movie, showSeats);

        List<Show> shows = List.of(show);
        BookingService bookingService = new BookingService(shows);

        User user1 = new User("U1", "Alice");
        User user2 = new User("U2", "Bob");

        System.out.println("Search Result : " + bookingService.searchShows("Interstellar").size());

        Thread t1 = new Thread(() -> {
            try {
                Booking booking = bookingService.bookSeats(user1, show, Arrays.asList("S1", "S2"));
                System.out.println("SUCCESS : " + booking);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                Booking booking = bookingService.bookSeats(user2, show, Arrays.asList("S2", "S3"));
                System.out.println("SUCCESS : " + booking);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        t1.start();
        t2.start();
    }
}