package pl.agh.seatreservation.user;

import java.time.OffsetDateTime;

public record User(Long id, String email, String firstName, String lastName, OffsetDateTime createdAt) {
}
