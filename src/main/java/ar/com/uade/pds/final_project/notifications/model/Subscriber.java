package ar.com.uade.pds.final_project.notifications.model;

public interface Subscriber {
    void notify(String address, String message);
}
