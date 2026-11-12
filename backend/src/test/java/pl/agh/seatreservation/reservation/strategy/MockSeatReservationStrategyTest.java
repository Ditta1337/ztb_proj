package pl.agh.seatreservation.reservation.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

import pl.agh.seatreservation.common.exception.SeatUnavailableException;
import pl.agh.seatreservation.event.EventSeat;
import pl.agh.seatreservation.mock.MockDataStore;
import pl.agh.seatreservation.reservation.Reservation;
import pl.agh.seatreservation.reservation.ReservationStatus;

class MockSeatReservationStrategyTest {

    private static final long EVENT_ID = 1L;
    private static final long USER_ID = 1L;

    private final MockDataStore store = new MockDataStore();
    private final SeatReservationStrategy strategy = new UniqueConstraintReservationStrategy(store);

    @Test
    void shouldReserveAvailableSeatWithoutPersistingIt() {
        EventSeat freeSeat = firstSeat(true);

        List<Reservation> result = strategy.reserve(USER_ID, EVENT_ID, List.of(freeSeat.id()));

        assertThat(result).singleElement().satisfies(reservation -> {
            assertThat(reservation.eventSeatId()).isEqualTo(freeSeat.id());
            assertThat(reservation.status()).isEqualTo(ReservationStatus.CONFIRMED);
        });
        assertThat(store.findEventSeat(freeSeat.id())).get().extracting(EventSeat::isAvailable).isEqualTo(true);
    }

    @Test
    void shouldRejectAlreadyReservedSeat() {
        EventSeat takenSeat = firstSeat(false);

        assertThatThrownBy(() -> strategy.reserve(USER_ID, EVENT_ID, List.of(takenSeat.id())))
                .isInstanceOf(SeatUnavailableException.class);
    }

    private EventSeat firstSeat(boolean available) {
        return store.seatsOfEvent(EVENT_ID).stream()
                .filter(seat -> seat.isAvailable() == available)
                .findFirst()
                .orElseThrow();
    }
}
