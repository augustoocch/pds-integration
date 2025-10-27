package ar.com.uade.pds.final_project.notifications.service.impl;

import ar.com.uade.pds.final_project.notifications.event.DomainEvent;
import ar.com.uade.pds.final_project.notifications.event.EventType;
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
        if (subs == null || subs.isEmpty()) {
            System.out.println("⚠️ No hay suscriptores para el evento " + event.getEventType());
            return;
        }

        String message = buildMessage(event);

        for (SubscriberData sub : subs) {
            Subscriber notifier;

            switch (sub.getType().toUpperCase()) {
                case "EMAIL" -> notifier = new EmailSubscriber();
                case "DISCORD" -> notifier = new DiscordSubscriber();
                case "PUSH" -> notifier = new PushSubscriber();
                default -> throw new IllegalArgumentException("Tipo de notificación no soportado: " + sub.getType());
            }

            try {
                notifier.notify(sub.getAddress(), message);
                System.out.printf("Notificación enviada a %s (%s): %s%n",
                        sub.getAddress(), sub.getType(), message);
            } catch (Exception e) {
                System.err.printf("Error enviando notificación a %s (%s): %s%n",
                        sub.getAddress(), sub.getType(), e.getMessage());
            }
        }
    }
    private String buildMessage(DomainEvent event) {
        Long scrimId = event.getScrimId();
        if (event.getEventType() == null) return "Evento desconocido";

        return switch (event.getEventType()) {
            case SCRIM_CREATED -> "Se ha creado un nuevo scrim #" + scrimId;
            case SCRIM_ENDED -> "El scrim #" + scrimId + " ha finalizado.";
            case SCRIM_CANCELLED -> "El scrim #" + scrimId + " fue cancelado.";
            case SCRIM_CONFIRMED -> "El scrim #" + scrimId + " ha sido confirmado.";
            default -> "Nuevo evento: " + event.getEventType();
        };
    }
}
