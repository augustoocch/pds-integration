package ar.com.uade.pds.final_project.notifications.service.impl;

import ar.com.uade.pds.final_project.notifications.event.DomainEvent;
import ar.com.uade.pds.final_project.notifications.event.SubscribeRequest;
import ar.com.uade.pds.final_project.notifications.exception.NotificationException;
import ar.com.uade.pds.final_project.notifications.model.SubscriberData;
import ar.com.uade.pds.final_project.notifications.repository.NotificationRepository;
import ar.com.uade.pds.final_project.notifications.service.NotificationService;
import ar.com.uade.pds.final_project.scrim.constants.ErrorDescription;
import ar.com.uade.pds.final_project.users.entity.User;
import ar.com.uade.pds.final_project.users.service.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final DataService dataService;
    private final NotificationRepository notificationRepository;
    private final NotificationManager notificationManager;

    @Override
    public void subscribe(SubscribeRequest request) {
        SubscriberData subscriber = SubscriberData.builder()
                .userId(request.getUserId())
                .address(request.getAddress())
                .channel(request.getChannel())
                .build();
        notificationRepository.save(subscriber);
    }

    @Override
    public void process(DomainEvent event) {
        notificationManager.processEvent(event);
    }

    @Override
    public void unsubscribe() {
        User currentUser = dataService.findUserWithToken();
        if(currentUser == null) {
            throw new NotificationException(ErrorDescription.USER_NOT_FOUND.getDescription());
        }
        List<SubscriberData> subs = notificationRepository.findByUserId(currentUser.getId())
                .stream()
                .toList();

        if(subs.isEmpty()) {
            throw new NotificationException("No subscriptions found for user with ID: " + currentUser.getId());
        }
        notificationRepository.deleteAll(subs);
    }
}