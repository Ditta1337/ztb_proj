package pl.agh.seatreservation.reservation.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import pl.agh.seatreservation.reservation.ReservationStatus;

public record ReservationDto(
        Long id,
        Long userId,
        Long eventId,
        String eventTitle,
        OffsetDateTime eventStartsAt,
        String venueName,
        Long eventSeatId,
        String rowLabel,
        int seatNumber,
        BigDecimal price,
        ReservationStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime cancelledAt) {
}
