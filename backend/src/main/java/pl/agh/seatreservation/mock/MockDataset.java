package pl.agh.seatreservation.mock;

import java.util.List;

import pl.agh.seatreservation.event.Event;
import pl.agh.seatreservation.event.EventSeat;
import pl.agh.seatreservation.reservation.Reservation;
import pl.agh.seatreservation.user.User;
import pl.agh.seatreservation.venue.Seat;
import pl.agh.seatreservation.venue.Venue;

record MockDataset(
        List<User> users,
        List<Venue> venues,
        List<Seat> seats,
        List<Event> events,
        List<EventSeat> eventSeats,
        List<Reservation> reservations) {
}
