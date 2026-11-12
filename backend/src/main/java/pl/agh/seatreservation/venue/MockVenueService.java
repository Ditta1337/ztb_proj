package pl.agh.seatreservation.venue;

import java.util.List;

import org.springframework.stereotype.Service;

import pl.agh.seatreservation.common.exception.ResourceNotFoundException;
import pl.agh.seatreservation.mock.MockDataStore;
import pl.agh.seatreservation.venue.dto.VenueDto;

@Service
class MockVenueService implements VenueService {

    private final MockDataStore store;

    MockVenueService(MockDataStore store) {
        this.store = store;
    }

    @Override
    public List<VenueDto> findAll() {
        return store.venues().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public VenueDto findById(Long id) {
        return store.findVenue(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", id));
    }

    private VenueDto toDto(Venue venue) {
        return VenueDto.from(venue, store.seatsOfVenue(venue.id()).size());
    }
}
