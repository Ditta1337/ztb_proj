package pl.agh.seatreservation.reservation.strategy;

import java.util.List;

import pl.agh.seatreservation.reservation.LockingStrategy;
import pl.agh.seatreservation.reservation.Reservation;

public interface SeatReservationStrategy {

    LockingStrategy type();

    List<Reservation> reserve(Long userId, Long eventId, List<Long> eventSeatIds);
}
