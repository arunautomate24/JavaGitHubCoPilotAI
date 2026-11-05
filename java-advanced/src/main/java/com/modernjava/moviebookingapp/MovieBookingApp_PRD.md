# Product Requirements Document (PRD): Movie Booking App

| Field | Value |
|---|---|
| **Product** | Movie Booking App (console demo) |
| **Epic Key** | MOVIE-APP |
| **Owner** | Arun Kumar |
| **Status** | Draft |
| **Target Release** | v1.0 |
| **Related Files** | `MovieBookingApp.java`, `MovieBookingService.java`, `Movie.java`, `Showtime.java`, `Booking.java`, `MovieCatalogLoader.java`, `MovieBooking.md` |

---

## 1. Overview

The Movie Booking App is a console-based Java application that simulates a
streaming/ticketing experience similar to Netflix + a cinema booking site. A
user browses a catalog of movies, views movie details (cast, production info,
keywords), interacts with hero actions (Play, Watchlist, Like), selects a
showtime, picks seats, and confirms a paid booking.

This PRD describes the functional requirements as a set of epics/stories
suitable for import into JIRA, with acceptance criteria for each.

## 2. Goals

- Provide an end-to-end, text-based simulation of a movie discovery and
  ticket-booking workflow for teaching/demo purposes.
- Keep all movie, showtime, pricing, and seat-grid data externalized in
  `movies.properties` (no hardcoded catalog in Java).
- Support three distinct seat-selection modes to demonstrate different UX
  patterns: default/quick-pick, manual selection, and randomized ("AI") pick.

## 3. Non-Goals

- No persistent storage (database) — all state is in-memory for a single run.
- No real payment gateway integration — payment is simulated via a total
  price calculation.
- No GUI/web front end — console/CLI only.
- No multi-user concurrency or authentication.

## 4. User Personas

| Persona | Description |
|---|---|
| **Ticket Buyer** | End user browsing movies and booking seats for a showtime. |
| **Clerk** (future) | Staff member who can cancel/refund bookings on behalf of a buyer (modeled in `MovieReservationUtil`, not yet wired into this console app). |

## 5. Epics & User Stories

### Epic 1 — Movie Catalog (MOVIE-APP-1)
**Description:** Load and browse the movie catalog from external configuration.

- **MOVIE-APP-1.1** — As a ticket buyer, I want to see a list of all available
  movies with title, rating, duration, and genres, so I can decide what to
  watch.
  - *Acceptance Criteria:*
    - Catalog is loaded from `src/main/resources/movies.properties` via
      `MovieCatalogLoader.loadFromProperties()`.
    - Each movie displays: title, star rating, formatted duration (e.g.
      "2h 28m"), and genre list.
    - The app does not crash if the properties file is missing required
      fields for a movie (reasonable defaults/skip).

- **MOVIE-APP-1.2** — As a ticket buyer, I want to select a movie by number
  from the printed list.
  - *Acceptance Criteria:*
    - Prompt: `Select a movie (1-N)`.
    - Invalid or non-numeric input defaults to movie #1.
    - Selection maps 1:1 to the printed catalog index.

### Epic 2 — Movie Detail Page (MOVIE-APP-2)
**Description:** Show full details for a selected movie (Overview tab).

- **MOVIE-APP-2.1** — As a ticket buyer, I want to view a movie's full
  details (description, director, writers, studio, language, budget, cast,
  keywords) before booking.
  - *Acceptance Criteria:*
    - `MovieBookingService.viewMovieDetails(movie)` prints rating, duration,
      genres, release year, certification, description, production details,
      top cast (actor → character), and keywords.
    - Output mirrors the wireframe in `MovieBooking.md`.

### Epic 3 — Hero Actions (MOVIE-APP-3)
**Description:** Simulate the hero banner's Play / Watchlist / Like actions.

- **MOVIE-APP-3.1** — As a ticket buyer, I want to "Play" a movie (simulated).
  - *Acceptance Criteria:* Prints a "Now playing: {title}" message.

- **MOVIE-APP-3.2** — As a ticket buyer, I want to add a movie to "My List"
  (watchlist).
  - *Acceptance Criteria:* Movie is added to an internal watchlist set and
    is retrievable via `getMyList()`; duplicates are not re-added (`Set`
    semantics).

- **MOVIE-APP-3.3** — As a ticket buyer, I want to "Like" a movie.
  - *Acceptance Criteria:* Movie is added to an internal liked set; prints a
    confirmation message.

### Epic 4 — Showtime Selection (MOVIE-APP-4)
**Description:** Choose a specific screening time for the selected movie.

- **MOVIE-APP-4.1** — As a ticket buyer, I want to see today's available
  showtimes (with theatre and price) for my selected movie.
  - *Acceptance Criteria:*
    - Showtimes, theatre name, ticket price, and seat-grid size (rows ×
      seats/row) are loaded per-movie from `movies.properties` via
      `ShowtimeInfo`.
    - Each showtime is instantiated as `Showtime(movie, dateTime, theatre,
      price, rows, seatsPerRow)` and registered with the service.

- **MOVIE-APP-4.2** — As a ticket buyer, I want to select a showtime by
  number.
  - *Acceptance Criteria:*
    - Prompt: `Select showtime (1-N)`.
    - Invalid/non-numeric input defaults to the first showtime.

### Epic 5 — Seat Selection (MOVIE-APP-5)
**Description:** Three distinct ways to choose seats for a booking.

- **MOVIE-APP-5.1** — As a ticket buyer, I want to see the seat map and list
  of available seats before choosing.
  - *Acceptance Criteria:*
    - `showtime.printSeatMap()` renders a grid of `[O]` (open) / `[X]`
      (booked) per row (row letters A, B, C...).
    - `showtime.getAvailableSeats()` returns all unbooked seat codes (e.g.
      `A1`, `C4`).

- **MOVIE-APP-5.2** — As a ticket buyer, I want to choose how seats are
  selected via a 3-option menu.
  - *Acceptance Criteria:*
    - Menu prompt:
      ```
      1) Default seats (C1,C4,C3)
      2) Choose your own seats
      3) AI random selection
      ```
    - Input is read as an integer `1-3`; any invalid value defaults to
      option `1`.

- **MOVIE-APP-5.3 — Option 1: Default seats** — As a ticket buyer, I want a
  one-click "quick pick" of seats `C1`, `C4`, `C3` without typing anything
  else.
  - *Acceptance Criteria:*
    - Attempts to book exactly seats `C1`, `C4`, `C3` (in that order).
    - Any of those seats already booked is skipped with a message (e.g.
      "Default seat C4 is already booked; skipping.").
    - Invalid seat codes for the current theatre's grid size are skipped
      with a message.
    - If all three default seats are unavailable/invalid, falls back to
      random selection of up to 2 seats.
    - Prints `Default selected seats: [...]`.

- **MOVIE-APP-5.4 — Option 2: User selection** — As a ticket buyer, I want
  to manually type the seat codes I want.
  - *Acceptance Criteria:*
    - Prompt: `Enter seat(s) to book, comma-separated (e.g. C4,C5):`.
    - Input is parsed on commas, trimmed, and upper-cased.
    - Already-booked seats are skipped with a per-seat message; invalid
      codes are skipped with a per-seat message.
    - If no valid seats remain after parsing, falls back to random
      selection of up to 2 seats.
    - Prints `Selected seats: [...]`.

- **MOVIE-APP-5.5 — Option 3: AI random selection** — As a ticket buyer, I
  want the system to randomly pick seats for me.
  - *Acceptance Criteria:*
    - Prompts for desired seat count: `How many seats would you like to
      book (1-N)`, where N = currently available seat count.
    - Invalid/out-of-range input defaults to `min(2, availableCount)`.
    - Available seats are shuffled (`Collections.shuffle` with `Random`) and
      the first `seatCount` are selected.
    - Prints `Randomly selected seats: [...]`.

### Epic 6 — Booking & Confirmation (MOVIE-APP-6)
**Description:** Create and confirm a booking for the selected seats.

- **MOVIE-APP-6.1** — As a ticket buyer, I want my chosen seats booked and a
  booking record created.
  - *Acceptance Criteria:*
    - `MovieBookingService.bookTickets(userName, showtime, seats)` validates
      every seat is still available, marks each as booked on the
      `Showtime`'s seat map, and creates a `Booking` with status `PENDING`.
    - Booking has a short unique ID (`UUID` substring, 8 chars, upper-case).
    - Throws `IllegalStateException` if any seat became unavailable between
      selection and booking (race-safety at the API level).

- **MOVIE-APP-6.2** — As a ticket buyer, I want my booking confirmed and
  charged.
  - *Acceptance Criteria:*
    - `confirmBooking(booking)` sets status to `CONFIRMED`.
    - Total price = `seats.size() * showtime.getTicketPrice()`.
    - Prints `✅ Booking {id} confirmed for {user}. Total paid: ${total}`.

- **MOVIE-APP-6.3** — As a ticket buyer, I want to see the updated seat map
  after booking, reflecting my booked seats as `[X]`.
  - *Acceptance Criteria:* `showtime.printSeatMap()` reprinted after
    confirmation shows booked seats as `[X]`.

### Epic 7 — Booking Cancellation & Refund (MOVIE-APP-7) — **FUTURE / STUBBED**
**Description:** Allow a ticket buyer (or clerk) to cancel an existing
booking and receive a refund according to a defined refund policy. The
service-layer logic is implemented today as a stub; the console menu is not
yet wired up. See `MovieBookingApp_Wireframe.md` (section 7) for proposed
screens.

- **MOVIE-APP-7.1** — As a ticket buyer, I want to view my existing bookings
  ("My Bookings") so I can choose one to cancel.
  - *Acceptance Criteria (future):*
    - A new console menu option lists bookings via
      `MovieBookingService.getBookings()` (booking ID, movie, showtime,
      seats, total, status).
    - Not yet implemented in `MovieBookingApp`'s `main` method.

- **MOVIE-APP-7.2** — As a ticket buyer, I want to cancel a booking and have
  my seats released back to the seat map.
  - *Acceptance Criteria:*
    - `MovieBookingService.cancelBookingWithRefund(Booking)` (stub,
      implemented and unit-testable) releases every seat in the booking via
      `Showtime.releaseSeat(String)` and sets the booking's status to
      `CANCELLED`.
    - Already-cancelled bookings cannot be cancelled again (no-op, returns
      `$0.00` refund, prints a message).

- **MOVIE-APP-7.3** — As a ticket buyer, I want my refund amount calculated
  automatically based on how close to the showtime I cancel.
  - *Acceptance Criteria:*
    - `MovieBookingService.calculateRefundAmount(Booking)` (stub,
      implemented) applies the **Refund Policy** below based on hours
      remaining until `showtime.getStartTime()`.
    - `cancelBookingWithRefund(Booking)` prints
      `❌ Booking {id} cancelled. Refund issued: ${amount}` and returns the
      refund amount as a `double`.

- **MOVIE-APP-7.4** — As a clerk, I want to perform the same cancellation
  and refund on behalf of a ticket buyer.
  - *Acceptance Criteria (future):* Same service-layer methods as
    MOVIE-APP-7.2/7.3; the console-facing distinction between "ticket
    buyer self-cancels" vs. "clerk cancels for a buyer" is a UI/menu
    concern only, not a service-layer one.

#### Refund Policy

| Time of cancellation relative to showtime start | Refund % |
|---|---|
| 24 hours or more before showtime | 100% |
| Between 2 and 24 hours before showtime | 50% |
| Less than 2 hours before showtime, or after it has started | 0% |
| Booking already cancelled | 0% (not re-cancellable) |

Refund amount = `booking.getTotalPrice() * refundRate`. See
`MovieBookingApp_Wireframe.md` for full policy notes and proposed UI.

## 6. Functional Requirements Summary

| ID | Requirement |
|---|---|
| FR-1 | Load movie catalog and per-movie showtime/pricing/seat-grid data from `movies.properties`. |
| FR-2 | Display catalog and allow numeric movie selection with safe fallback. |
| FR-3 | Display movie detail page (Overview tab content). |
| FR-4 | Support Play / Add to Watchlist / Like actions. |
| FR-5 | Display and allow numeric showtime selection with safe fallback. |
| FR-6 | Display seat map and available seat codes. |
| FR-7 | Provide a 3-option seat-selection menu: Default (`C1,C4,C3`), Manual, AI Random. |
| FR-8 | Validate seat availability/validity in all three selection modes with graceful fallback. |
| FR-9 | Create a booking, mark seats booked, and confirm with total price. |
| FR-10 | Reprint seat map post-booking and display the user's watchlist ("My List"). |
| FR-11 *(stub)* | Compute refund amount for a booking per the Refund Policy (`calculateRefundAmount`). |
| FR-12 *(stub)* | Cancel a booking, release its seats, and issue a refund (`cancelBookingWithRefund`). |

## 7. Non-Functional Requirements

- **Resilience:** Any malformed/out-of-range console input must not crash
  the app; defaults are applied instead.
- **Data-driven:** No movie/showtime/pricing data hardcoded in Java source.
- **Testability:** Core logic (seat availability, booking totals, status
  transitions) lives in plain Java classes (`Showtime`, `Booking`,
  `MovieBookingService`) independent of console I/O, enabling unit tests
  without `Scanner` mocking.

## 8. Out of Scope / Future Enhancements

- Web/GUI front end.
- Persisting bookings across runs (database or file-based storage).
- Multiple simultaneous users / concurrency control on seat booking.
- Payment gateway integration (refunds are logged/calculated only, not
  processed against a real payment method).
- Wiring the "My Bookings" / cancellation & refund menu (Epic 7) into the
  console app's `main` method.
- Partial-seat cancellation within a multi-seat booking.
- Exposing the `MovieReservationUtil` clerk-cancel use case within this app.

## 9. Suggested JIRA Import Mapping

| JIRA Field | Source |
|---|---|
| Issue Type: Epic | Section 5 headers (Epic 1–7) |
| Issue Type: Story | Each `MOVIE-APP-X.Y` bullet |
| Summary | Bold "As a ... I want ..." line |
| Description | Corresponding prose + acceptance criteria |
| Acceptance Criteria | Nested bullet list under each story |
| Labels | `movie-booking-app`, `console-demo`, `java` |
