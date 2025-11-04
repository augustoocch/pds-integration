package ar.com.uade.pds.final_project.notifications.exception;

public class NotificationException extends RuntimeException {
    private String message;

    public NotificationException(String message) {
        super(message);
    }
}
