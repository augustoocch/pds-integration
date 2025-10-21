package ar.com.uade.pds.final_project.notifications.event;

public class DomainEvent {
    private EventType eventType;
    private NotificationType notificationType;
    private Long userId;
    private Long scrimId;

    public DomainEvent(EventType eventType, NotificationType notificationType, Long userId, Long scrimId) {
        this.eventType = eventType;
        this.notificationType = notificationType;
        this.userId = userId;
        this.scrimId = scrimId;
    }

    public EventType getEventType() { return eventType; }
    public NotificationType getNotificationType() { return notificationType; }
    public Long getUserId() { return userId; }
    public Long getScrimId() { return scrimId; }
}
