package pl.agh.seatreservation.event.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import pl.agh.seatreservation.event.EventCategory;
import pl.agh.seatreservation.venue.VenueType;

public record EventSummaryDto(
        Long id,
        String title,
        EventCategory category,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt,
        String venueName,
        String city,
        VenueType venueType,
        int totalSeats,
        int availableSeats,
        BigDecimal minPrice) {
}
