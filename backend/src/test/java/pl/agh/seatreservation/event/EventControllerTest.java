package pl.agh.seatreservation.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import pl.agh.seatreservation.common.exception.ResourceNotFoundException;
import pl.agh.seatreservation.event.dto.EventSummaryDto;
import pl.agh.seatreservation.venue.VenueType;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Test
    void shouldReturnEventsFilteredByCategory() throws Exception {
        OffsetDateTime start = OffsetDateTime.parse("2026-11-20T19:00:00+01:00");
        EventSummaryDto summary = new EventSummaryDto(1L, "Ostatni seans", EventCategory.MOVIE, start,
                start.plusHours(2), "Kino", "Kraków", VenueType.CINEMA, 96, 60, new BigDecimal("32.00"));
        given(eventService.findAll(Optional.of(EventCategory.MOVIE))).willReturn(List.of(summary));

        mockMvc.perform(get("/api/events").param("category", "MOVIE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Ostatni seans"))
                .andExpect(jsonPath("$[0].availableSeats").value(60));
    }

    @Test
    void shouldReturnBadRequestForUnknownCategory() throws Exception {
        mockMvc.perform(get("/api/events").param("category", "OPERA"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundProblemForMissingEvent() throws Exception {
        given(eventService.findById(any())).willThrow(new ResourceNotFoundException("Event", 99L));

        mockMvc.perform(get("/api/events/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Event with id 99 not found"));
    }
}
