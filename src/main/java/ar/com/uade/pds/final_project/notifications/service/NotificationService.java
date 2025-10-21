package ar.com.uade.pds.final_project.notifications.service;

import ar.com.uade.pds.final_project.notifications.event.DomainEvent;

public interface NotificationService {
    void subscribe(DomainEvent event);
    void notify(DomainEvent event);
    void unsubscribe(DomainEvent event);
}
