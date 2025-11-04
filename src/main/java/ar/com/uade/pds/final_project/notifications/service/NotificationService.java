package ar.com.uade.pds.final_project.notifications.service;

import ar.com.uade.pds.final_project.notifications.event.DomainEvent;
import ar.com.uade.pds.final_project.notifications.event.SubscribeRequest;

public interface NotificationService {
    void subscribe(SubscribeRequest request);
    void process(DomainEvent event);
    void unsubscribe();
}
