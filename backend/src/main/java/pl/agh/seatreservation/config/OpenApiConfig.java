package pl.agh.seatreservation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI seatReservationOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Seat Reservation API")
                .description("System rezerwacji miejsc – endpointy na mockach (bez bazy danych)")
                .version("0.0.1"));
    }
}
