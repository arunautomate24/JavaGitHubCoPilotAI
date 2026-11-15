# MovieBookingApp

`MovieBookingApp` is the console **entry point** (`main` method) for the Movie
Booking App. It wires together the data (movie catalog, showtimes), the
service layer (`MovieBookingService`), and user input (`Scanner`) to drive the
end-to-end flow shown in `MovieBooking.md`:

```
Home/Movies -> Movie Detail (Overview) -> Hero Actions (Play/Watchlist/Like)
           -> Booking Flow (Showtime -> Seats -> Confirm)
```

It contains no business logic of its own — it only reads user choices and
delegates to collaborator classes.

## Collaborators

| Class | Responsibility |
|---|---|
| `MovieCatalogLoader` | Reads `movies.properties` from the classpath and builds the `Movie` catalog plus each movie's `ShowtimeInfo` (timings, theatre, price, seat-grid size). |
| `Movie` | Immutable model for a movie's metadata (title, rating, duration, genres, description, production details, cast, keywords). |
| `ShowtimeInfo` | Simple holder for a movie's available showtimes, theatre, ticket price, and seat-grid dimensions (rows × seats/row). |
| `Showtime` | A specific bookable screening of a `Movie`; owns the seat map (`boolean[][]`) and exposes seat availability/booking operations. |
| `Booking` | A confirmed/pending reservation: user name, showtime, seat codes, status, total price. |
| `MovieBookingService` | Service layer — browsing, viewing details, hero actions (play/watchlist/like), and the booking flow (find showtimes, book seats, confirm/cancel). |
| `CastMember` | Small record/class representing an actor → character pairing shown in "Top Cast". |

## Workflow (step-by-step)

1. **Bootstrap**
   - Creates a `MovieBookingService`.
   - Loads the full movie catalog via `MovieCatalogLoader.loadFromProperties()` and registers each movie with `service.addMovieToCatalog(movie)`.
   - Loads a `Map<Movie, ShowtimeInfo>` via `MovieCatalogLoader.loadShowtimeInfo(catalog)` so each movie's showtimes/theatre/price/seat-grid are available without hardcoding.

2. **Browse movies** (`Home > Movies`)
   - Calls `service.browseMovies()` to print the catalog (title, rating, duration, genres).
   - Prints a numbered list of all movies and prompts the user to pick one (`Select a movie (1-N)`).
   - Falls back to movie `#1` if the input is missing or out of range.

3. **Select a showtime**
   - Looks up the selected movie's `ShowtimeInfo` (times, theatre, price, rows, seats/row).
   - Builds today's `Showtime` objects (one per time slot) and registers them with `service.addShowtime(s)`.
   - Prints the available showtimes and prompts the user to choose one, defaulting to the first if input is invalid.

4. **Movie detail page**
   - Calls `service.viewMovieDetails(selectedMovie)` to print the "Overview" tab: rating/duration/genres/year/certification, description, production details (director, writers, studio, language, budget), top cast, and keywords.

5. **Hero banner actions**
   - `service.playMovie(selectedMovie)` — simulates pressing "▶️ Play Movie".
   - `service.addToWatchlist(selectedMovie)` — simulates "➕ Add to Watchlist".
   - `service.likeMovie(selectedMovie)` — simulates "👍 Like".

6. **Booking flow**
   - Prints the chosen showtime's seat map (`showtime.printSeatMap()`) and its list of available seat codes (e.g. `A1`, `C4`).
   - Prompts the user to either:
     - Type specific seat codes (comma-separated, e.g. `C3,C4`) — each seat is validated via `showtime.isSeatAvailable(seat)`; invalid/taken seats are skipped with a message, or
     - Press **Enter** for random selection — asks how many seats to book, then shuffles the available seats and picks that many.
   - Calls `service.bookTickets("ArunKumar", showtime, seats)`, which validates all seats are free, marks them booked on the `Showtime`, and creates a `Booking` (status `PENDING`).
   - Calls `service.confirmBooking(booking)`, which sets the status to `CONFIRMED` and prints the total price (`seats.size() * ticketPrice`).

7. **Final output**
   - Reprints the seat map (now showing booked seats as `[X]`).
   - Prints the user's "My List" (watchlist) via `service.getMyList()`.

## Key Design Notes

- **Data-driven catalog**: Movies and their showtime/pricing/seat-grid info live in `src/main/resources/movies.properties`, not hardcoded in Java — `MovieCatalogLoader` is the single place that parses this file.
- **Defensive input handling**: Every `Scanner` prompt validates the input (`hasNextInt()`, range checks) and falls back to a sensible default (first movie, first showtime, random seats) instead of crashing on bad input.
- **Seat codes**: Format is `<row letter><seat number>`, e.g. `C4` = row C (3rd row), seat 4. `Showtime` converts these to `[row][col]` indices internally.
- **Separation of concerns**: `MovieBookingApp` only orchestrates I/O; all domain rules (seat availability, booking totals, status transitions) live in `MovieBookingService`, `Showtime`, and `Booking`.

## Example Run

```
== Home > Movies ==
  * Inception | ★ 8.8 | 2h 28m | [Action, Sci-Fi]
  ...
Select a movie (1-20): 1

Available showtimes for Inception (Screen 1, $12.5/seat):
  1) 10:00
  2) 13:30
  ...
Select showtime (1-5): 2

================================================================
★ 8.8 | 2h 28m | [Action, Sci-Fi] | 2010 | PG-13
Inception
----------------------------------------------------------------
Description: A thief who steals corporate secrets...
...

▶️ Now playing: Inception
➕ Added to My List: Inception
👍 Liked: Inception

=== Ticket buyer books seats for Inception ===
Seat map for Inception @ Screen 1 (2026-11-05T13:30)
A [O] [O] [O] [O] [O] [O] [O] [O]
...
Available seats: [A1, A2, , ]
Enter seat(s) to book, comma-separated (e.g. C1, C4, C3), or press Enter for random selection: D3,C5
Selected seats: [C1, C4, C3]
Booking created: Booking{id='####', user='ArunKumar', movie='Inception', seats=[C1, C4, C3], total=$25.0, status=PENDING}
✅ Booking ... confirmed for ArunKumar. Total paid: $25.00

Seat map for Inception @ Screen 1 (2026-11-05T13:30)
...
My List: [Movie{title='Inception', ...}]
```
