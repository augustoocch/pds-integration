package ar.com.uade.pds.final_project.notifications.service.impl;

import ar.com.uade.pds.final_project.notifications.event.DomainEvent;
import ar.com.uade.pds.final_project.notifications.repository.NotificationRepository;
import ar.com.uade.pds.final_project.notifications.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private NotificationRepository notificationRepository;
    private NotificationManager notificationManager;

    @Override
    public void subscribe(DomainEvent event) {
        notificationRepository.createSubscriber(event);
    }

    @Override
    public void notify(DomainEvent event) {
        notificationManager.processEvent(event);
    }

    @Override
    public void unsubscribe(DomainEvent event) {
        notificationRepository.unsubscribe(event.getUserId(), event);
    }
}
