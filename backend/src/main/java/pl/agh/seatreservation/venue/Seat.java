package pl.agh.seatreservation.venue;

public record Seat(Long id, Long venueId, String rowLabel, int seatNumber, SeatCategory category) {
}
