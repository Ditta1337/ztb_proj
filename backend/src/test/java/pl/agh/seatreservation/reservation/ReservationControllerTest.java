package pl.agh.seatreservation.reservation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import pl.agh.seatreservation.common.exception.SeatUnavailableException;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    void shouldRejectRequestWithoutSeats() throws Exception {
        String body = """
                {"userId": 1, "eventId": 1, "eventSeatIds": []}
                """;

        mockMvc.perform(post("/api/reservations").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.eventSeatIds").exists());
    }

    @Test
    void shouldReturnCreatedForValidRequest() throws Exception {
        given(reservationService.create(any())).willReturn(List.of());
        String body = """
                {"userId": 1, "eventId": 1, "eventSeatIds": [5]}
                """;

        mockMvc.perform(post("/api/reservations").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnConflictWhenSeatTaken() throws Exception {
        given(reservationService.create(any())).willThrow(new SeatUnavailableException(List.of(5L)));
        String body = """
                {"userId": 1, "eventId": 1, "eventSeatIds": [5]}
                """;

        mockMvc.perform(post("/api/reservations").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.unavailableEventSeatIds[0]").value(5));
    }
}
