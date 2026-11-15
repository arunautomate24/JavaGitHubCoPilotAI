# Movie Booking App — Wireframe

This wireframe is created for deriving `MovieBookingApp_ClassDiagram.md`'s class
diagram. Each screen below is annotated with the exact class/method calls
(MovieBookingApp_UML.md) that produce it, and a Mermaid sequence diagram shows the
full object interaction flow end-to-end, including the cancellation/refund
stub.

---

## 1. Class → Screen mapping

| UML Class | Member(s) used | Screen(s) it drives |
|---|---|---|
| `MovieCatalogLoader` | `loadFromProperties()`, `loadShowtimeInfo()` | Screen 1 (Catalog), Screen 4 (Showtimes) data source |
| `MovieBookingService` | `browseMovies()` | Screen 1 — Catalog list |
| `Movie` | `getTitle()`, `getRating()`, `formattedDuration()`, `getGenres()`, `getDescription()`, `getDirector()`, `getWriters()`, `getStudio()`, `getLanguage()`, `getBudget()`, `getCast()`, `getKeywords()` | Screen 2 — Movie Detail (Overview) |
| `CastMember` | `toString()` ("actor -> character") | Screen 2 — Top Cast rows |
| `MovieBookingService` | `viewMovieDetails(Movie)` | Screen 2 — renders all `Movie` fields |
| `MovieBookingService` | `playMovie()`, `addToWatchlist()`, `likeMovie()` | Screen 3 — Hero Actions |
| `ShowtimeInfo` | `times()`, `theatre()`, `price()` | Screen 4 — Showtime Selection |
| `Showtime` | constructor, `printSeatMap()`, `getAvailableSeats()`, `isSeatAvailable()` | Screen 5 — Seat Map + Selection |
| `MovieBookingService` | `bookTickets()`, `confirmBooking()` | Screen 6 — Booking + Confirmation |
| `Booking` | `getBookingId()`, `getTotalPrice()`, `getStatus()` | Screen 6 — Confirmation summary |
| `BookingStatus` | `PENDING` → `CONFIRMED` | Screen 6 — status transition |
| `Showtime` | `releaseSeat(String)` *(stub)* | Screen 7 — Cancellation (future) |
| `MovieBookingService` | `calculateRefundAmount()`, `cancelBookingWithRefund()` *(stub)* | Screen 7 — Refund (future) |

---

## 2. Screens

### Screen 1 — Catalog (via `MovieBookingService.browseMovies()` → `Movie` list)
```
== Home > Movies ==
  1) Inception            ★ 8.8 | 2h 28m | Action, Sci-Fi
  2) Avengers: Endgame     ★ 8.4 | 3h 01m | Action, Adventure
  ...
Select a movie (1-N): _
```
`browseMovies()` iterates the `catalog : List<Movie>` field and calls
`Movie.getTitle()`, `getRating()`, `formattedDuration()`, `getGenres()` for
each row.

### Screen 2 — Movie Detail / Overview (via `viewMovieDetails(Movie)`)
```
★ 8.8 | 2h 28m | Action, Sci-Fi | 2010 | PG-13
Inception
----------------------------------------------------------------
Description: A thief who steals corporate secrets...

Production Details
  Director: Christopher Nolan
  Writers: [Christopher Nolan]
  Studio: Warner Bros. Pictures
  Language: English
  Budget: $160,000,000

Top Cast
  [O] Leonardo D. -> Dom Cobb        <-- CastMember.toString()
  [O] Elliot P.   -> Ariadne         <-- CastMember.toString()

Keywords: [Dreams, Sci-Fi, Heist]
```
Each line maps directly to a `Movie` getter; the cast block iterates
`Movie.getCast() : List<CastMember>`.

### Screen 3 — Hero Actions (via `playMovie`, `addToWatchlist`, `likeMovie`)
```
▶️  Now playing: Inception          <-- playMovie(movie)
➕  Added to My List: Inception      <-- addToWatchlist(movie)  (watchlist : Set<Movie>)
👍  Liked: Inception                 <-- likeMovie(movie)        (liked : Set<Movie>)
```

### Screen 4 — Showtime Selection (via `ShowtimeInfo`)
```
Available showtimes for Inception (Screen 1, $12.5/seat):
  1) 10:00
  2) 13:30
  ...
Select showtime (1-N): _
```
`ShowtimeInfo.times()` populates the numbered list; `theatre()` and
`price()` populate the header line. `MovieBookingApp` then constructs a
`Showtime` per time slot and calls `MovieBookingService.addShowtime(s)`.

### Screen 5 — Seat Map + Selection (via `Showtime`)
```
Seat map for Inception @ Screen 1 (2026-11-05T13:30)   <-- Showtime.printSeatMap()
A [O] [O] [O] [O] [O] [O] [O] [O]
B [O] [O] [O] [O] [O] [O] [O] [O]
C [O] [O] [O] [O] [O] [O] [O] [O]

Available seats: [A1, A2, ... E8]                        <-- Showtime.getAvailableSeats()

Seat selection options:
  1) Default seats (C1,C4,C3)
  2) Choose your own seats          <-- Showtime.isSeatAvailable(seat) validates each
  3) AI random selection
Select an option (1-3): _
```
`seatMap : boolean[][]` inside `Showtime` is the source of truth for every
`[O]`/`[X]` rendered.

### Screen 6 — Booking + Confirmation (via `Booking`, `BookingStatus`)
```
Booking created: Booking{id='1D8EC464', user='Arun Kumar', movie='Inception',
                          seats=[C1, C4, C3], total=$37.50, status=PENDING}
                                                          <-- bookTickets() -> new Booking(...)
✅ Booking 1D8EC464 confirmed for Arun Kumar. Total paid: $37.50
                                                          <-- confirmBooking() -> status = CONFIRMED
Seat map for Inception @ Screen 1 (2026-11-05T13:30)
C [X] [O] [X] [X] [O] [O] [O] [O]                        <-- seatMap updated via bookSeat()

My List: [Inception]                                     <-- getMyList() -> watchlist : Set<Movie>
```

### Screen 7 — Cancellation & Refund *(future, stub only — not wired into `MovieBookingApp`)*
```
== My Bookings ==
  1) 1D8EC464 | Inception | 2026-11-05 13:30 | Seats: C1,C4,C3 | $37.50 | CONFIRMED
Select a booking to cancel (1-N): 1

Cancelling now is 18h 45m before showtime -> 100% refund eligible.
Are you sure? (y/n): y

❌ Booking 1D8EC464 cancelled. Refund issued: $37.50      <-- cancelBookingWithRefund()
Seats C1, C4, C3 released back to the seat map.           <-- Showtime.releaseSeat(seat) x3
```
Backed by `MovieBookingService.calculateRefundAmount(Booking)` (policy
lookup) and `cancelBookingWithRefund(Booking)` (orchestrates
`Showtime.releaseSeat()` + `Booking.setStatus(CANCELLED)`).

---

## 3. End-to-end sequence diagram

See **`MovieBookingApp_SequenceDiagram.md`** for the full Mermaid sequence
diagram of object interactions (including the cancellation/refund stub),
extracted from this document into its own file.

---

## 4. Notes

- This wireframe is a direct visual rendering of the class/method surface
  described in `MovieBookingApp_ClassDiagram.md` — every screen element traces back
  to a specific class member in that diagram.
- Screen 7 and its sequence-diagram section are clearly marked **future /
  stub-only**: the methods exist and compile (`Showtime.releaseSeat`,
  `MovieBookingService.calculateRefundAmount`,
  `MovieBookingService.cancelBookingWithRefund`) but `MovieBookingApp`'s
  `main` method does not yet call them.
- For the full refund policy (100%/50%/0% thresholds), see
  `MovieBookingApp_Wireframe.md` and `../../../requirementdocs/MovieBookingApp_PRD.md` (Epic 7).
