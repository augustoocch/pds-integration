package ar.com.uade.pds.final_project.users.constants;

public enum UsersErrorDetails {
    USER_NOT_FOUND("User not found"),
    INVALID_CREDENTIALS("Invalid credentials"),
    USER_NOT_AUTHENTICATED("User not authenticated"),
    USER_EMAIL_ALREADY_EXISTS("User email already exists"),
    USERNAME_ALREADY_EXISTS("Username already exists"),
    EMAIL_NOT_VERIFIED("Email not verified");


    private final String message;

    UsersErrorDetails(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
