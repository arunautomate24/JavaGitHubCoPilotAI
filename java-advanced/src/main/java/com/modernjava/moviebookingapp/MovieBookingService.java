package com.modernjava.moviebookingapp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service layer for the Movie Booking App. Backs the actions shown in
 * MovieBooking.md: browsing the top nav tabs, viewing a movie's Overview
 * page, Play/Add to Watchlist/Like buttons, and the ticket booking flow
 * (choose showtime -> pick seats -> confirm booking).
 */
public class MovieBookingService {

    private final List<Movie> catalog = new ArrayList<>();
    private final List<Showtime> showtimes = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private final Set<Movie> watchlist = new HashSet<>();
    private final Set<Movie> liked = new HashSet<>();

    public void addMovieToCatalog(Movie movie) {
        catalog.add(movie);
    }

    public void addShowtime(Showtime showtime) {
        showtimes.add(showtime);
    }

    // ---- "Home / Movies" tab: browse the catalog ----
    public void browseMovies() {
        System.out.println("== Home > Movies ==");
        for (Movie movie : catalog) {
            System.out.printf("  * %s | \u2605 %.1f | %s | %s%n",
                    movie.getTitle(), movie.getRating(), movie.formattedDuration(), movie.getGenres());
        }
    }

    // ---- Movie detail page: hero banner + Overview tab ----
    public void viewMovieDetails(Movie movie) {
        System.out.println("================================================================");
        System.out.printf("\u2605 %.1f | %s | %s | %d | %s%n",
                movie.getRating(), movie.formattedDuration(), movie.getGenres(),
                movie.getReleaseYear(), movie.getCertification());
        System.out.println(movie.getTitle());
        System.out.println("----------------------------------------------------------------");
        System.out.println("Description: " + movie.getDescription());
        System.out.println();
        System.out.println("Production Details");
        System.out.println("  Director: " + movie.getDirector());
        System.out.println("  Writers: " + movie.getWriters());
        System.out.println("  Studio: " + movie.getStudio());
        System.out.println("  Language: " + movie.getLanguage());
        System.out.printf("  Budget: $%,.0f%n", movie.getBudget());
        System.out.println();
        System.out.println("Top Cast");
        for (CastMember member : movie.getCast()) {
            System.out.println("  [O] " + member);
        }
        System.out.println();
        System.out.println("Keywords: " + movie.getKeywords());
        System.out.println("================================================================");
    }

    // ---- Hero banner action buttons ----
    public void playMovie(Movie movie) {
        System.out.println("\u25b6\ufe0f Now playing: " + movie.getTitle());
    }

    public void addToWatchlist(Movie movie) {
        watchlist.add(movie);
        System.out.println("\u2795 Added to My List: " + movie.getTitle());
    }

    public void likeMovie(Movie movie) {
        liked.add(movie);
        System.out.println("\ud83d\udc4d Liked: " + movie.getTitle());
    }

    public List<Movie> getMyList() {
        return new ArrayList<>(watchlist);
    }

    // ---- Booking flow: pick a showtime, choose seats, confirm ----
    public List<Showtime> findShowtimesFor(Movie movie) {
        List<Showtime> result = new ArrayList<>();
        for (Showtime s : showtimes) {
            if (s.getMovie().equals(movie)) {
                result.add(s);
            }
        }
        return result;
    }

    public Booking bookTickets(String userName, Showtime showtime, List<String> seatCodes) {
        for (String seatCode : seatCodes) {
            if (!showtime.isSeatAvailable(seatCode)) {
                throw new IllegalStateException("Seat " + seatCode + " is already taken.");
            }
        }
        for (String seatCode : seatCodes) {
            showtime.bookSeat(seatCode);
        }
        Booking booking = new Booking(userName, showtime, seatCodes);
        bookings.add(booking);
        System.out.println("Booking created: " + booking);
        return booking;
    }

    public void confirmBooking(Booking booking) {
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        System.out.printf("\u2705 Booking %s confirmed for %s. Total paid: $%.2f%n",
                booking.getBookingId(), booking.getUserName(), booking.getTotalPrice());
    }

    // ---- Cancellation & refund (STUB for a future "My Bookings" screen;
    // not yet wired into MovieBookingApp's console menu) ----

    /**
     * Refund policy, measured from "now" to the showtime's start time:
     * <ul>
     *   <li>24+ hours before showtime: 100% refund</li>
     *   <li>2-24 hours before showtime: 50% refund</li>
     *   <li>less than 2 hours before, or after the showtime has started: 0% refund</li>
     * </ul>
     * Already-cancelled bookings are not eligible for a (further) refund.
     */
    public double calculateRefundAmount(Booking booking) {
        if (booking == null || booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            return 0.0;
        }
        long hoursUntilShowtime = java.time.Duration.between(
                java.time.LocalDateTime.now(), booking.getShowtime().getStartTime()).toHours();
        double refundRate;
        if (hoursUntilShowtime >= 24) {
            refundRate = 1.0;
        } else if (hoursUntilShowtime >= 2) {
            refundRate = 0.5;
        } else {
            refundRate = 0.0;
        }
        return booking.getTotalPrice() * refundRate;
    }

    /**
     * Cancels a booking without applying the refund policy. Kept for
     * backward compatibility; prefer {@link #cancelBookingWithRefund(Booking)}.
     */
    public void cancelBooking(Booking booking) {
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        System.out.println("\u274c Booking " + booking.getBookingId() + " cancelled.");
    }

    /**
     * STUB: cancels a booking, releases its seats back to the showtime's
     * seat map, and issues a refund per {@link #calculateRefundAmount(Booking)}.
     * Not yet exposed via the console UI in {@code MovieBookingApp} — wire
     * this up once a "My Bookings" / "Cancel Booking" screen is added.
     */
    public double cancelBookingWithRefund(Booking booking) {
        if (booking == null || booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            System.out.println("Booking is invalid or already cancelled; no refund issued.");
            return 0.0;
        }
        double refundAmount = calculateRefundAmount(booking);
        for (String seatCode : booking.getSeats()) {
            booking.getShowtime().releaseSeat(seatCode);
        }
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        System.out.printf("\u274c Booking %s cancelled. Refund issued: $%.2f%n",
                booking.getBookingId(), refundAmount);
        return refundAmount;
    }

    public List<Booking> getBookings() {
        return new ArrayList<>(bookings);
    }
}
