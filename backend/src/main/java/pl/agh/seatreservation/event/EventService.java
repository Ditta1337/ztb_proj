package pl.agh.seatreservation.event;

import java.util.List;
import java.util.Optional;

import pl.agh.seatreservation.event.dto.EventDetailsDto;
import pl.agh.seatreservation.event.dto.EventSeatDto;
import pl.agh.seatreservation.event.dto.EventSummaryDto;

public interface EventService {

    List<EventSummaryDto> findAll(Optional<EventCategory> category);

    EventDetailsDto findById(Long id);

    List<EventSeatDto> findSeats(Long eventId);
}
