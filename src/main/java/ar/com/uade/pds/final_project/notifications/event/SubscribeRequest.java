package ar.com.uade.pds.final_project.notifications.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SubscribeRequest {
    private Long userId;
    private String address;
    private NotificationType channel;
}
