package pl.agh.seatreservation.user.dto;

import pl.agh.seatreservation.user.User;

public record UserDto(Long id, String email, String firstName, String lastName) {

    public static UserDto from(User user) {
        return new UserDto(user.id(), user.email(), user.firstName(), user.lastName());
    }
}
