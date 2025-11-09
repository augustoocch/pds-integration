package ar.com.uade.pds.final_project.notifications.service;

import ar.com.uade.pds.final_project.notifications.event.DomainEvent;
import ar.com.uade.pds.final_project.notifications.event.EventType;
import ar.com.uade.pds.final_project.notifications.event.NotificationType;
import ar.com.uade.pds.final_project.notifications.event.SubscribeRequest;
import ar.com.uade.pds.final_project.notifications.exception.NotificationException;
import ar.com.uade.pds.final_project.notifications.model.SubscriberData;
import ar.com.uade.pds.final_project.notifications.repository.NotificationRepository;
import ar.com.uade.pds.final_project.notifications.service.impl.NotificationServiceImpl;
import ar.com.uade.pds.final_project.notifications.service.impl.NotificationManager;
import ar.com.uade.pds.final_project.scrim.constants.ErrorDescription;
import ar.com.uade.pds.final_project.users.entity.User;
import ar.com.uade.pds.final_project.users.service.DataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private DataService dataService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationManager notificationManager;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User currentUser;
    private SubscribeRequest subscribeRequest;
    private DomainEvent domainEvent;
    private SubscriberData subscriberData;

    @BeforeEach
    void setUp() {
        currentUser = new User.Builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .build();

        subscribeRequest = SubscribeRequest.builder()
                .userId(1L)
                .address("test@example.com")
                .channel(NotificationType.EMAIL)
                .build();

        domainEvent = new DomainEvent(
                EventType.SCRIM_CREATED,
                1L,
                List.of(),
                1L
        );

        subscriberData = SubscriberData.builder()
                .userId(1L)
                .address("test@example.com")
                .channel(NotificationType.EMAIL)
                .build();
    }

    @Test
    void testSubscribe_Success() {
        when(notificationRepository.save(any(SubscriberData.class))).thenReturn(subscriberData);

        notificationService.subscribe(subscribeRequest);

        verify(notificationRepository, times(1)).save(any(SubscriberData.class));
    }

    @Test
    void testProcess_Success() {
        doNothing().when(notificationManager).processEvent(any(DomainEvent.class));

        notificationService.process(domainEvent);

        verify(notificationManager, times(1)).processEvent(domainEvent);
    }

    @Test
    void testUnsubscribe_Success() {
        List<SubscriberData> subscriptions = List.of(subscriberData);
        
        when(dataService.findUserWithToken()).thenReturn(currentUser);
        when(notificationRepository.findByUserId(1L)).thenReturn(subscriptions);
        doNothing().when(notificationRepository).deleteAll(anyList());

        notificationService.unsubscribe();

        verify(dataService, times(1)).findUserWithToken();
        verify(notificationRepository, times(1)).findByUserId(1L);
        verify(notificationRepository, times(1)).deleteAll(subscriptions);
    }

    @Test
    void testUnsubscribe_UserNotFound() {
        when(dataService.findUserWithToken()).thenReturn(null);

        NotificationException exception = assertThrows(NotificationException.class,
                () -> notificationService.unsubscribe());

        assertEquals(ErrorDescription.USER_NOT_FOUND.getDescription(), exception.getMessage());
        verify(dataService, times(1)).findUserWithToken();
        verify(notificationRepository, never()).findByUserId(any());
    }

    @Test
    void testUnsubscribe_NoSubscriptions() {
        when(dataService.findUserWithToken()).thenReturn(currentUser);
        when(notificationRepository.findByUserId(1L)).thenReturn(new ArrayList<>());

        NotificationException exception = assertThrows(NotificationException.class,
                () -> notificationService.unsubscribe());

        assertTrue(exception.getMessage().contains("No subscriptions found"));
        verify(dataService, times(1)).findUserWithToken();
        verify(notificationRepository, times(1)).findByUserId(1L);
        verify(notificationRepository, never()).deleteAll(anyList());
    }
}

