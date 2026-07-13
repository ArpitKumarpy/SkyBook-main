# SkyBook — Frontend

A React (Vite) admin console for the SkyBook Spring Boot backend. Covers every
endpoint exposed by the API: aircraft, users, flights, schedules, seats,
bookings, tickets, and flight search.

## 1. Install

```bash
npm install
```

## 2. Point it at your backend

Copy the example env file and adjust if your backend runs somewhere other than
`http://localhost:8080`:

```bash
cp .env.example .env
```

## 3. Enable CORS on the backend

The SkyBook Spring Boot project doesn't currently have a CORS policy, so the
browser will block requests from the Vite dev server (`http://localhost:5173`)
unless you add one. A ready-to-drop-in `CorsConfig.java` is included alongside
this frontend (see the message that came with these files) — copy it into
`src/main/java/com/example/SkyBook/config/` in your backend project and restart
the backend.

## 4. Run

```bash
npm run dev
```

The app runs on http://localhost:5173.

## Project structure

```
src/
  api/          one thin module per controller (aircraftApi, bookingApi, ...)
  components/   shared UI: DataTable, Modal, FormField, StatusBadge, ...
  hooks/        useResource — a small list-loading hook used by every page
  pages/        one page per resource, plus Dashboard and FlightSearch
  utils/        formatting helpers + enum option lists
```

## Notes on the API contract

- `UserRequestDto` requires a password on **every** call, including updates —
  the edit form asks for it again for that reason.
- `BookingController` has no update/delete route, only create, list, get, and
  `PATCH /{id}/cancel` — so bookings can only be created or cancelled here.
- `TicketController` has no update route either; tickets are issued, looked
  up, or cancelled (`DELETE /{id}`).
- Flight search (`GET /api/flights/search`) returns `flightId`, not a
  schedule id, so "View & book" from search results routes to the Schedules
  page filtered by that flight, where you can pick the exact departure.
