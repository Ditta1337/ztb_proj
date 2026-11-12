package pl.agh.seatreservation.venue;

import java.util.List;

import pl.agh.seatreservation.venue.dto.VenueDto;

public interface VenueService {

    List<VenueDto> findAll();

    VenueDto findById(Long id);
}
