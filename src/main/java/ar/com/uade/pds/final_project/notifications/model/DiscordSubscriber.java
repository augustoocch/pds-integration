package ar.com.uade.pds.final_project.notifications.model;

public class DiscordSubscriber implements Subscriber {
    @Override
    public void notify(String address, String message) {
        System.out.println("[DISCORD] Notificando a " + address + ": " + message);
    }
}