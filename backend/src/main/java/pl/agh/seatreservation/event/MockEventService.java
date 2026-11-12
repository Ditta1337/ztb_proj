package pl.agh.seatreservation.event;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import pl.agh.seatreservation.common.exception.ResourceNotFoundException;
import pl.agh.seatreservation.event.dto.EventDetailsDto;
import pl.agh.seatreservation.event.dto.EventSeatDto;
import pl.agh.seatreservation.event.dto.EventSummaryDto;
import pl.agh.seatreservation.mock.MockDataStore;
import pl.agh.seatreservation.venue.Seat;
import pl.agh.seatreservation.venue.Venue;
import pl.agh.seatreservation.venue.dto.VenueDto;

@Service
class MockEventService implements EventService {

    private static final Comparator<EventSeatDto> SEAT_MAP_ORDER = Comparator
            .comparing(EventSeatDto::seatId);

    private final MockDataStore store;

    MockEventService(MockDataStore store) {
        this.store = store;
    }

    @Override
    public List<EventSummaryDto> findAll(Optional<EventCategory> category) {
        return store.events().stream()
                .filter(event -> category.map(c -> c == event.category()).orElse(true))
                .sorted(Comparator.comparing(Event::startsAt))
                .map(this::toSummary)
                .toList();
    }

    @Override
    public EventDetailsDto findById(Long id) {
        Event event = getEvent(id);
        Venue venue = getVenue(event.venueId());
        List<EventSeat> seats = store.seatsOfEvent(id);
        VenueDto venueDto = VenueDto.from(venue, store.seatsOfVenue(venue.id()).size());
        return new EventDetailsDto(
                event.id(),
                event.title(),
                event.description(),
                event.category(),
                event.startsAt(),
                event.endsAt(),
                venueDto,
                seats.size(),
                countAvailable(seats));
    }

    @Override
    public List<EventSeatDto> findSeats(Long eventId) {
        Event event = getEvent(eventId);
        Map<Long, Seat> seatsById = store.seatsOfVenue(event.venueId()).stream()
                .collect(Collectors.toMap(Seat::id, Function.identity()));
        return store.seatsOfEvent(eventId).stream()
                .map(eventSeat -> toSeatDto(eventSeat, seatsById.get(eventSeat.seatId())))
                .sorted(SEAT_MAP_ORDER)
                .toList();
    }

    private EventSummaryDto toSummary(Event event) {
        Venue venue = getVenue(event.venueId());
        List<EventSeat> seats = store.seatsOfEvent(event.id());
        BigDecimal minPrice = seats.stream()
                .map(EventSeat::price)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
        return new EventSummaryDto(
                event.id(),
                event.title(),
                event.category(),
                event.startsAt(),
                event.endsAt(),
                venue.name(),
                venue.city(),
                venue.venueType(),
                seats.size(),
                countAvailable(seats),
                minPrice);
    }

    private static EventSeatDto toSeatDto(EventSeat eventSeat, Seat seat) {
        return new EventSeatDto(
                eventSeat.id(),
                seat.id(),
                seat.rowLabel(),
                seat.seatNumber(),
                seat.category(),
                eventSeat.price(),
                eventSeat.status());
    }

    private static int countAvailable(List<EventSeat> seats) {
        return (int) seats.stream().filter(EventSeat::isAvailable).count();
    }

    private Event getEvent(Long id) {
        return store.findEvent(id).orElseThrow(() -> new ResourceNotFoundException("Event", id));
    }

    private Venue getVenue(Long id) {
        return store.findVenue(id).orElseThrow(() -> new ResourceNotFoundException("Venue", id));
    }
}
