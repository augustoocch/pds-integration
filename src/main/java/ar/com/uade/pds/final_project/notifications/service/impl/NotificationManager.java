package ar.com.uade.pds.final_project.notifications.service.impl;

import ar.com.uade.pds.final_project.notifications.event.DomainEvent;
import ar.com.uade.pds.final_project.notifications.model.*;
import ar.com.uade.pds.final_project.notifications.repository.NotificationRepository;
import ar.com.uade.pds.final_project.notifications.service.NotificationFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
public class NotificationManager {

    private final NotificationRepository notificationRepository;
    private final NotificationFactory notificationFactory;

    public void processEvent(DomainEvent event) {
        List<SubscriberData> subs = getSubscribersToNotify(event);
        if (subs.isEmpty()) {
            log.info("No hay suscriptores para eventos.");
            return;
        }
        String message = buildMessage(event);
        for (SubscriberData sub : subs) {
            if (Objects.equals(sub.getUserId(), event.getUserNotNotifiable())) continue;
            ISubscriber notifier = notificationFactory.createNotifier(sub.getChannel());
            try {
                notifier.notify(sub.getAddress(), message);
            } catch (Exception e) {
                log.error("Error enviando notificación a {} ({}): {}",
                        sub.getAddress(), sub.getChannel(), e.getMessage());
            }
        }
    }

    private String buildMessage(DomainEvent event) {
        return switch (event.getEventType()) {
            case SCRIM_CREATED -> "Se ha creado un nuevo scrim #" + event.getScrimId();
            case SCRIM_ENDED -> "El scrim #" + event.getScrimId() + " ha finalizado.";
            case SCRIM_CANCELLED -> "El scrim #" + event.getScrimId() + " fue cancelado.";
            case SCRIM_CONFIRMED -> "El scrim #" + event.getScrimId() + " ha sido confirmado.";
            default -> "Nuevo evento: " + event.getEventType();
        };
    }

    private List<SubscriberData> getSubscribersToNotify(DomainEvent event) {
        if (event.getSubscribersToNotify().isEmpty()) {
            return notificationRepository.findAll();
        } else {
            List<SubscriberData> subs = new ArrayList<>();
            for (Long subId : event.getSubscribersToNotify()) {
                notificationRepository.findById(subId).ifPresent(subs::add);
            }
            return subs;
        }
    }
}
