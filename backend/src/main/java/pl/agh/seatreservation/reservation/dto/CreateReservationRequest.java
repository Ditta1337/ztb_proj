package pl.agh.seatreservation.reservation.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateReservationRequest(
        @NotNull @Positive Long userId,
        @NotNull @Positive Long eventId,
        @NotEmpty @Size(max = MAX_SEATS_PER_REQUEST) List<@NotNull @Positive Long> eventSeatIds) {

    public static final int MAX_SEATS_PER_REQUEST = 10;
}
