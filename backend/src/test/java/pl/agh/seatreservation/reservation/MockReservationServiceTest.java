package pl.agh.seatreservation.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.agh.seatreservation.common.exception.InvalidReservationException;
import pl.agh.seatreservation.common.exception.ResourceNotFoundException;
import pl.agh.seatreservation.event.EventSeat;
import pl.agh.seatreservation.mock.MockDataStore;
import pl.agh.seatreservation.reservation.dto.CreateReservationRequest;
import pl.agh.seatreservation.reservation.dto.ReservationDto;
import pl.agh.seatreservation.reservation.strategy.ReservationStrategyProperties;
import pl.agh.seatreservation.reservation.strategy.ReservationStrategyResolver;
import pl.agh.seatreservation.reservation.strategy.SeatReservationStrategy;

class MockReservationServiceTest {

    private static final long EVENT_ID = 1L;
    private static final long OTHER_EVENT_ID = 2L;
    private static final long USER_ID = 1L;

    private final MockDataStore store = new MockDataStore();
    private final RecordingStrategy strategy = new RecordingStrategy();
    private final MockReservationService service = new MockReservationService(store,
            new ReservationStrategyResolver(List.of(strategy),
                    new ReservationStrategyProperties(LockingStrategy.OPTIMISTIC)));

    @Test
    void shouldDelegateReservationToConfiguredStrategy() {
        EventSeat freeSeat = firstSeat(EVENT_ID, true);

        List<ReservationDto> result = service.create(request(EVENT_ID, freeSeat.id()));

        assertThat(strategy.reservedSeatIds).containsExactly(freeSeat.id());
        assertThat(result).singleElement()
                .extracting(ReservationDto::eventSeatId)
                .isEqualTo(freeSeat.id());
    }

    @Test
    void shouldRejectSeatFromAnotherEvent() {
        EventSeat foreignSeat = firstSeat(OTHER_EVENT_ID, true);

        assertThatThrownBy(() -> service.create(request(EVENT_ID, foreignSeat.id())))
                .isInstanceOf(InvalidReservationException.class);
        assertThat(strategy.reservedSeatIds).isEmpty();
    }

    @Test
    void shouldRejectDuplicatedSeats() {
        EventSeat freeSeat = firstSeat(EVENT_ID, true);
        CreateReservationRequest request = new CreateReservationRequest(
                USER_ID, EVENT_ID, List.of(freeSeat.id(), freeSeat.id()));

        assertThatThrownBy(() -> service.create(request)).isInstanceOf(InvalidReservationException.class);
    }

    @Test
    void shouldRejectUnknownUser() {
        EventSeat freeSeat = firstSeat(EVENT_ID, true);
        CreateReservationRequest request = new CreateReservationRequest(999L, EVENT_ID, List.of(freeSeat.id()));

        assertThatThrownBy(() -> service.create(request)).isInstanceOf(ResourceNotFoundException.class);
    }

    private EventSeat firstSeat(long eventId, boolean available) {
        return store.seatsOfEvent(eventId).stream()
                .filter(seat -> seat.isAvailable() == available)
                .findFirst()
                .orElseThrow();
    }

    private static CreateReservationRequest request(long eventId, long eventSeatId) {
        return new CreateReservationRequest(USER_ID, eventId, List.of(eventSeatId));
    }

    private static final class RecordingStrategy implements SeatReservationStrategy {

        private List<Long> reservedSeatIds = List.of();

        @Override
        public LockingStrategy type() {
            return LockingStrategy.OPTIMISTIC;
        }

        @Override
        public List<Reservation> reserve(Long userId, Long eventId, List<Long> eventSeatIds) {
            reservedSeatIds = List.copyOf(eventSeatIds);
            return eventSeatIds.stream()
                    .map(id -> new Reservation(1000L + id, userId, eventId, id, ReservationStatus.CONFIRMED,
                            OffsetDateTime.now(), null))
                    .toList();
        }
    }
}
