package ar.com.uade.pds.final_project.security.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SecurityException extends RuntimeException {
    private String message;
}
