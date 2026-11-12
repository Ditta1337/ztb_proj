package pl.agh.seatreservation.reservation.strategy;

import java.time.OffsetDateTime;
import java.util.List;

import pl.agh.seatreservation.common.exception.ResourceNotFoundException;
import pl.agh.seatreservation.common.exception.SeatUnavailableException;
import pl.agh.seatreservation.event.EventSeat;
import pl.agh.seatreservation.mock.MockDataStore;
import pl.agh.seatreservation.reservation.Reservation;
import pl.agh.seatreservation.reservation.ReservationStatus;

abstract class MockSeatReservationStrategy implements SeatReservationStrategy {

    private final MockDataStore store;

    protected MockSeatReservationStrategy(MockDataStore store) {
        this.store = store;
    }

    @Override
    public List<Reservation> reserve(Long userId, Long eventId, List<Long> eventSeatIds) {
        List<EventSeat> seats = eventSeatIds.stream().map(this::getEventSeat).toList();
        ensureAllAvailable(seats);

        OffsetDateTime now = OffsetDateTime.now();
        return seats.stream()
                .map(seat -> new Reservation(
                        store.nextReservationId(),
                        userId,
                        eventId,
                        seat.id(),
                        ReservationStatus.CONFIRMED,
                        now,
                        null))
                .toList();
    }

    private EventSeat getEventSeat(Long eventSeatId) {
        return store.findEventSeat(eventSeatId)
                .orElseThrow(() -> new ResourceNotFoundException("EventSeat", eventSeatId));
    }

    private static void ensureAllAvailable(List<EventSeat> seats) {
        List<Long> unavailable = seats.stream()
                .filter(seat -> !seat.isAvailable())
                .map(EventSeat::id)
                .toList();
        if (!unavailable.isEmpty()) {
            throw new SeatUnavailableException(unavailable);
        }
    }
}
