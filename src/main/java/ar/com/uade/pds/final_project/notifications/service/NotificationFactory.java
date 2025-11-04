package ar.com.uade.pds.final_project.notifications.service;

import ar.com.uade.pds.final_project.notifications.event.NotificationType;
import ar.com.uade.pds.final_project.notifications.model.DiscordSubscriber;
import ar.com.uade.pds.final_project.notifications.model.EmailSubscriber;
import ar.com.uade.pds.final_project.notifications.model.INotifier;
import ar.com.uade.pds.final_project.notifications.model.PushSubscriber;

import java.util.Map;

public class NotificationFactory {

    Map<NotificationType, INotifier> notifierMap = Map.of(
        NotificationType.EMAIL, new EmailSubscriber(),
        NotificationType.DISCORD, new DiscordSubscriber(),
        NotificationType.PUSH, new PushSubscriber()
    );

    public INotifier createNotifier(NotificationType channel) {
        return switch (channel) {
            case EMAIL -> notifierMap.get(NotificationType.EMAIL);
            case DISCORD -> notifierMap.get(NotificationType.DISCORD);
            case PUSH -> notifierMap.get(NotificationType.PUSH);
        };
    }
}
