# JavaGitHubCoPilotAI
To Understand and Implement Modern Java with GitHub Copilot AI
### * This Project is for personal skills purpose ONLY and should NOT be used by ANYONE
<span style="background-color: red; color: white; padding: 2px 4px; border-radius: 3px;">### * This Project was developed using GitHub Copilot AI from Requirement to Implementation and Testing</span>


----------
# MovieBookingApp

A console-based movie ticket booking application built in
`com.modernjava.moviebookingapp`. Users can browse a movie catalog,
view details, pick a showtime, select seats (default / manual / AI-random),
and confirm a booking. A cancellation & refund flow is designed and stubbed
for a future release.

This README is the **single entry point** into the full documentation set
for this package — every design/requirements artifact below was produced as
part of a complete SDLC walkthrough, and is linked here in reading order.

---

## 🚀 Quick Start

```bash
# Compile (from repo root)
javac -d out $(find java-advanced/src/main/java/com/modernjava/moviebookingapp -name "*.java")

# Run (movies.properties must be on the classpath)
java -cp out:java-advanced/src/main/resources com.modernjava.moviebookingapp.MovieBookingApp
```

---

## 📚 Documentation Index

| # | Document | Path | Phase | Description |
|---|---|---|---|---|
| 1 | `MovieBookingApp_PRD.md` | `java-advanced/src/main/java/requirementdocs/MovieBookingApp_PRD.md` | Requirements | JIRA-style Product Requirements Document — 7 epics, user stories, acceptance criteria, functional/non-functional requirements, refund policy, out-of-scope, JIRA field-mapping guide. |
| 2 | `MovieBooking_Hi-Fi_Wireframe.md` | `java-advanced/src/main/java/designdocuments/MovieBooking_Hi-Fi_Wireframe.md` | Design | Original hi-fi ASCII mockup (hero banner, tab bar, Overview tab, cast) that inspired the PRD's screen descriptions. |
| 3 | `MovieBookingApp_Wireframe.md` | `java-advanced/src/main/java/designdocuments/MovieBookingApp_Wireframe.md` | Design | PRD-driven wireframes for all 7 screens (catalog → detail → hero actions → showtime → seats → confirmation → *future* cancellation/refund), plus the Refund Policy table. |
| 4 | `MovieBookingApp_ClassDiagram.md` | `java-advanced/src/main/java/designdocuments/MovieBookingApp_ClassDiagram.md` | Design | Mermaid **class diagram** of every class, field, method, and relationship in the package. |
| 5 | `MovieBookingApp_UseCaseDiagram.md` | `java-advanced/src/main/java/designdocuments/MovieBookingApp_UseCaseDiagram.md` | Design | Mermaid **use-case diagram** — actors (Ticket Buyer, Clerk, Movies Website), use cases, `<<include>>` relationships. |
| 6 | `MovieBookingApp_Lo-Fi_Wireframe.md` | `java-advanced/src/main/java/designdocuments/MovieBookingApp_Lo-Fi_Wireframe.md` | Design | Same 7 screens re-derived directly from the class diagram, annotated with exact class/method calls. |
| 7 | `MovieBookingApp_SequenceDiagram.md` | `java-advanced/src/main/java/designdocuments/MovieBookingApp_SequenceDiagram.md` | Design | Mermaid **sequence diagram** — full object-interaction timeline across `MovieBookingApp → MovieCatalogLoader → MovieBookingService → Movie/Showtime/Booking`, including the cancellation/refund stub. |
| 8 | `MovieBookingApp_Prompt.md` | `java-advanced/src/main/java/agenticAIPrompt/MovieBookingApp_Prompt.md` | Design | Plain-language recap of the app's collaborators, step-by-step workflow, design notes, and an example console run. |
| 9 | `TestingScenariosDocument.md` | `java-advanced/src/main/java/testingdocs/TestingScenariosDocument.md` | Testing | End-user functional test scenarios (positive/negative/boundary) for every screen and flow, mapped to PRD Epic Keys, with Priority/Severity/Design Steps/Expected Result/Test Status columns. |
| 10 | `MovieBookingApp_SDLC_Workflow.md` | `java-advanced/src/main/java/testingdocs/MovieBookingApp_SDLC_Workflow.md` | All phases | The master document — walks through all 7 SDLC phases (requirements → design → implementation → testing → deployment → maintenance) and maps every phase to the files in this directory, with a full traceability matrix. |

> 💡 **New here?** Start with `MovieBookingApp_SDLC_Workflow.md` for the big
> picture, or `MovieBookingApp_PRD.md` if you just want the requirements.

---

## 🧱 Source Code

| Class | Layer | Role |
|---|---|---|
| `Movie.java` | Domain model | Title, rating, duration, genres, description, production details, cast, keywords. |
| `CastMember.java` | Domain model | Actor → character pairing for "Top Cast". |
| `ShowtimeInfo.java` | Domain model | Per-movie showtime config (times, theatre, price, seat-grid size). |
| `Showtime.java` | Domain model | A bookable screening; owns the seat map (`isSeatAvailable`, `bookSeat`, `releaseSeat`, `printSeatMap`). |
| `Booking.java` | Domain model | A reservation: ID, user, showtime, seats, status, total price. |
| `MovieCatalogLoader.java` | Data access | Loads `movies.properties` into the `Movie` catalog + showtime map. |
| `MovieBookingService.java` | Service layer | Browse/detail/hero-action/booking logic, plus stubbed `calculateRefundAmount`/`cancelBookingWithRefund`. |
| `MovieBookingApp.java` | Entry point | Console `main`; seat-selection menu (Default `C1,C4,C3` / Manual / AI Random). |

---

## 🔮 Future Work

- Wire "My Bookings" + cancellation/refund into the console menu (service
  methods already implemented — see Epic 7 in the PRD).
- Persistent storage across runs; payment gateway integration.
- Unit tests for `MovieBookingService`, `Showtime`, `Booking`.

See `MovieBookingApp_SDLC_Workflow.md` → *Phase 7 — Maintenance / Future
Backlog* for the complete list.
