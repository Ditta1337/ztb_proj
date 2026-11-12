package pl.agh.seatreservation.venue.dto;

import pl.agh.seatreservation.venue.Venue;
import pl.agh.seatreservation.venue.VenueType;

public record VenueDto(Long id, String name, String city, VenueType venueType, int capacity) {

    public static VenueDto from(Venue venue, int capacity) {
        return new VenueDto(venue.id(), venue.name(), venue.city(), venue.venueType(), capacity);
    }
}
