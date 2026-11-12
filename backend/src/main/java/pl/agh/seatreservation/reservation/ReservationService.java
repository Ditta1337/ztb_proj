package pl.agh.seatreservation.reservation;

import java.util.List;

import pl.agh.seatreservation.reservation.dto.CreateReservationRequest;
import pl.agh.seatreservation.reservation.dto.ReservationDto;

public interface ReservationService {
    List<ReservationDto> findByUser(Long userId);

    List<ReservationDto> create(CreateReservationRequest request);

    void cancel(Long reservationId);
}
