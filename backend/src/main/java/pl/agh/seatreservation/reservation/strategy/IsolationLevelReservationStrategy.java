package pl.agh.seatreservation.reservation.strategy;

import org.springframework.stereotype.Component;

import pl.agh.seatreservation.mock.MockDataStore;
import pl.agh.seatreservation.reservation.LockingStrategy;

@Component
class IsolationLevelReservationStrategy extends MockSeatReservationStrategy {

    IsolationLevelReservationStrategy(MockDataStore store) {
        super(store);
    }

    @Override
    public LockingStrategy type() {
        return LockingStrategy.ISOLATION;
    }
}
