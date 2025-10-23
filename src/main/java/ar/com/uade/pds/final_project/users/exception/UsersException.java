package ar.com.uade.pds.final_project.users.exception;

public class UsersException extends RuntimeException {
    private String message;

    public UsersException(String message) {
        super(message);
    }

    public UsersException(String message, Throwable cause) {
        super(message, cause);
    }
}
