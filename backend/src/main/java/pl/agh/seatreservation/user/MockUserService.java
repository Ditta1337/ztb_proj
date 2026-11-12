package pl.agh.seatreservation.user;

import java.util.List;

import org.springframework.stereotype.Service;

import pl.agh.seatreservation.common.exception.ResourceNotFoundException;
import pl.agh.seatreservation.mock.MockDataStore;
import pl.agh.seatreservation.user.dto.UserDto;

@Service
class MockUserService implements UserService {

    private final MockDataStore store;

    MockUserService(MockDataStore store) {
        this.store = store;
    }

    @Override
    public List<UserDto> findAll() {
        return store.users().stream()
                .map(UserDto::from)
                .toList();
    }

    @Override
    public UserDto findById(Long id) {
        return store.findUser(id)
                .map(UserDto::from)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
