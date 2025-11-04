package ar.com.uade.pds.final_project.notifications.model;

public class PushSubscriber implements INotifier {
    @Override
    public void notify(String address, String message) {
        System.out.println("[PUSH] Notificando a " + address + ": " + message);
    }
}
