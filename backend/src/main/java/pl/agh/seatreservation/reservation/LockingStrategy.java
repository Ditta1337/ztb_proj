package pl.agh.seatreservation.reservation;

public enum LockingStrategy {
    PESSIMISTIC,
    OPTIMISTIC,
    ISOLATION,
    UNIQUE_CONSTRAINT
}
