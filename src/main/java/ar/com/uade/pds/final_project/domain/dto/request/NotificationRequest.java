package ar.com.uade.pds.final_project.domain.dto.request;

import ar.com.uade.pds.final_project.notifications.event.EventType;
import ar.com.uade.pds.final_project.notifications.event.NotificationType;

public class NotificationRequest {
    private EventType eventType;
    private NotificationType notificationType;
    private Long userId;
    private Long scrimId;

    public EventType getEventType() { return eventType; }
    public NotificationType getNotificationType() { return notificationType; }
    public Long getUserId() { return userId; }
    public Long getScrimId() { return scrimId; }

    public void setEventType(EventType eventType) { this.eventType = eventType; }
    public void setNotificationType(NotificationType notificationType) { this.notificationType = notificationType; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setScrimId(Long scrimId) { this.scrimId = scrimId; }
}
