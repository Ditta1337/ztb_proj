package pl.agh.seatreservation.reservation.strategy;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import pl.agh.seatreservation.reservation.LockingStrategy;

@ConfigurationProperties(prefix = "app.reservation")
public record ReservationStrategyProperties(@DefaultValue("PESSIMISTIC") LockingStrategy lockingStrategy) {
}
