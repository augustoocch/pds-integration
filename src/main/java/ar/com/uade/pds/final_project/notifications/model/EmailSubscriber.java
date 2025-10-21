package ar.com.uade.pds.final_project.notifications.model;

public class EmailSubscriber implements Subscriber {
    @Override
    public void notify(String address, String message) {
        System.out.println("[EMAIL] Notificando a " + address + ": " + message);
    }
}
