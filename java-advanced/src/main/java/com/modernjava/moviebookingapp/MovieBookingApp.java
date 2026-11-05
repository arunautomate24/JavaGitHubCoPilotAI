package com.modernjava.moviebookingapp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Console demo of the Movie Booking App described in MovieBooking.md:
 * Home/Movies browsing -> movie detail page (Overview tab, cast, production
 * details, keywords) -> hero actions (Play/Watchlist/Like) -> booking flow
 * (showtime selection, seat selection, payment confirmation).
 *
 * <p>The movie catalog, each movie's showtimes/theatre/price/seat-grid, and
 * the demo seat selection are all read from
 * src/main/resources/movies.properties via {@link MovieCatalogLoader}.
 */
public class MovieBookingApp {

    public static void main(String[] args) {
        MovieBookingService service = new MovieBookingService();

        // Load the 20-movie catalog from movies.properties and add each one
        // via addMovieToCatalog, instead of hardcoding movies.
        List<Movie> catalog = MovieCatalogLoader.loadFromProperties();
        for (Movie movie : catalog) {
            service.addMovieToCatalog(movie);
        }

        // Load each movie's showtimes/theatre/price/seat-grid from the same
        // properties file.
        Map<Movie, ShowtimeInfo> showtimeInfoByMovie = MovieCatalogLoader.loadShowtimeInfo(catalog);

        Scanner scanner = new Scanner(System.in);

        // [ Top Navigation Bar ] Home > Movies - let the user pick a movie
        // from the full catalog loaded from movies.properties.
        service.browseMovies();
        System.out.println();
        for (int i = 0; i < catalog.size(); i++) {
            System.out.printf("  %d) %s%n", i + 1, catalog.get(i).getTitle());
        }
        System.out.print("Select a movie (1-" + catalog.size() + "): ");
        int movieChoice = 1;
        if (scanner.hasNextInt()) {
            movieChoice = scanner.nextInt();
        }
        if (movieChoice < 1 || movieChoice > catalog.size()) {
            movieChoice = 1;
        }
        Movie selectedMovie = catalog.get(movieChoice - 1);
        ShowtimeInfo info = showtimeInfoByMovie.get(selectedMovie);

        // Build today's showtimes for the selected movie from its properties
        // (timings, theatre, price, and seat-grid dimensions).
        LocalDate today = LocalDate.now();
        List<Showtime> showtimes = new ArrayList<>();
        for (LocalTime time : info.times()) {
            Showtime s = new Showtime(
                    selectedMovie, today.atTime(time),
                    info.theatre(), info.price(), info.rows(), info.seatsPerRow());
            service.addShowtime(s);
            showtimes.add(s);
        }

        System.out.println("Available showtimes for " + selectedMovie.getTitle() +
                " (" + info.theatre() + ", $" + info.price() + "/seat):");
        for (int i = 0; i < showtimes.size(); i++) {
            System.out.printf("  %d) %s%n", i + 1, info.times().get(i));
        }
        System.out.print("Select showtime (1-" + showtimes.size() + "): ");
        int choice = 1;
        if (scanner.hasNextInt()) {
            choice = scanner.nextInt();
        }
        if (choice < 1 || choice > showtimes.size()) {
            choice = 1;
        }
        Showtime showtime = showtimes.get(choice - 1);

        // Movie detail page (Overview tab)
        System.out.println();
        service.viewMovieDetails(selectedMovie);

        // Hero banner actions
        System.out.println();
        service.playMovie(selectedMovie);
        service.addToWatchlist(selectedMovie);
        service.likeMovie(selectedMovie);

        // Booking flow
        System.out.println("\n=== Ticket buyer books seats for " + selectedMovie.getTitle() + " ===");
        showtime.printSeatMap();

        // Seat selection: let the user pick how seats are chosen.
        System.out.println("Available seats: " + showtime.getAvailableSeats());
        System.out.println("Seat selection options:");
        System.out.println("  1) Default seats (C1,C4,C3)");
        System.out.println("  2) Choose your own seats");
        System.out.println("  3) AI random selection");
        System.out.print("Select an option (1-3): ");
        int seatOption = 1;
        if (scanner.hasNextInt()) {
            seatOption = scanner.nextInt();
        }
        if (seatOption < 1 || seatOption > 3) {
            seatOption = 1;
        }
        if (scanner.hasNextLine()) {
            scanner.nextLine(); // consume leftover newline from the int reads above
        }

        List<String> seats;
        if (seatOption == 1) {
            // Option 1: default seat selection.
            List<String> defaultSeats = List.of("C1", "C4", "C3");
            seats = new ArrayList<>();
            for (String seat : defaultSeats) {
                try {
                    if (showtime.isSeatAvailable(seat)) {
                        seats.add(seat);
                    } else {
                        System.out.println("Default seat " + seat + " is already booked; skipping.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Default seat '" + seat + "' is invalid for this theatre; skipping.");
                }
            }
            if (seats.isEmpty()) {
                System.out.println("No default seats available; picking seats randomly instead.");
                List<String> availableSeats = new ArrayList<>(showtime.getAvailableSeats());
                Collections.shuffle(availableSeats, new Random());
                seats = new ArrayList<>(availableSeats.subList(0, Math.min(2, availableSeats.size())));
            }
            System.out.println("Default selected seats: " + seats);
        } else if (seatOption == 2) {
            // Option 2: user chooses their own seats.
            System.out.print("Enter seat(s) to book, comma-separated (e.g. C1): ");
            String seatInput = scanner.nextLine().trim();
            seats = new ArrayList<>();
            for (String code : seatInput.split(",")) {
                String seat = code.trim().toUpperCase();
                if (seat.isEmpty()) {
                    continue;
                }
                try {
                    if (showtime.isSeatAvailable(seat)) {
                        seats.add(seat);
                    } else {
                        System.out.println("Seat " + seat + " is already booked; skipping.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid seat code '" + seat + "'; skipping.");
                }
            }
            if (seats.isEmpty()) {
                System.out.println("No valid seats entered; picking seats randomly instead.");
                List<String> availableSeats = new ArrayList<>(showtime.getAvailableSeats());
                Collections.shuffle(availableSeats, new Random());
                seats = new ArrayList<>(availableSeats.subList(0, Math.min(2, availableSeats.size())));
            }
            System.out.println("Selected seats: " + seats);
        } else {
            // Option 3: AI random selection.
            int availableCount = showtime.getAvailableSeats().size();
            System.out.print("How many seats would you like to book (1-" + availableCount + "): ");
            int seatCount = 2;
            if (scanner.hasNextInt()) {
                seatCount = scanner.nextInt();
            }
            if (seatCount < 1 || seatCount > availableCount) {
                seatCount = Math.min(2, availableCount);
            }
            List<String> availableSeats = new ArrayList<>(showtime.getAvailableSeats());
            Collections.shuffle(availableSeats, new Random());
            seats = new ArrayList<>(availableSeats.subList(0, seatCount));
            System.out.println("Randomly selected seats: " + seats);
        }
        Booking booking = service.bookTickets("Arun Kumar", showtime, seats);
        service.confirmBooking(booking);

        System.out.println();
        showtime.printSeatMap();
        System.out.println("\nMy List: " + service.getMyList());
    }
}
