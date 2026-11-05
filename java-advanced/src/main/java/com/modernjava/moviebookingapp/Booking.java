package com.modernjava.moviebookingapp;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * A confirmed (or pending) booking made by a user for a {@link Showtime}.
 */
public class Booking {

    private final String bookingId;
    private final String userName;
    private final Showtime showtime;
    private final List<String> seats;
    private BookingStatus status;

    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED
    }

    public Booking(String userName, Showtime showtime, List<String> seats) {
        this.bookingId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.userName = userName;
        this.showtime = showtime;
        this.seats = seats;
        this.status = BookingStatus.PENDING;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getUserName() {
        return userName;
    }

    public Showtime getShowtime() {
        return showtime;
    }

    public List<String> getSeats() {
        return seats;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return seats.size() * showtime.getTicketPrice();
    }

    @Override
    public String toString() {
        return "Booking{id='" + bookingId + "', user='" + userName +
                "', movie='" + showtime.getMovie().getTitle() + "', seats=" + seats +
                ", total=$" + getTotalPrice() + ", status=" + status + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Booking b)) return false;
        return Objects.equals(bookingId, b.bookingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }
}
