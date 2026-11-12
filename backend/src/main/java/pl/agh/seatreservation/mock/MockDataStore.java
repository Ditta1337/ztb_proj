package pl.agh.seatreservation.mock;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import pl.agh.seatreservation.event.Event;
import pl.agh.seatreservation.event.EventSeat;
import pl.agh.seatreservation.reservation.Reservation;
import pl.agh.seatreservation.user.User;
import pl.agh.seatreservation.venue.Seat;
import pl.agh.seatreservation.venue.Venue;

@Component
public class MockDataStore {
    private final List<User> users;
    private final List<Venue> venues;
    private final List<Event> events;
    private final Map<Long, User> usersById;
    private final Map<Long, Venue> venuesById;
    private final Map<Long, Seat> seatsById;
    private final Map<Long, Event> eventsById;
    private final Map<Long, EventSeat> eventSeatsById;
    private final Map<Long, Reservation> reservationsById;
    private final Map<Long, List<Seat>> seatsByVenueId;
    private final Map<Long, List<EventSeat>> eventSeatsByEventId;
    private final Map<Long, List<Reservation>> reservationsByUserId;
    private final AtomicLong reservationIdSequence;

    public MockDataStore() {
        this(new MockDataGenerator().generate());
    }

    MockDataStore(MockDataset dataset) {
        this.users = dataset.users();
        this.venues = dataset.venues();
        this.events = dataset.events();
        this.usersById = indexById(dataset.users(), User::id);
        this.venuesById = indexById(dataset.venues(), Venue::id);
        this.seatsById = indexById(dataset.seats(), Seat::id);
        this.eventsById = indexById(dataset.events(), Event::id);
        this.eventSeatsById = indexById(dataset.eventSeats(), EventSeat::id);
        this.reservationsById = indexById(dataset.reservations(), Reservation::id);
        this.seatsByVenueId = groupBy(dataset.seats(), Seat::venueId);
        this.eventSeatsByEventId = groupBy(dataset.eventSeats(), EventSeat::eventId);
        this.reservationsByUserId = groupBy(dataset.reservations(), Reservation::userId);
        long maxReservationId = dataset.reservations().stream().mapToLong(Reservation::id).max().orElse(0);
        this.reservationIdSequence = new AtomicLong(maxReservationId);
    }

    public List<User> users() {
        return users;
    }

    public Optional<User> findUser(Long id) {
        return Optional.ofNullable(usersById.get(id));
    }

    public List<Venue> venues() {
        return venues;
    }

    public Optional<Venue> findVenue(Long id) {
        return Optional.ofNullable(venuesById.get(id));
    }

    public Optional<Seat> findSeat(Long id) {
        return Optional.ofNullable(seatsById.get(id));
    }

    public List<Seat> seatsOfVenue(Long venueId) {
        return seatsByVenueId.getOrDefault(venueId, List.of());
    }

    public List<Event> events() {
        return events;
    }

    public Optional<Event> findEvent(Long id) {
        return Optional.ofNullable(eventsById.get(id));
    }

    public Optional<EventSeat> findEventSeat(Long id) {
        return Optional.ofNullable(eventSeatsById.get(id));
    }

    public List<EventSeat> seatsOfEvent(Long eventId) {
        return eventSeatsByEventId.getOrDefault(eventId, List.of());
    }

    public Optional<Reservation> findReservation(Long id) {
        return Optional.ofNullable(reservationsById.get(id));
    }

    public List<Reservation> reservationsOfUser(Long userId) {
        return reservationsByUserId.getOrDefault(userId, List.of());
    }

    public long nextReservationId() {
        return reservationIdSequence.incrementAndGet();
    }

    private static <T> Map<Long, T> indexById(List<T> items, Function<T, Long> idExtractor) {
        return items.stream().collect(Collectors.toUnmodifiableMap(idExtractor, Function.identity()));
    }

    private static <T> Map<Long, List<T>> groupBy(List<T> items, Function<T, Long> keyExtractor) {
        return items.stream().collect(Collectors.groupingBy(keyExtractor,
                Collectors.collectingAndThen(Collectors.toList(), List::copyOf)));
    }
}
