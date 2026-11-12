# System rezerwacji miejsc

Projekt z baz danych (AGH): ogólny system rezerwacji miejsc na wydarzenia (kino, pociąg, koncert, sala wykładowa).
Celem jest porównanie sposobów zapewnienia poprawnej współbieżności w PostgreSQL: blokada pesymistyczna,
blokada optymistyczna, poziomy izolacji i ograniczenie UNIQUE.

> **Aktualny etap: UI + API na mockach.** Bazy danych jeszcze nie ma. Backend zwraca deterministyczne dane
> z pamięci, a `POST /api/reservations` waliduje żądanie i zwraca `201`, ale **niczego nie zapisuje**
> (po odświeżeniu mapa miejsc wygląda tak samo).

Schemat bazy (mermaid + szkic DDL): [`docs/database-schema.md`](docs/database-schema.md)

## Wymagania

| Narzędzie | Wersja |
|---|---|
| JDK | 21 |
| Node.js | 20.19+ (testowane na 25) |
| npm | 10+ |

Gradle nie jest potrzebny — repo zawiera wrapper (`gradlew`).

## Uruchomienie

Potrzebne są dwa terminale.

**1. Backend (Spring Boot, port 8080)**

```bash
cd backend
./gradlew bootRun          # Windows: gradlew.bat bootRun
```

**2. Frontend (React + Vite, port 5173)**

```bash
cd frontend
npm install                # tylko za pierwszym razem
npm run dev
```

Otwórz **http://localhost:5173**. Vite przekierowuje `/api/*` na `http://localhost:8080`, więc CORS nie jest problemem.

| Co | Adres |
|---|---|
| Aplikacja | http://localhost:5173 |
| API | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

### Inne komendy

```bash
# backend
./gradlew test             # testy
./gradlew build            # kompilacja + testy + jar

# frontend
npm run build              # typecheck (tsc) + build produkcyjny
npm run lint               # oxlint
```

## IntelliJ IDEA

Repo zawiera współdzielone konfiguracje uruchomieniowe w `.run/`:

| Konfiguracja | Co robi |
|---|---|
| **Backend** | Gradle `bootRun` w `backend/` (działa też Debug) |
| **Backend tests** | Gradle `test` |
| **Frontend** | `npm run dev` w `frontend/` (wymaga IntelliJ **Ultimate** — wtyczka JavaScript) |
| **Backend + Frontend** | odpala obie naraz |

1. *File → Open…* → wskaż **katalog główny repo** (ten z `README.md`), *Trust Project*.
2. Podłącz backend jako projekt Gradle: kliknij *Load Gradle Project* w powiadomieniu
   albo prawym na `backend/build.gradle.kts` → *Link Gradle Project*.
3. *File → Project Structure → Project → SDK*: JDK **21**.
   *Settings → Build Tools → Gradle → Gradle JVM*: też JDK 21.
4. *Settings → Languages & Frameworks → Node.js*: wybierz interpreter Node (20.19+).
   Za pierwszym razem: prawym na `frontend/package.json` → *Run 'npm install'*.
5. Z listy konfiguracji (prawy górny róg) wybierz **Backend + Frontend** → ▶.

IntelliJ Community nie ma konfiguracji npm — wtedy backend z IntelliJ, a frontend w terminalu (`npm run dev`).

## API (mock)

| Metoda | Ścieżka | Opis |
|---|---|---|
| GET | `/api/events?category=MOVIE` | Lista wydarzeń (filtr opcjonalny: `MOVIE`, `TRAIN_RIDE`, `CONCERT`, `LECTURE`) |
| GET | `/api/events/{id}` | Szczegóły wydarzenia z salą |
| GET | `/api/events/{id}/seats` | Mapa miejsc wydarzenia (status, cena, kategoria) |
| GET | `/api/venues`, `/api/venues/{id}` | Sale |
| GET | `/api/users`, `/api/users/{id}` | Użytkownicy (w UI zamiast logowania jest przełącznik użytkownika) |
| GET | `/api/users/{id}/reservations` | Rezerwacje użytkownika |
| POST | `/api/reservations` | Rezerwacja miejsc — **mock, bez zapisu** |
| DELETE | `/api/reservations/{id}` | Anulowanie — **mock, bez zapisu** |

Przykład:

```bash
curl -X POST http://localhost:8080/api/reservations \
  -H 'Content-Type: application/json' \
  -d '{"userId": 1, "eventId": 1, "eventSeatIds": [1, 2]}'
```

### Strategia współbieżności

Klient **nie wybiera** sposobu rezerwacji — wszyscy użytkownicy idą tą samą ścieżką. Strategię ustawia się
w `backend/src/main/resources/application.properties`:

```properties
app.reservation.locking-strategy=PESSIMISTIC
```

Dostępne: `PESSIMISTIC`, `OPTIMISTIC`, `ISOLATION`, `UNIQUE_CONSTRAINT`. `ReservationStrategyResolver` przy starcie
wybiera odpowiednią implementację `SeatReservationStrategy` (pakiet `reservation/strategy/`) i loguje
`Active reservation locking strategy: ...`. Można też nadpisać bez edycji pliku:

```bash
./gradlew bootRun --args='--app.reservation.locking-strategy=OPTIMISTIC'
```

Na razie wszystkie cztery strategie to mocki z tą samą logiką; po podpięciu bazy każda dostanie własną implementację.

Błędy są zwracane jako `ProblemDetail` (RFC 9457): `400` walidacja, `404` brak zasobu, `409` miejsce zajęte
(pole `unavailableEventSeatIds`).

## Struktura

```
.
├── backend/                         Spring Boot 4, Java 21, Gradle (Kotlin DSL)
│   └── src/main/java/pl/agh/seatreservation/
│       ├── config/                  CORS, OpenAPI
│       ├── common/exception/        wyjątki + GlobalExceptionHandler (ProblemDetail)
│       ├── mock/                    MockDataStore + generator danych (zamiast bazy)
│       ├── reservation/strategy/    strategie rezerwacji + resolver (wybór z application.properties)
│       ├── venue/  event/  user/  reservation/
│       │   ├── *Controller          REST
│       │   ├── *Service             interfejs (kontroler zależy tylko od niego)
│       │   ├── Mock*Service         obecna implementacja na mockach
│       │   ├── modele domenowe      rekordy = przyszłe tabele
│       │   └── dto/                 rekordy DTO
├── frontend/                        React 19 + TypeScript + Vite
│   └── src/
│       ├── api/                     klient HTTP + funkcje per zasób
│       ├── types/                   typy odpowiadające DTO z backendu
│       ├── components/              layout, ui, events, seat-map, reservation
│       ├── pages/                   EventsPage, EventDetailsPage, MyReservationsPage
│       ├── hooks/  context/         useAsync, useSeatSelection, aktualny użytkownik
│       ├── constants/  utils/       etykiety PL, układ sal, formatowanie
│       └── styles/                  tokeny (kolory/fonty z prezentacji) + style globalne
└── docs/
    └── database-schema.md           schemat bazy (mermaid) + szkic DDL
```

## Co dalej (podpięcie bazy)

1. PostgreSQL (np. Docker) + migracje z `docs/database-schema.md`, dane z generatora (Python + Faker).
2. Encje JPA / repozytoria, nowe implementacje `*Service` zamiast `Mock*Service` — kontrolery i DTO zostają bez zmian.
3. Prawdziwe implementacje `SeatReservationStrategy` (FOR UPDATE, `version`, poziomy izolacji, UNIQUE) i testy współbieżności (wiele wątków, `pgbench`).
