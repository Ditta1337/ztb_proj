package pl.agh.seatreservation.event.dto;

import java.math.BigDecimal;

import pl.agh.seatreservation.event.SeatStatus;
import pl.agh.seatreservation.venue.SeatCategory;

public record EventSeatDto(
        Long id,
        Long seatId,
        String rowLabel,
        int seatNumber,
        SeatCategory category,
        BigDecimal price,
        SeatStatus status) {
}
