package pl.agh.seatreservation.event;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pl.agh.seatreservation.event.dto.EventDetailsDto;
import pl.agh.seatreservation.event.dto.EventSeatDto;
import pl.agh.seatreservation.event.dto.EventSummaryDto;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventSummaryDto> getEvents(@RequestParam Optional<EventCategory> category) {
        return eventService.findAll(category);
    }

    @GetMapping("/{id}")
    public EventDetailsDto getEvent(@PathVariable Long id) {
        return eventService.findById(id);
    }

    @GetMapping("/{id}/seats")
    public List<EventSeatDto> getEventSeats(@PathVariable Long id) {
        return eventService.findSeats(id);
    }
}
