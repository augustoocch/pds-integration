package ar.com.uade.pds.final_project.notifications.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DomainEvent {
    private final EventType eventType;
    private final Long userNotNotifiable;
    private final List<Long> subscribersToNotify;
    private final Long scrimId;
}
