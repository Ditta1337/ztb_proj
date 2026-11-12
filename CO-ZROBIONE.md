## Backend (`backend/`)

Java 21 + Spring Boot 4, Gradle (jest `gradlew`, nie trzeba nic instalować).

- Podzieliłem na pakiety per funkcjonalność: `venue`, `event`, `user`, `reservation` + `config`, `common/exception`, `mock`.
- W każdym pakiecie jest controller, interfejs serwisu i implementacja `Mock...Service`. Controller zna tylko interfejs, więc jak będzie baza, to dopisujemy nową implementację (np. JPA) i controllerów/DTO nie ruszamy.
- DTO są jako Java `record`, z walidacją (`@NotNull`, `@NotEmpty` itd.).
- Modele domenowe (`User`, `Venue`, `Seat`, `Event`, `EventSeat`, `Reservation`) odpowiadają 1:1 tabelom z naszego schematu.
- `MockDataStore` + `MockDataGenerator` – dane w pamięci: 4 sale (kino, pociąg, filharmonia, sala na AGH), 8 wydarzeń, ok. 1100 miejsc, 40 userów i ich rezerwacje. Dane są zawsze takie same po restarcie (stały seed).
- Ceny też są z mocka: każda sala ma cenę bazową, VIP x1.5, wykłady za darmo. Docelowo cena będzie w `event_seats.price`.
- Obsługa błędów w jednym miejscu (`GlobalExceptionHandler`), zawsze ten sam format odpowiedzi:
  - 400 – zła walidacja,
  - 404 – nie ma takiego eventu/usera,
  - 409 – miejsce już zajęte.
- Swagger: http://localhost:8080/swagger-ui.html
- Kilka testów (controllery + serwis rezerwacji), `./gradlew test`.

Endpointy:

| | |
|---|---|
| `GET /api/events` (+ `?category=`) | lista wydarzeń |
| `GET /api/events/{id}` | szczegóły wydarzenia |
| `GET /api/events/{id}/seats` | mapa miejsc |
| `GET /api/venues` | sale |
| `GET /api/users` | userzy (zamiast logowania) |
| `GET /api/users/{id}/reservations` | rezerwacje usera |
| `POST /api/reservations` | rezerwacja – **mock**, zwraca 201, ale nic nie zapisuje |
| `DELETE /api/reservations/{id}` | anulowanie – **mock** |

Strategię blokowania (`PESSIMISTIC`, `OPTIMISTIC`, `ISOLATION`, `UNIQUE_CONSTRAINT`) wybiera backend, a nie frontend – jak Michał zauważył, nie ma sensu, żeby dwóch gości szło innym scenariuszem. Ustawia się ją w `application.properties`:

```properties
app.reservation.locking-strategy=PESSIMISTIC
```

i na tej podstawie `ReservationStrategyResolver` routuje rezerwację do odpowiedniej klasy w `reservation/strategy/` (`PessimisticReservationStrategy`, `OptimisticReservationStrategy` itd.). Na razie wszystkie cztery to mocki z tą samą logiką, każda dostanie swoją implementację jak będzie baza. Przy starcie w logach widać, która strategia jest aktywna. Strategii nie trzymamy w bazie (ani przy rezerwacji, ani przy evencie) – jak chcemy porównać, to odpalamy test kilka razy z innym ustawieniem.

## Frontend (`frontend/`)

React + TypeScript + Vite. Bez żadnego frameworka do CSS, zwykłe CSS Modules. Kolory i fonty wziąłem z naszej prezentacji (beż, czarny, pomarańczowy, Oswald).

- **Lista wydarzeń** – kafelki z datą, salą, zajętością i ceną "od", filtr po kategorii (kino / pociąg / koncert / wykład).
- **Strona wydarzenia** – mapa miejsc (szare = zajęte, pomarańczowe = wybrane, VIP i miejsca dla niepełnosprawnych oznaczone), po prawej podsumowanie: wybrane miejsca, suma i przycisk "Zarezerwuj". Max 10 miejsc naraz.
- **Moje rezerwacje** – lista aktywnych i anulowanych, można kliknąć "Anuluj" (też mock).
- Nie ma logowania – w headerze jest select z userami i tym się przełącza "kto rezerwuje".
- Na górze jest pasek "Tryb demo", żeby było jasne, że nic się nie zapisuje.
- Vite ma proxy `/api` → `localhost:8080`, więc nie ma problemów z CORS.

Struktura w `src/`: `api/` (zapytania do backendu), `types/` (typy jak DTO z backendu), `components/`, `pages/`, `hooks/`, `context/`, `constants/`, `utils/`, `styles/`.
