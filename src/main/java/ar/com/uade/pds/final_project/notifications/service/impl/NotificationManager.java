package ar.com.uade.pds.final_project.notifications.service.impl;

import ar.com.uade.pds.final_project.notifications.event.DomainEvent;
import ar.com.uade.pds.final_project.notifications.model.*;
import ar.com.uade.pds.final_project.notifications.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class NotificationManager {
    private final NotificationRepository notificationRepository;

    public void processEvent(DomainEvent event) {
        List<SubscriberData> subs = notificationRepository.findSubscribers(event);
        for (SubscriberData sub : subs) {
            Subscriber notifier;
            switch (sub.getType()) {
                case "EMAIL" -> notifier = new EmailSubscriber();
                case "DISCORD" -> notifier = new DiscordSubscriber();
                case "PUSH" -> notifier = new PushSubscriber();
                default -> throw new IllegalArgumentException("Tipo no soportado");
            }
            notifier.notify(sub.getAddress(), "Evento: " + sub.getEvent());
        }
    }
}
