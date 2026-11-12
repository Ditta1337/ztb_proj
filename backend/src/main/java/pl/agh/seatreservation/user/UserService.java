package pl.agh.seatreservation.user;

import java.util.List;

import pl.agh.seatreservation.user.dto.UserDto;

public interface UserService {

    List<UserDto> findAll();

    UserDto findById(Long id);
}
