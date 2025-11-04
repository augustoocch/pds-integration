package ar.com.uade.pds.final_project.scrim.exception;

public class ScrimException extends RuntimeException {
    private String message;

    public ScrimException(String message) {
        super(message);
    }
}
