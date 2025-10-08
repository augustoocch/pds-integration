package ar.com.uade.pds.final_project.users.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UsersException extends RuntimeException {
    private String message;
}
