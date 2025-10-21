package ar.com.uade.pds.final_project.notifications.repository.impl;

import ar.com.uade.pds.final_project.notifications.event.DomainEvent;
import ar.com.uade.pds.final_project.notifications.model.SubscriberData;
import ar.com.uade.pds.final_project.notifications.repository.NotificationRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {

    @Override
    public List<SubscriberData> findSubscribers(DomainEvent event) {
        // TODO: reemplazar con lógica real desde la base de datos
        List<SubscriberData> list = new ArrayList<>();
        list.add(new SubscriberData(event.getUserId(), "usuario@correo.com", "EMAIL", event.getEventType().name()));
        return list;
    }

    @Override
    public void createSubscriber(DomainEvent event) {
        System.out.println("Suscripción creada para user " + event.getUserId());
    }

    @Override
    public void unsubscribe(Long userId, DomainEvent event) {
        System.out.println("Suscripción removida para user " + userId);
    }
}
