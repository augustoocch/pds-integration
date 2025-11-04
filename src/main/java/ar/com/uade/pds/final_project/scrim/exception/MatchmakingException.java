package ar.com.uade.pds.final_project.scrim.exception;

public class MatchmakingException extends RuntimeException {
    private String message;

    public MatchmakingException(String message) {
        super(message);
    }
}
