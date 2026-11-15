# Movie Booking App — Sequence Diagram

This sequence diagram was extracted from `MovieBookingApp_Lo-Fi_Wireframe.md`
(section 3). It shows the end-to-end object interaction flow per the class
diagram in `MovieBookingApp_ClassDiagram.md`, including the cancellation/refund stub.

## End-to-end sequence diagram (object interactions per the MovieBookingApp Class Diagram)

```mermaid
sequenceDiagram
    actor Buyer as Ticket Buyer
    participant App as MovieBookingApp
    participant Loader as MovieCatalogLoader
    participant Svc as MovieBookingService
    participant Mv as Movie
    participant ST as Showtime
    participant Bk as Booking

    App->>Loader: loadFromProperties()
    Loader-->>App: List<Movie>
    App->>Loader: loadShowtimeInfo(catalog)
    Loader-->>App: Map<Movie, ShowtimeInfo>
    App->>Svc: addMovieToCatalog(movie) [for each]

    Buyer->>App: select movie #
    App->>Svc: browseMovies()
    Svc->>Mv: getTitle()/getRating()/formattedDuration()/getGenres()
    Svc-->>Buyer: printed catalog

    Buyer->>App: select showtime #
    App->>ST: new Showtime(movie, time, theatre, price, rows, seatsPerRow)
    App->>Svc: addShowtime(showtime)

    App->>Svc: viewMovieDetails(movie)
    Svc->>Mv: getters (description, director, cast, keywords...)
    Svc-->>Buyer: printed detail page

    App->>Svc: playMovie(movie) / addToWatchlist(movie) / likeMovie(movie)

    App->>ST: printSeatMap() / getAvailableSeats()
    Buyer->>App: choose seat option (1/2/3)
    App->>ST: isSeatAvailable(seat) [per seat]

    App->>Svc: bookTickets(userName, showtime, seats)
    Svc->>ST: isSeatAvailable(seat) [re-check]
    Svc->>ST: bookSeat(seat) [for each]
    Svc->>Bk: new Booking(userName, showtime, seats)
    Bk-->>Svc: booking (status=PENDING)

    App->>Svc: confirmBooking(booking)
    Svc->>Bk: setStatus(CONFIRMED)
    Svc-->>Buyer: "Booking confirmed. Total paid: $..."

    Note over Svc,ST: --- FUTURE: Cancellation & Refund (stub) ---
    Buyer->>App: (future) select "My Bookings" > cancel
    App->>Svc: cancelBookingWithRefund(booking)
    Svc->>Svc: calculateRefundAmount(booking)
    Svc->>ST: releaseSeat(seat) [for each]
    Svc->>Bk: setStatus(CANCELLED)
    Svc-->>Buyer: "Booking cancelled. Refund issued: $..."
```

## Notes

- Screen/step order matches the 7 screens described in
  `MovieBookingApp_Lo-Fi_Wireframe.md`.
- The final block (cancellation/refund) is **future/stub-only**: the
  underlying methods (`Showtime.releaseSeat`,
  `MovieBookingService.calculateRefundAmount`,
  `MovieBookingService.cancelBookingWithRefund`) exist and compile, but
  `MovieBookingApp`'s `main` method does not yet call them.
- For the full refund policy (100%/50%/0% thresholds), see
  `MovieBookingApp_Wireframe.md` and `../../../requirementdocs/MovieBookingApp_PRD.md` (Epic 7).
