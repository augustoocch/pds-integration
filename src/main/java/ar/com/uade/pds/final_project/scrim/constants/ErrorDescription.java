package ar.com.uade.pds.final_project.scrim.constants;

public enum ErrorDescription {
    SCRIM_NOT_FOUND("Scrim not found"),
    USER_NOT_FOUND("User not found"),
    NOT_AVAILABLE_SCRIMS("No available scrims to join"),
    SCRIM_FULL("Scrim is full"),
    INVALID_SCRIM_STATE("Invalid scrim state for this operation"),
    SCRIM_CANNOT_CHANGE_STATE("Scrim cannot be ended in its current state"),
    USER_ALREADY_IN_SCRIM("User is already participating in this scrim"),
    RANGE_MATCHMAKING_ERROR("No suitable scrim found for MMR range"),
    LATENCY_MATCHMAKING_ERROR("No suitable scrim found for latency"),
    NO_COMPATIBLE_SCRIM_FOUND("No compatible scrim found for the given criteria");

    private final String description;

    ErrorDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
