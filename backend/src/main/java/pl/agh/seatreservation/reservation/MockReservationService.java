package pl.agh.seatreservation.reservation;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import pl.agh.seatreservation.common.exception.InvalidReservationException;
import pl.agh.seatreservation.common.exception.ResourceNotFoundException;
import pl.agh.seatreservation.event.Event;
import pl.agh.seatreservation.event.EventSeat;
import pl.agh.seatreservation.mock.MockDataStore;
import pl.agh.seatreservation.reservation.dto.CreateReservationRequest;
import pl.agh.seatreservation.reservation.dto.ReservationDto;
import pl.agh.seatreservation.reservation.strategy.ReservationStrategyResolver;
import pl.agh.seatreservation.venue.Seat;
import pl.agh.seatreservation.venue.Venue;

@Service
class MockReservationService implements ReservationService {
    private static final Comparator<ReservationDto> NEWEST_FIRST =
            Comparator.comparing(ReservationDto::createdAt).reversed();

    private final MockDataStore store;
    private final ReservationStrategyResolver strategyResolver;

    MockReservationService(MockDataStore store, ReservationStrategyResolver strategyResolver) {
        this.store = store;
        this.strategyResolver = strategyResolver;
    }

    @Override
    public List<ReservationDto> findByUser(Long userId) {
        ensureUserExists(userId);
        return store.reservationsOfUser(userId).stream()
                .map(this::toDto)
                .sorted(NEWEST_FIRST)
                .toList();
    }

    @Override
    public List<ReservationDto> create(CreateReservationRequest request) {
        ensureUserExists(request.userId());
        Event event = getEvent(request.eventId());
        ensureSeatsBelongToEvent(request.eventSeatIds(), event);

        return strategyResolver.activeStrategy()
                .reserve(request.userId(), event.id(), request.eventSeatIds())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public void cancel(Long reservationId) {
        Reservation reservation = store.findReservation(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", reservationId));
        if (reservation.status() == ReservationStatus.CANCELLED) {
            throw new InvalidReservationException("Reservation %d is already cancelled".formatted(reservationId));
        }
    }

    private void ensureSeatsBelongToEvent(List<Long> eventSeatIds, Event event) {
        Set<Long> uniqueIds = new HashSet<>(eventSeatIds);
        if (uniqueIds.size() != eventSeatIds.size()) {
            throw new InvalidReservationException("Request contains duplicated seats");
        }
        for (Long id : eventSeatIds) {
            boolean belongsToEvent = store.findEventSeat(id)
                    .map(eventSeat -> eventSeat.eventId().equals(event.id()))
                    .orElse(false);
            if (!belongsToEvent) {
                throw new InvalidReservationException("Seat %d does not belong to event %d".formatted(id, event.id()));
            }
        }
    }

    private ReservationDto toDto(Reservation reservation) {
        Event event = getEvent(reservation.eventId());
        Venue venue = store.findVenue(event.venueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue", event.venueId()));
        EventSeat eventSeat = store.findEventSeat(reservation.eventSeatId())
                .orElseThrow(() -> new ResourceNotFoundException("EventSeat", reservation.eventSeatId()));
        Seat seat = store.findSeat(eventSeat.seatId())
                .orElseThrow(() -> new ResourceNotFoundException("Seat", eventSeat.seatId()));
        return new ReservationDto(
                reservation.id(),
                reservation.userId(),
                event.id(),
                event.title(),
                event.startsAt(),
                venue.name(),
                eventSeat.id(),
                seat.rowLabel(),
                seat.seatNumber(),
                eventSeat.price(),
                reservation.status(),
                reservation.createdAt(),
                reservation.cancelledAt());
    }

    private void ensureUserExists(Long userId) {
        if (store.findUser(userId).isEmpty()) {
            throw new ResourceNotFoundException("User", userId);
        }
    }

    private Event getEvent(Long eventId) {
        return store.findEvent(eventId).orElseThrow(() -> new ResourceNotFoundException("Event", eventId));
    }
}
