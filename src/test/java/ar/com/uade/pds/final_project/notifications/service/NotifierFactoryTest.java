package ar.com.uade.pds.final_project.notifications.service;

import ar.com.uade.pds.final_project.notifications.event.NotificationType;
import ar.com.uade.pds.final_project.notifications.model.DiscordSubscriber;
import ar.com.uade.pds.final_project.notifications.model.EmailSubscriber;
import ar.com.uade.pds.final_project.notifications.model.ISubscriber;
import ar.com.uade.pds.final_project.notifications.model.PushSubscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotifierFactoryTest {

    private NotificationFactory notificationFactory;

    @BeforeEach
    void setUp() {
        notificationFactory = new NotificationFactory();
    }

    @Test
    void testCreateNotifier_WhenEmailType_ShouldReturnEmailSubscriber() {
        ISubscriber subscriber = notificationFactory.createNotifier(NotificationType.EMAIL);

        assertNotNull(subscriber);
        assertTrue(subscriber instanceof EmailSubscriber);
    }

    @Test
    void testCreateNotifier_WhenDiscordType_ShouldReturnDiscordSubscriber() {
        ISubscriber subscriber = notificationFactory.createNotifier(NotificationType.DISCORD);

        assertNotNull(subscriber);
        assertTrue(subscriber instanceof DiscordSubscriber);
    }

    @Test
    void testCreateNotifier_WhenPushType_ShouldReturnPushSubscriber() {
        ISubscriber subscriber = notificationFactory.createNotifier(NotificationType.PUSH);

        assertNotNull(subscriber);
        assertTrue(subscriber instanceof PushSubscriber);
    }

    @Test
    void testCreateNotifier_WhenEmailType_ShouldReturnSameInstance() {
        ISubscriber subscriber1 = notificationFactory.createNotifier(NotificationType.EMAIL);
        ISubscriber subscriber2 = notificationFactory.createNotifier(NotificationType.EMAIL);

        assertSame(subscriber1, subscriber2, "Debería retornar la misma instancia para EMAIL");
    }

    @Test
    void testCreateNotifier_WhenDiscordType_ShouldReturnSameInstance() {
        ISubscriber subscriber1 = notificationFactory.createNotifier(NotificationType.DISCORD);
        ISubscriber subscriber2 = notificationFactory.createNotifier(NotificationType.DISCORD);

        assertSame(subscriber1, subscriber2, "Debería retornar la misma instancia para DISCORD");
    }

    @Test
    void testCreateNotifier_WhenPushType_ShouldReturnSameInstance() {
        ISubscriber subscriber1 = notificationFactory.createNotifier(NotificationType.PUSH);
        ISubscriber subscriber2 = notificationFactory.createNotifier(NotificationType.PUSH);

        assertSame(subscriber1, subscriber2, "Debería retornar la misma instancia para PUSH");
    }

    @Test
    void testCreateNotifier_WhenDifferentTypes_ShouldReturnDifferentInstances() {
        ISubscriber emailSubscriber = notificationFactory.createNotifier(NotificationType.EMAIL);
        ISubscriber discordSubscriber = notificationFactory.createNotifier(NotificationType.DISCORD);
        ISubscriber pushSubscriber = notificationFactory.createNotifier(NotificationType.PUSH);

        assertNotSame(emailSubscriber, discordSubscriber);
        assertNotSame(emailSubscriber, pushSubscriber);
        assertNotSame(discordSubscriber, pushSubscriber);
    }

    @Test
    void testCreateNotifier_AllTypes_ShouldNotBeNull() {
        assertNotNull(notificationFactory.createNotifier(NotificationType.EMAIL));
        assertNotNull(notificationFactory.createNotifier(NotificationType.DISCORD));
        assertNotNull(notificationFactory.createNotifier(NotificationType.PUSH));
    }

    @Test
    void testCreateNotifier_EmailSubscriber_ShouldImplementISubscriber() {
        ISubscriber subscriber = notificationFactory.createNotifier(NotificationType.EMAIL);

        assertNotNull(subscriber);
        assertDoesNotThrow(() -> subscriber.notify("test@example.com", "Test message"));
    }

    @Test
    void testCreateNotifier_DiscordSubscriber_ShouldImplementISubscriber() {
        ISubscriber subscriber = notificationFactory.createNotifier(NotificationType.DISCORD);

        assertNotNull(subscriber);
        assertDoesNotThrow(() -> subscriber.notify("user#1234", "Test message"));
    }

    @Test
    void testCreateNotifier_PushSubscriber_ShouldImplementISubscriber() {
        ISubscriber subscriber = notificationFactory.createNotifier(NotificationType.PUSH);

        assertNotNull(subscriber);
        assertDoesNotThrow(() -> subscriber.notify("device-token", "Test message"));
    }
}

