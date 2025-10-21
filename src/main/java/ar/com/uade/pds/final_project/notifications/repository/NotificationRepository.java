package ar.com.uade.pds.final_project.notifications.repository;

import ar.com.uade.pds.final_project.notifications.event.DomainEvent;
import ar.com.uade.pds.final_project.notifications.model.SubscriberData;

import java.util.List;

public interface NotificationRepository {
    List<SubscriberData> findSubscribers(DomainEvent event);
    void createSubscriber(DomainEvent event);
    void unsubscribe(Long userId, DomainEvent event);
}
