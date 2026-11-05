# MovieBookingApp — End-to-End SDLC Workflow

This document walks through the **full Software Development Life Cycle
(SDLC)** for the `com.modernjava.movieapp.moviebookingapp` package, mapping
every phase to the actual files present in this directory. It is meant as a
teaching reference: "here is what a real feature's paper trail looks like,
from requirements to running code to future backlog."

```
moviebookingapp/
├── MovieBookingApp_BRD.md               (Phase 1 — business requirements)
├── MovieBookingApp_PRD.md               (Phase 2 — product requirements specification)
├── MovieBooking_Hi-Fi_Wireframe.md      (Phase 3 — initial design sketch)
├── MovieBookingApp_Wireframe.md         (Phase 3 — PRD-driven wireframes)
├── MovieBookingApp_ClassDiagram.md      (Phase 3 — class/UML diagram)
├── MovieBookingApp_UseCaseDiagram.md    (Phase 3 — use-case diagram)
├── MovieBookingApp_Lo-Fi_Wireframe.md   (Phase 3 — UML-driven wireframes)
├── MovieBookingApp_SequenceDiagram.md   (Phase 3 — sequence diagram)
├── MovieBookingApp_Prompt.md            (Phase 3 — design brief / recap)
├── Movie.java                          (Phase 4 — domain model)
├── CastMember.java                     (Phase 4 — domain model)
├── ShowtimeInfo.java                   (Phase 4 — domain model)
├── Showtime.java                       (Phase 4 — domain model)
├── Booking.java                        (Phase 4 — domain model)
├── MovieCatalogLoader.java             (Phase 4 — data access)
├── MovieBookingService.java            (Phase 4 — service layer)
└── MovieBookingApp.java                (Phase 4 — entry point)
```

---

## Phase 1 — Requirements Gathering (BRD)

**Artifact:** `MovieBookingApp_BRD.md`

The Business Requirements Document translates the movie-booking need into
business outcomes and high-level capabilities, before the detailed product
specification in Phase 2:

- **Business need:** Provide a straightforward way for customers to discover
  movies, review relevant details, choose a screening and seats, and receive
  a booking confirmation.
- **Business objectives:** Make the catalog and screening information easy to
  browse; support a clear, end-to-end ticket-booking journey; and make movie,
  showtime, theatre, price, and seat-grid data configurable.
- **Stakeholders and users:** Ticket buyers use the booking experience;
  booking clerks and the movie catalog/provider are represented as supporting
  actors in the product's use cases.
- **High-level business requirements:** Present the available movie catalog
  and movie information; offer movie actions and configured showtimes; allow
  customers to select available seats; calculate the booking total and
  confirm the reservation; and document cancellation/refund as a future
  capability governed by the stated refund policy.
- **Business success criteria:** A customer can follow the supported flow
  from browsing a configured movie to a confirmed booking, with the selected
  seats reflected as unavailable and the total derived from the configured
  price. The detailed, testable acceptance criteria are defined in the PRD.
- **Scope and constraints:** The initial delivery is a console-based,
  in-memory teaching application with catalog and showtime data supplied by
  configuration. Persistent storage, payment gateway integration, partial
  cancellation, and a complete cancellation/refund user flow are outside the
  initial implementation scope.

> *SDLC equivalent:* Business Requirements Document defining the business
> need, outcomes, stakeholders, scope, and high-level requirements.

---

## Phase 2 — Requirements Specification (PRD)

**Artifact:** `MovieBookingApp_PRD.md`

Refines the business requirements in `MovieBookingApp_BRD.md` into a
JIRA-importable Product Requirements Document:

- **Epic 1** — Movie Catalog (load from `movies.properties`, browse, select).
- **Epic 2** — Movie Detail Page (Overview tab: description, production
  details, cast, keywords).
- **Epic 3** — Hero Actions (Play / Add to Watchlist / Like).
- **Epic 4** — Showtime Selection (per-movie times, theatre, price).
- **Epic 5** — Seat Selection, with three sub-modes: Default
  (`C1,C4,C3`), Manual entry, AI Random.
- **Epic 6** — Booking & Confirmation (create booking, charge total,
  reprint seat map).
- **Epic 7 (future/stubbed)** — Booking Cancellation & Refund, including a
  documented **Refund Policy** (100% / 50% / 0% refund based on hours
  before showtime).
- Functional requirements table (FR-1 … FR-12), non-functional requirements
  (resilience, data-driven config, testability), out-of-scope list, and a
  JIRA field-mapping guide.

> *SDLC equivalent:* Product Requirements Document / Epics & User Stories
> with acceptance criteria.

---

## Phase 3 — Design

Design happened across six complementary artifacts, each capturing a
different view of the same system:

| Artifact | View | Content |
|---|---|---|
| `MovieBooking_Hi-Fi_Wireframe.md` | Visual mock | Original ASCII hero-banner/tab-bar mockup (nav bar, hero backdrop, Overview tab, production details, cast) that inspired the PRD's screen descriptions. |
| `MovieBookingApp_Wireframe.md` | Screen flow (PRD-driven) | ASCII wireframes for all 7 screens (catalog → detail → hero actions → showtime → seats → confirmation → *future* cancellation/refund), plus the full Refund Policy table and a stub reference table. |
| `MovieBookingApp_ClassDiagram.md` | Structure | Mermaid **class diagram** of every class in this package (`Movie`, `CastMember`, `ShowtimeInfo`, `Showtime`, `Booking`/`BookingStatus`, `MovieBookingService`, `MovieCatalogLoader`, `MovieBookingApp`) with fields, methods, and composition/aggregation/dependency relationships. |
| `MovieBookingApp_UseCaseDiagram.md` | Behavior (actors) | Mermaid **use-case diagram** (Ticket Buyer, Clerk, Movies Website actors) with `<<include>>` relationships. |
| `MovieBookingApp_Lo-Fi_Wireframe.md` | Screen flow (UML-driven) | Re-derives the same 7 screens directly from the class diagram, annotating each screen with the exact class/method calls that produce it. |
| `MovieBookingApp_SequenceDiagram.md` | Interaction | Mermaid **sequence diagram** showing the full object-interaction timeline across `MovieBookingApp → MovieCatalogLoader → MovieBookingService → Movie/Showtime/Booking`, including the future cancellation/refund stub. |
| `MovieBookingApp_Prompt.md` | Plain-language recap | Describes `MovieBookingApp`'s collaborators, step-by-step workflow, key design notes, and an example console run — the design team's narrative walkthrough of the system, recapping all the diagrams above in plain language. |

> *SDLC equivalent:* UML design artifacts (class diagrams, use-case
> diagrams, sequence diagrams) and UI wireframes (lo-fi and hi-fi) produced
> before implementation.

---

## Phase 4 — Implementation

**Artifacts (Java source), grouped by architectural layer:**

| Class | Layer | Role |
|---|---|---|
| `Movie.java` | Domain model | Immutable: title, rating, duration, genres, description, production details, cast, keywords. |
| `CastMember.java` | Domain model | Record: actor → character pairing for the "Top Cast" section. |
| `ShowtimeInfo.java` | Domain model | Record: per-movie showtime config (times, theatre, price, seat-grid size) loaded from properties. |
| `Showtime.java` | Domain model | A bookable screening; owns the `boolean[][]` seat map; exposes `isSeatAvailable`, `bookSeat`, `releaseSeat` *(stub)*, `printSeatMap`, `getAvailableSeats`. |
| `Booking.java` | Domain model | A reservation: ID, user, showtime, seats, `BookingStatus` (`PENDING`/`CONFIRMED`/`CANCELLED`), total price. |
| `MovieCatalogLoader.java` | Data access | Reads `movies.properties` from the classpath and builds the `Movie` catalog + `Map<Movie, ShowtimeInfo>` — keeps all data external/configurable instead of hardcoded. |
| `MovieBookingService.java` | Service layer | `browseMovies`, `viewMovieDetails`, `playMovie`/`addToWatchlist`/`likeMovie`, `bookTickets`, `confirmBooking`, and the stubbed `calculateRefundAmount`/`cancelBookingWithRefund` (Epic 7). |
| `MovieBookingApp.java` | Entry point | `main` method; pure orchestration — reads `Scanner` input, delegates all business logic to the service layer, implements the 3-option seat-selection menu (Default `C1,C4,C3` / Manual / AI Random). |

> *SDLC equivalent:* Development phase — turning the design artifacts above
> into compiling, running code, following a clean separation of domain
> models, data access, service layer, and console entry point.

---

## Phase 5 — Testing / Verification

Performed interactively during development (no dedicated test classes yet
in this package):

- `javac` compilation checks after every change (`MovieBookingApp.java`,
  `MovieBookingService.java`, `Showtime.java`).
- Manual console runs piping scripted input (e.g. `printf "1\n2\n1\n" | java
  ... MovieBookingApp`) to verify the default-seat option (`C1,C4,C3`)
  books correctly and the seat map reflects `[X]` afterward.
- `MovieBookingApp_PRD.md`'s acceptance criteria double as manual test
  cases per story (e.g. "invalid movie choice defaults to #1").

> *SDLC equivalent:* Manual/exploratory testing against acceptance
> criteria. **Gap/next step:** add JUnit tests for `MovieBookingService`,
> `Showtime`, and `Booking` — these classes are already decoupled from
> console I/O, so they're unit-testable without `Scanner` mocking (see
> `MovieBookingApp_PRD.md` §7, Non-Functional Requirements).

---

## Phase 6 — Deployment / Usage

This is a teaching console app, not a deployed service:

- **Build:** compiled via the project's Gradle build (`basics-final`
  module) or directly with `javac`.
- **Run:** `java com.modernjava.movieapp.moviebookingapp.MovieBookingApp`
  (requires `movies.properties` on the classpath, e.g.
  `src/main/resources`).
- **Config:** all movie/showtime/pricing/seat-grid data lives in
  `movies.properties` — changing the catalog requires no code changes.

> *SDLC equivalent:* Release/deployment — here, simply "runnable via your
> IDE or `java`/Gradle," since there's no server, container, or CI/CD
> pipeline for this teaching module.

---

## Phase 7 — Maintenance / Future Backlog

Captured explicitly as **stubs and "future" markers** across the design and
implementation artifacts so the next contributor knows exactly what's left:

| Backlog item | Where it's tracked | Current state |
|---|---|---|
| Wire "My Bookings" + Cancel/Refund into the console menu | `MovieBookingApp_PRD.md` Epic 7, `MovieBookingApp_Wireframe.md` Screen 7 | Service methods implemented (`calculateRefundAmount`, `cancelBookingWithRefund`, `Showtime.releaseSeat`) but not called from `MovieBookingApp.main()`. |
| Partial-seat cancellation within a multi-seat booking | `MovieBookingApp_PRD.md` §8 Out of Scope | Not started. |
| Persistent storage (DB/file) across runs | `MovieBookingApp_PRD.md` §8 | Not started; all state is in-memory per run. |
| Payment gateway integration | `MovieBookingApp_PRD.md` §8 | Simulated total-price calculation only. |
| Unit tests for service/domain classes | This document, Phase 5 | Not started; classes are already decoupled and test-ready. |

> *SDLC equivalent:* Maintenance phase — backlog grooming, with every
> pending item traceable back to a specific PRD epic or wireframe screen
> rather than living only in tribal knowledge.

---

## Full Phase ↔ Artifact Traceability Matrix

| SDLC Phase | Artifact(s) in this directory |
|---|---|
| 1. Requirements Gathering | `MovieBookingApp_BRD.md` |
| 2. Requirements Specification | `MovieBookingApp_PRD.md` |
| 3. Design | `MovieBooking_Hi-Fi_Wireframe.md`, `MovieBookingApp_Wireframe.md`, `MovieBookingApp_ClassDiagram.md`, `MovieBookingApp_UseCaseDiagram.md`, `MovieBookingApp_Lo-Fi_Wireframe.md`, `MovieBookingApp_SequenceDiagram.md`, `MovieBookingApp_Prompt.md` |
| 4. Implementation | `Movie.java`, `CastMember.java`, `ShowtimeInfo.java`, `Showtime.java`, `Booking.java`, `MovieCatalogLoader.java`, `MovieBookingService.java`, `MovieBookingApp.java` |
| 5. Testing / Verification | Manual compile/run checks against `MovieBookingApp_PRD.md` acceptance criteria |
| 6. Deployment / Usage | Gradle/`javac` build + `movies.properties` config |
| 7. Maintenance / Future Backlog | `MovieBookingApp_PRD.md` §8, `MovieBookingApp_Wireframe.md` §7 (stub notes) |
