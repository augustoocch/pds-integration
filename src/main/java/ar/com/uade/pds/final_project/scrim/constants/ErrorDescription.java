package ar.com.uade.pds.final_project.scrim.constants;

public enum ErrorDescription {
    SCRIM_NOT_FOUND("Scrim not found"),
    USER_NOT_FOUND("User not found"),
    NOT_AVAILABLE_SCRIMS("No available scrims to join"),
    SCRIM_FULL("Scrim is full"),
    INVALID_SCRIM_STATE("Invalid scrim state for this operation"),
    SCRIM_CANNOT_CHANGE_STATE("Scrim cannot be ended in its current state"),
    USER_ALREADY_IN_OTHER_SCRIM("User is already participating in other scrim"),
    USER_ALREADY_IN_SCRIM("User is already participating in this scrim"),
    RANGE_MATCHMAKING_ERROR("No suitable scrim found for MMR range"),
    LATENCY_MATCHMAKING_ERROR("No suitable scrim found for latency"),
    NO_COMPATIBLE_SCRIM_FOUND("No compatible scrim found for the given criteria"),
    MUST_BE_ORGANIZER_TO_SWAP("Only the organizer can swap players"),
    MUST_BE_ORGANIZER_TO_ASSIGN("Only the organizer can assign roles."),
    PLAYER_NOT_IN_SCRIM("El jugador no está en este scrim"),
    PLAYER_A_NOT_IN_SCRIM("El jugador A no está en este scrim"),
    PLAYER_B_NOT_IN_SCRIM("El jugador A no está en este scrim");

    private final String description;

    ErrorDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
