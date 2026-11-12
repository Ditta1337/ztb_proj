package pl.agh.seatreservation.event;

import java.math.BigDecimal;

public record EventSeat(Long id, Long eventId, Long seatId, BigDecimal price, SeatStatus status, long version) {
    public boolean isAvailable() {
        return status == SeatStatus.AVAILABLE;
    }
}
