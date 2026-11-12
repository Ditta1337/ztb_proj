package pl.agh.seatreservation.common.exception;

import java.util.Collection;
import java.util.List;

public class SeatUnavailableException extends RuntimeException {
    private final List<Long> unavailableEventSeatIds;

    public SeatUnavailableException(Collection<Long> unavailableEventSeatIds) {
        super("Seats already reserved: " + unavailableEventSeatIds);
        this.unavailableEventSeatIds = List.copyOf(unavailableEventSeatIds);
    }

    public List<Long> getUnavailableEventSeatIds() {
        return unavailableEventSeatIds;
    }
}
