package pl.agh.seatreservation.user;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.agh.seatreservation.reservation.ReservationService;
import pl.agh.seatreservation.reservation.dto.ReservationDto;
import pl.agh.seatreservation.user.dto.UserDto;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ReservationService reservationService;

    public UserController(UserService userService, ReservationService reservationService) {
        this.userService = userService;
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping("/{id}/reservations")
    public List<ReservationDto> getUserReservations(@PathVariable Long id) {
        return reservationService.findByUser(id);
    }
}
