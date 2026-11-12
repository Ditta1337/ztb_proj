package pl.agh.seatreservation.reservation.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

import pl.agh.seatreservation.mock.MockDataStore;
import pl.agh.seatreservation.reservation.LockingStrategy;

class ReservationStrategyResolverTest {

    private final MockDataStore store = new MockDataStore();
    private final List<SeatReservationStrategy> allStrategies = List.of(
            new PessimisticReservationStrategy(store),
            new OptimisticReservationStrategy(store),
            new IsolationLevelReservationStrategy(store),
            new UniqueConstraintReservationStrategy(store));

    @Test
    void shouldPickStrategyFromProperties() {
        for (LockingStrategy configured : LockingStrategy.values()) {
            ReservationStrategyResolver resolver =
                    new ReservationStrategyResolver(allStrategies, new ReservationStrategyProperties(configured));

            assertThat(resolver.activeStrategy().type()).isEqualTo(configured);
        }
    }

    @Test
    void shouldFailFastWhenConfiguredStrategyIsMissing() {
        List<SeatReservationStrategy> onlyPessimistic = List.of(new PessimisticReservationStrategy(store));
        ReservationStrategyProperties properties = new ReservationStrategyProperties(LockingStrategy.OPTIMISTIC);

        assertThatThrownBy(() -> new ReservationStrategyResolver(onlyPessimistic, properties))
                .isInstanceOf(IllegalStateException.class);
    }
}
