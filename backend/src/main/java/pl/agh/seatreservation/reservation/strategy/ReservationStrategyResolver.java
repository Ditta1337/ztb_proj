package pl.agh.seatreservation.reservation.strategy;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import pl.agh.seatreservation.reservation.LockingStrategy;

@Component
@EnableConfigurationProperties(ReservationStrategyProperties.class)
public class ReservationStrategyResolver {

    private static final Logger log = LoggerFactory.getLogger(ReservationStrategyResolver.class);

    private final SeatReservationStrategy activeStrategy;

    public ReservationStrategyResolver(List<SeatReservationStrategy> strategies,
                                       ReservationStrategyProperties properties) {
        LockingStrategy configured = properties.lockingStrategy();
        this.activeStrategy = strategies.stream()
                .filter(strategy -> strategy.type() == configured)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No reservation strategy registered for " + configured));
        log.info("Active reservation locking strategy: {}", configured);
    }

    public SeatReservationStrategy activeStrategy() {
        return activeStrategy;
    }
}
