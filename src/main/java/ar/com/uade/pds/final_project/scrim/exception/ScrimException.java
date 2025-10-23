package ar.com.uade.pds.final_project.scrim.exception;

import lombok.AllArgsConstructor;

public class ScrimException extends RuntimeException {
    private String message;

    public ScrimException(String message) {
        super(message);
    }

    public ScrimException(String message, Throwable cause) {
        super(message, cause);
    }
}
