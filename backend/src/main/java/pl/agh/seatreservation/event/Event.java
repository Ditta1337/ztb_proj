package pl.agh.seatreservation.event;

import java.time.OffsetDateTime;

public record Event(
        Long id,
        Long venueId,
        String title,
        String description,
        EventCategory category,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt) {
}
