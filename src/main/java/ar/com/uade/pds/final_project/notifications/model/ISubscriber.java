package ar.com.uade.pds.final_project.notifications.model;

public interface ISubscriber {
    void notify(String address, String message);
}
