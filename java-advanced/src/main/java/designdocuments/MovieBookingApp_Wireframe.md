# Movie Booking App — Wireframe

Text/ASCII wireframes for every screen described in `../../../../requirementdocs/MovieBookingApp_PRD.md`.
Screens map 1:1 to epics MOVIE-APP-1 through MOVIE-APP-6, plus a **new**
screen (MOVIE-APP-7, below) for the future cancellation & refund flow.

---

## 1. Home / Movie Catalog — (Epic 1)

```
================================================================================
[ Top Navigation Bar ]
Home   Movies   TV Shows   My List                              [ Search ] [🔔] [👤]
================================================================================

== Home > Movies ==

  1) Inception            ★ 8.8 | 2h 28m | Action, Sci-Fi
  2) Avengers: Endgame     ★ 8.4 | 3h 01m | Action, Adventure
  3) Finding Nemo          ★ 8.1 | 1h 40m | Animation
  ...
  20) ...

Select a movie (1-20): _
```

---

## 2. Movie Detail Page (Overview tab) — (Epic 2)

```
+----------------------------------------------------------------------------+
|   [ Hero Backdrop Image Landscape Banner ]                                 |
|   ★ 8.8  |  2h 28m  |  Action, Sci-Fi  |  2010  |  PG-13                   |
|   Inception                                                                |
|   ----------------------------------------------------------------------  |
|   [ ▶️ Play Movie ]      [ ➕ Add to Watchlist ]     [ 👍 Like ]            |
+----------------------------------------------------------------------------+

[ Tab Bar ]  Overview (Active)  |  Cast & Crew  |  Reviews  |  More Like This

Description                               Production Details
A thief who steals corporate secrets      Director: Christopher Nolan
through dream-sharing technology...       Writers: Christopher Nolan
                                           Studio: Warner Bros. Pictures
                                           Language: English
                                           Budget: $160,000,000

Top Cast                                  Keywords
[ O ] Leonardo D. -> Dom Cobb              [ Dreams ] [ Sci-Fi ] [ Heist ]
[ O ] Elliot P.   -> Ariadne                [ Mind-Bending ] [ Illusion ]
```

---

## 3. Hero Actions — (Epic 3)

```
▶️  Now playing: Inception
➕  Added to My List: Inception
👍  Liked: Inception
```

---

## 4. Showtime Selection — (Epic 4)

```
Available showtimes for Inception (Screen 1, $12.5/seat):
  1) 10:00
  2) 13:30
  3) 16:45
  4) 19:30
  5) 22:15
Select showtime (1-5): _
```

---

## 5. Seat Selection — (Epic 5)

```
=== Ticket buyer books seats for Inception ===

Seat map for Inception @ Screen 1 (2026-11-05T13:30)
A [O] [O] [O] [O] [O] [O] [O] [O]
B [O] [O] [O] [O] [O] [O] [O] [O]
C [O] [O] [O] [O] [O] [O] [O] [O]
D [O] [O] [O] [O] [O] [O] [O] [O]
E [O] [O] [O] [O] [O] [O] [O] [O]

Available seats: [A1, A2, A3, ... E8]

Seat selection options:
  1) Default seats (C1,C4,C3)
  2) Choose your own seats
  3) AI random selection
Select an option (1-3): _
```

**Option 1 selected:**
```
Default selected seats: [C1, C4, C3]
```

**Option 2 selected:**
```
Enter seat(s) to book, comma-separated (e.g. C4,C5): C4,C5
Selected seats: [C4, C5]
```

**Option 3 selected:**
```
How many seats would you like to book (1-40): 2
Randomly selected seats: [B6, D2]
```

---

## 6. Booking Confirmation — (Epic 6)

```
Booking created: Booking{id='1D8EC464', user='Arun Kumar', movie='Inception',
                          seats=[C1, C4, C3], total=$37.50, status=PENDING}

✅ Booking 1D8EC464 confirmed for Arun Kumar. Total paid: $37.50

Seat map for Inception @ Screen 1 (2026-11-05T13:30)
A [O] [O] [O] [O] [O] [O] [O] [O]
B [O] [O] [O] [O] [O] [O] [O] [O]
C [X] [O] [X] [X] [O] [O] [O] [O]
D [O] [O] [O] [O] [O] [O] [O] [O]
E [O] [O] [O] [O] [O] [O] [O] [O]

My List: [Inception]
```

---

## 7. My Bookings / Cancellation & Refund — (Epic 7 — FUTURE, STUBBED)

> **Status:** Not yet wired into `MovieBookingApp`'s console menu. The
> underlying logic exists as a stub in `MovieBookingService`:
> `calculateRefundAmount(Booking)` and `cancelBookingWithRefund(Booking)`,
> plus `Showtime.releaseSeat(String)` to free seats back to the pool.

### 7a. "My Bookings" list (proposed)

```
================================================================================
[ Top Navigation Bar ]
Home   Movies   TV Shows   My List   My Bookings                [ Search ] [🔔] [👤]
================================================================================

== My Bookings ==

  1) 1D8EC464 | Inception | 2026-11-05 13:30 | Seats: C1,C4,C3 | $37.50 | CONFIRMED
  2) 9F2A7C10 | Avengers: Endgame | 2026-11-06 19:00 | Seats: A2 | $12.50 | CONFIRMED

Select a booking to cancel (1-2), or press Enter to go back: _
```

### 7b. Cancellation confirmation (proposed)

```
Booking 1D8EC464 — Inception — 2026-11-05 13:30 — Seats: C1,C4,C3 — $37.50

Cancelling this booking now (current time: 2026-11-05 10:45) is:
  18h 45m before showtime -> eligible for a 100% refund.

Are you sure you want to cancel this booking? (y/n): y

❌ Booking 1D8EC464 cancelled. Refund issued: $37.50
Seats C1, C4, C3 have been released back to the seat map.
```

### 7c. Late-cancellation example (no refund)

```
Booking 9F2A7C10 — Avengers: Endgame — 2026-11-06 19:00 — Seats: A2 — $12.50

Cancelling this booking now (current time: 2026-11-06 18:30) is:
  0h 30m before showtime -> not eligible for a refund (< 2 hours).

Are you sure you want to cancel this booking? (y/n): y

❌ Booking 9F2A7C10 cancelled. Refund issued: $0.00
Seat A2 has been released back to the seat map.
```

---

## Refund Policy

| Time of cancellation relative to showtime start | Refund % | Refund amount |
|---|---|---|
| 24 hours or more before showtime | 100% | Full ticket price for all seats |
| Between 2 and 24 hours before showtime | 50% | Half of ticket price for all seats |
| Less than 2 hours before showtime, or after it has started | 0% | No refund |
| Booking already cancelled | 0% | No further refund (not re-cancellable) |

**Notes:**
- Refund = `booking.getTotalPrice() * refundRate`, where `refundRate` is
  `1.0`, `0.5`, or `0.0` per the table above.
- Cancelling a booking always releases its seats back onto the showtime's
  seat map (`[X]` → `[O]`), regardless of refund eligibility, so the seats
  become bookable by other buyers.
- This policy is implemented in
  `MovieBookingService.calculateRefundAmount(Booking)` and applied by
  `MovieBookingService.cancelBookingWithRefund(Booking)`.
- **Out of scope for now:** partial seat cancellation (cancelling only some
  seats within a multi-seat booking), refund-to-original-payment-method
  integration, and a time-based UI countdown — all deferred to a later
  iteration once this flow is prioritized.

## Implementation stub reference

| Class | Member | Purpose |
|---|---|---|
| `Showtime` | `releaseSeat(String seatCode)` | Marks a previously booked seat as available again. |
| `MovieBookingService` | `calculateRefundAmount(Booking booking)` | Computes refund amount per the policy table above. |
| `MovieBookingService` | `cancelBookingWithRefund(Booking booking)` | Releases seats, sets status to `CANCELLED`, prints/returns the refund amount. |

These methods are fully implemented and unit-testable today, but
**`MovieBookingApp`'s console `main` method does not yet call them** — a
future iteration should add a "My Bookings" menu option (see mockups 7a/7b/7c
above) that lists a user's bookings and lets them cancel one via
`cancelBookingWithRefund`.
