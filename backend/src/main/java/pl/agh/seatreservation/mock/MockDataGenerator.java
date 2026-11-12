package pl.agh.seatreservation.mock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import pl.agh.seatreservation.event.Event;
import pl.agh.seatreservation.event.EventCategory;
import pl.agh.seatreservation.event.EventSeat;
import pl.agh.seatreservation.event.SeatStatus;
import pl.agh.seatreservation.reservation.Reservation;
import pl.agh.seatreservation.reservation.ReservationStatus;
import pl.agh.seatreservation.user.User;
import pl.agh.seatreservation.venue.Seat;
import pl.agh.seatreservation.venue.SeatCategory;
import pl.agh.seatreservation.venue.Venue;
import pl.agh.seatreservation.venue.VenueType;

final class MockDataGenerator {
    private static final ZoneId ZONE = ZoneId.of("Europe/Warsaw");
    private static final long RANDOM_SEED = 2026L;
    private static final int USER_COUNT = 40;
    private static final double CANCELLED_RESERVATION_RATIO = 0.05;
    private static final BigDecimal VIP_PRICE_MULTIPLIER = new BigDecimal("1.5");

    private static final OffsetDateTime BOOKING_WINDOW_START = at(2026, 10, 1, 8, 0);
    private static final int BOOKING_WINDOW_MINUTES = 40 * 24 * 60;

    private static final List<String> FIRST_NAMES = List.of(
            "Anna", "Piotr", "Katarzyna", "Michał", "Magdalena", "Tomasz", "Agnieszka", "Paweł",
            "Joanna", "Krzysztof", "Aleksandra", "Łukasz", "Zofia", "Jakub", "Natalia", "Wojciech");
    private static final List<String> LAST_NAMES = List.of(
            "Nowak", "Kowalski", "Wiśniewski", "Wójcik", "Kamiński", "Lewandowski", "Zieliński",
            "Szymański", "Woźniak", "Dąbrowski", "Kozłowski", "Jankowski", "Mazur", "Krawczyk");

    private final Random random = new Random(RANDOM_SEED);
    private long nextSeatId = 1;
    private long nextEventSeatId = 1;
    private long nextReservationId = 1;

    private record VenueLayout(Venue venue, List<String> rows, int seatsPerRow, Set<String> vipRows,
                               boolean hasAccessibleSeats, BigDecimal basePrice) {
    }

    private record EventDefinition(Event event, double occupancy) {
    }

    MockDataset generate() {
        List<User> users = generateUsers();
        List<VenueLayout> layouts = venueLayouts();
        List<EventDefinition> eventDefinitions = eventDefinitions();

        List<Seat> seats = new ArrayList<>();
        List<EventSeat> eventSeats = new ArrayList<>();
        List<Reservation> reservations = new ArrayList<>();

        for (VenueLayout layout : layouts) {
            List<Seat> venueSeats = generateSeats(layout);
            seats.addAll(venueSeats);
            eventDefinitions.stream()
                    .filter(definition -> definition.event().venueId().equals(layout.venue().id()))
                    .forEach(definition -> generateEventSeats(definition, layout, venueSeats, users,
                            eventSeats, reservations));
        }

        return new MockDataset(
                users,
                layouts.stream().map(VenueLayout::venue).toList(),
                seats,
                eventDefinitions.stream().map(EventDefinition::event).toList(),
                eventSeats,
                reservations);
    }

    private List<User> generateUsers() {
        OffsetDateTime registeredAt = at(2026, 10, 1, 9, 0);
        return IntStream.rangeClosed(1, USER_COUNT)
                .mapToObj(id -> {
                    String firstName = FIRST_NAMES.get((id - 1) % FIRST_NAMES.size());
                    String lastName = adjustLastNameToGender(firstName,
                            LAST_NAMES.get((id * 7) % LAST_NAMES.size()));
                    String email = "%s.%s%d@example.com".formatted(asciiLower(firstName), asciiLower(lastName), id);
                    return new User((long) id, email, firstName, lastName, registeredAt.plusHours(id));
                })
                .toList();
    }

    private static List<VenueLayout> venueLayouts() {
        return List.of(
                new VenueLayout(new Venue(1L, "Kino Orzeł – Sala 1", "Kraków", VenueType.CINEMA),
                        letters('A', 'H'), 12, Set.of("G", "H"), true, new BigDecimal("32.00")),
                new VenueLayout(new Venue(2L, "IC Wawel – wagon 7, klasa 2", "Kraków", VenueType.TRAIN),
                        numbers(1, 12), 4, Set.of(), false, new BigDecimal("89.00")),
                new VenueLayout(new Venue(3L, "Filharmonia nad Wisłą – Sala główna", "Kraków", VenueType.CONCERT_HALL),
                        letters('A', 'L'), 20, Set.of("A", "B", "C"), true, new BigDecimal("120.00")),
                new VenueLayout(new Venue(4L, "AGH, pawilon D-17 – sala 1.38", "Kraków", VenueType.LECTURE_HALL),
                        numbers(1, 10), 16, Set.of(), true, BigDecimal.ZERO.setScale(2)));
    }

    private static List<EventDefinition> eventDefinitions() {
        return List.of(
                event(1L, 1L, "Ostatni seans", "Kameralny dramat o ostatnim wieczorze w starym kinie studyjnym.",
                        EventCategory.MOVIE, at(2026, 11, 20, 19, 0), 120, 0.35),
                event(2L, 1L, "Miasto z mgły", "Thriller science fiction – pokaz przedpremierowy.",
                        EventCategory.MOVIE, at(2026, 11, 22, 17, 30), 105, 0.85),
                event(3L, 2L, "Kraków Główny → Gdańsk Główny", "Poranny przejazd ekspresowy z miejscówkami.",
                        EventCategory.TRAIN_RIDE, at(2026, 11, 27, 6, 15), 330, 0.60),
                event(4L, 2L, "Gdańsk Główny → Kraków Główny", "Popołudniowy powrót, wagon z gniazdkami.",
                        EventCategory.TRAIN_RIDE, at(2026, 11, 29, 15, 40), 330, 0.20),
                event(5L, 3L, "Koncert noworoczny", "Orkiestra kameralna i walce na pożegnanie roku.",
                        EventCategory.CONCERT, at(2026, 12, 31, 20, 0), 150, 0.70),
                event(6L, 3L, "Jazz nad Wisłą", "Wieczór z kwartetem jazzowym – standardy i autorskie kompozycje.",
                        EventCategory.CONCERT, at(2026, 12, 5, 19, 30), 120, 0.40),
                event(7L, 4L, "Transakcje i współbieżność w PostgreSQL", "Wykład: blokady, MVCC i rywalizacja o zasoby.",
                        EventCategory.LECTURE, at(2026, 11, 18, 12, 30), 90, 0.50),
                event(8L, 4L, "Poziomy izolacji w praktyce", "Seminarium: anomalie pod Read Committed, Repeatable Read i Serializable.",
                        EventCategory.LECTURE, at(2026, 12, 2, 14, 0), 90, 0.95));
    }

    private List<Seat> generateSeats(VenueLayout layout) {
        List<Seat> seats = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < layout.rows().size(); rowIndex++) {
            String rowLabel = layout.rows().get(rowIndex);
            for (int number = 1; number <= layout.seatsPerRow(); number++) {
                SeatCategory category = seatCategory(layout, rowIndex, rowLabel, number);
                seats.add(new Seat(nextSeatId++, layout.venue().id(), rowLabel, number, category));
            }
        }
        return seats;
    }

    private static SeatCategory seatCategory(VenueLayout layout, int rowIndex, String rowLabel, int number) {
        if (layout.hasAccessibleSeats() && rowIndex == 0 && number <= 2) {
            return SeatCategory.ACCESSIBLE;
        }
        return layout.vipRows().contains(rowLabel) ? SeatCategory.VIP : SeatCategory.STANDARD;
    }

    private void generateEventSeats(EventDefinition definition, VenueLayout layout, List<Seat> venueSeats,
                                    List<User> users, List<EventSeat> eventSeats, List<Reservation> reservations) {
        Event event = definition.event();
        for (Seat seat : venueSeats) {
            BigDecimal price = price(layout.basePrice(), seat.category());
            long eventSeatId = nextEventSeatId++;
            double roll = random.nextDouble();

            if (roll < definition.occupancy()) {
                eventSeats.add(new EventSeat(eventSeatId, event.id(), seat.id(), price, SeatStatus.RESERVED, 1));
                reservations.add(reservation(event, eventSeatId, randomUser(users), ReservationStatus.CONFIRMED));
            } else if (roll < definition.occupancy() + CANCELLED_RESERVATION_RATIO) {
                eventSeats.add(new EventSeat(eventSeatId, event.id(), seat.id(), price, SeatStatus.AVAILABLE, 2));
                reservations.add(reservation(event, eventSeatId, randomUser(users), ReservationStatus.CANCELLED));
            } else {
                eventSeats.add(new EventSeat(eventSeatId, event.id(), seat.id(), price, SeatStatus.AVAILABLE, 0));
            }
        }
    }

    private Reservation reservation(Event event, long eventSeatId, User user, ReservationStatus status) {
        OffsetDateTime createdAt = BOOKING_WINDOW_START.plusMinutes(random.nextInt(BOOKING_WINDOW_MINUTES))
                .atZoneSameInstant(ZONE).toOffsetDateTime();
        OffsetDateTime cancelledAt = status == ReservationStatus.CANCELLED ? createdAt.plusHours(1 + random.nextInt(48))
                .atZoneSameInstant(ZONE).toOffsetDateTime() : null;
        return new Reservation(nextReservationId++, user.id(), event.id(), eventSeatId, status, createdAt, cancelledAt);
    }

    private User randomUser(List<User> users) {
        return users.get(random.nextInt(users.size()));
    }

    private static BigDecimal price(BigDecimal basePrice, SeatCategory category) {
        BigDecimal price = category == SeatCategory.VIP ? basePrice.multiply(VIP_PRICE_MULTIPLIER) : basePrice;
        return price.setScale(2, RoundingMode.HALF_UP);
    }

    private static EventDefinition event(Long id, Long venueId, String title, String description,
                                         EventCategory category, OffsetDateTime startsAt, int durationMinutes,
                                         double occupancy) {
        Event event = new Event(id, venueId, title, description, category, startsAt, startsAt.plusMinutes(durationMinutes));
        return new EventDefinition(event, occupancy);
    }

    private static OffsetDateTime at(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute).atZone(ZONE).toOffsetDateTime();
    }

    private static List<String> letters(char from, char to) {
        return IntStream.rangeClosed(from, to).mapToObj(c -> String.valueOf((char) c)).toList();
    }

    private static List<String> numbers(int from, int to) {
        return IntStream.rangeClosed(from, to).mapToObj(String::valueOf).toList();
    }

    private static String adjustLastNameToGender(String firstName, String lastName) {
        boolean female = firstName.endsWith("a");
        if (female && lastName.endsWith("ki")) {
            return lastName.substring(0, lastName.length() - 1) + "a";
        }
        return lastName;
    }

    private static String asciiLower(String value) {
        String withoutDiacritics = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("ł", "l")
                .replace("Ł", "L");
        return withoutDiacritics.toLowerCase(Locale.ROOT);
    }
}
