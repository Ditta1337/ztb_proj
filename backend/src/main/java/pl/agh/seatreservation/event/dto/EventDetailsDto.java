package pl.agh.seatreservation.event.dto;

import java.time.OffsetDateTime;

import pl.agh.seatreservation.event.EventCategory;
import pl.agh.seatreservation.venue.dto.VenueDto;

public record EventDetailsDto(
        Long id,
        String title,
        String description,
        EventCategory category,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt,
        VenueDto venue,
        int totalSeats,
        int availableSeats) {
}
