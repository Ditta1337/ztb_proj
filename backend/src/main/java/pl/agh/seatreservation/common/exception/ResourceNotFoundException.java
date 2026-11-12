package pl.agh.seatreservation.common.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Object id) {
        super("%s with id %s not found".formatted(resourceName, id));
    }
}
