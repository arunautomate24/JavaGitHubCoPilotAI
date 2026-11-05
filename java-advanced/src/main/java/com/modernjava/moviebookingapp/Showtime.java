package com.modernjava.moviebookingapp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A bookable showtime (screening) for a {@link Movie}, with a fixed-size
 * seat grid. Seats are addressed by row letter + seat number, e.g. "A1".
 */
public class Showtime {

    private final Movie movie;
    private final LocalDateTime startTime;
    private final String theatre;
    private final double ticketPrice;
    private final boolean[][] seatMap; // true = booked

    public Showtime(Movie movie, LocalDateTime startTime, String theatre,
                     double ticketPrice, int rows, int seatsPerRow) {
        this.movie = movie;
        this.startTime = startTime;
        this.theatre = theatre;
        this.ticketPrice = ticketPrice;
        this.seatMap = new boolean[rows][seatsPerRow];
    }

    public Movie getMovie() {
        return movie;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getTheatre() {
        return theatre;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public int getRowCount() {
        return seatMap.length;
    }

    public int getSeatsPerRow() {
        return seatMap.length == 0 ? 0 : seatMap[0].length;
    }

    public boolean isSeatAvailable(String seatCode) {
        int[] rc = parseSeatCode(seatCode);
        return !seatMap[rc[0]][rc[1]];
    }

    /**
     * Returns the seat codes (e.g. "A1", "A2", ...) that are not yet booked,
     * used to pick seats randomly at runtime.
     */
    public List<String> getAvailableSeats() {
        List<String> available = new ArrayList<>();
        for (int r = 0; r < seatMap.length; r++) {
            for (int s = 0; s < seatMap[r].length; s++) {
                if (!seatMap[r][s]) {
                    available.add("" + (char) ('A' + r) + (s + 1));
                }
            }
        }
        return available;
    }

    public void bookSeat(String seatCode) {
        int[] rc = parseSeatCode(seatCode);
        if (seatMap[rc[0]][rc[1]]) {
            throw new IllegalStateException("Seat " + seatCode + " is already booked.");
        }
        seatMap[rc[0]][rc[1]] = true;
    }

    /**
     * Frees a previously booked seat, making it available again. Used by
     * the (future) booking-cancellation flow to return seats to the pool.
     */
    public void releaseSeat(String seatCode) {
        int[] rc = parseSeatCode(seatCode);
        seatMap[rc[0]][rc[1]] = false;
    }

    /**
     * Converts a seat code like "C5" into {row index, seat index}.
     */
    private int[] parseSeatCode(String seatCode) {
        char rowLetter = Character.toUpperCase(seatCode.charAt(0));
        int row = rowLetter - 'A';
        int seat = Integer.parseInt(seatCode.substring(1)) - 1;
        if (row < 0 || row >= seatMap.length || seat < 0 || seat >= getSeatsPerRow()) {
            throw new IllegalArgumentException("Invalid seat code: " + seatCode);
        }
        return new int[]{row, seat};
    }

    public void printSeatMap() {
        System.out.println("Seat map for " + movie.getTitle() + " @ " + theatre + " (" + startTime + ")");
        for (int r = 0; r < seatMap.length; r++) {
            StringBuilder sb = new StringBuilder((char) ('A' + r) + " ");
            for (int s = 0; s < seatMap[r].length; s++) {
                sb.append(seatMap[r][s] ? "[X] " : "[O] ");
            }
            System.out.println(sb);
        }
    }

    @Override
    public String toString() {
        return movie.getTitle() + " @ " + theatre + " on " + startTime +
                " ($" + ticketPrice + "/seat)";
    }
}
