package pl.agh.seatreservation.reservation;

import java.time.OffsetDateTime;

public record Reservation(
        Long id,
        Long userId,
        Long eventId,
        Long eventSeatId,
        ReservationStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime cancelledAt) {
}
