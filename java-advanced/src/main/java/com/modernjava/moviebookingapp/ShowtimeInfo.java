package com.modernjava.moviebookingapp;

import java.time.LocalTime;
import java.util.List;

/**
 * Per-movie showtime configuration loaded from movies.properties: the list
 * of time-of-day slots offered, the theatre/screen, the ticket price, and
 * the seat-grid dimensions used for seat selection.
 */
public record ShowtimeInfo(List<LocalTime> times, String theatre, double price,
                            int rows, int seatsPerRow) {
}
