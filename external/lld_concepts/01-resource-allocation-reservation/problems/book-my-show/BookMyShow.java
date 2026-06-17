import java.util.*;

public class BookMyShow {

    enum SeatStatus {
        AVAILABLE, BOOKED
    }

    static class Movie {
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

    static class Seat {
        private final String seatId;
        private SeatStatus status;

        public Seat(String seatId) {
            this.seatId = seatId;
            this.status = SeatStatus.AVAILABLE;
        }

        public String getSeatId() {
            return seatId;
        }

        public SeatStatus getStatus() {
            return status;
        }

        public void setStatus(SeatStatus status) {
            this.status = status;
        }
    }

    static class Show {

        private final String showId;
        private final Movie movie;
        private final Map<String, Seat> seats;

        public Show(String showId, Movie movie, List<Seat> seatList) {
            this.showId = showId;
            this.movie = movie;
            this.seats = new HashMap<>();

            for (Seat seat : seatList) {
                seats.put(seat.getSeatId(), seat);
            }
        }

        public Movie getMovie() {
            return movie;
        }

        public Seat getSeat(String seatId) {
            return seats.get(seatId);
        }

        public void printAvailableSeats() {
            System.out.println("Available Seats:");

            for (Seat seat : seats.values()) {
                if (seat.getStatus() == SeatStatus.AVAILABLE) {
                    System.out.print(seat.getSeatId() + " ");
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

    static class Booking {

        private final String bookingId;
        private final User user;
        private final Show show;
        private final List<Seat> seats;

        public Booking(String bookingId, User user, Show show, List<Seat> seats) {

            this.bookingId = bookingId;
            this.user = user;
            this.show = show;
            this.seats = seats;
        }

        @Override
        public String toString() {

            List<String> seatIds = new ArrayList<>();
            for (Seat seat : seats) {
                seatIds.add(seat.getSeatId());
            }
            return "BookingId : " + bookingId + ", User : " + user.getName() + ", Seats : " + seatIds;
        }
    }

    static class BookingService {

        public Booking bookSeats(User user, Show show, List<String> seatIds) {
            List<Seat> selectedSeats = new ArrayList<>();

            // Lock all booking operation
            synchronized (show) {
                // Validation
                for (String seatId : seatIds) {
                    Seat seat = show.getSeat(seatId);

                    if (seat == null) {
                        throw new RuntimeException("Seat not found");
                    }

                    if (seat.getStatus() == SeatStatus.BOOKED) {
                        throw new RuntimeException(seatId + " already booked");
                    }

                    selectedSeats.add(seat);
                }

                // Mark booked
                for (Seat seat : selectedSeats) {
                    seat.setStatus(SeatStatus.BOOKED);
                }

                return new Booking(UUID.randomUUID().toString(), user, show, selectedSeats);
            }
        }
    }

    public static void main(String[] args) {

        Movie movie = new Movie("M1", "Interstellar");
        List<Seat> seats = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            seats.add(new Seat("A" + i));
        }

        Show show = new Show("S1", movie, seats);
        User user1 = new User("U1", "User1");
        User user2 = new User("U2", "User2");

        BookingService bookingService = new BookingService();
        show.printAvailableSeats();

        Runnable task1 = () -> {
            try {
                Booking booking = bookingService.bookSeats(user1, show, Arrays.asList("A1", "A2"));
                System.out.println("SUCCESS -> " + booking);

            } catch (Exception e) {
                System.out.println("FAILED -> " + e.getMessage());
            }
        };

        Runnable task2 = () -> {
            try {
                Booking booking = bookingService.bookSeats(user2, show, Arrays.asList("A2", "A3"));
                System.out.println("SUCCESS -> " + booking);
            } catch (Exception e) {
                System.out.println("FAILED -> " + e.getMessage());
            }
        };

        Thread t1 = new Thread(task1);
        Thread t2 = new Thread(task2);

        t1.start();
        t2.start();
    }
}