package pl.agh.seatreservation.venue;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.agh.seatreservation.venue.dto.VenueDto;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @GetMapping
    public List<VenueDto> getVenues() {
        return venueService.findAll();
    }

    @GetMapping("/{id}")
    public VenueDto getVenue(@PathVariable Long id) {
        return venueService.findById(id);
    }
}
