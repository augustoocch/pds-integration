package ar.com.uade.pds.final_project.notifications.model;

import lombok.Getter;

@Getter
public class SubscriberData {
    private Long userId;
    private String address;
    private String type;
    private String event;

    public SubscriberData(Long userId, String address, String type, String event) {
        this.userId = userId;
        this.address = address;
        this.type = type;
        this.event = event;
    }
}
