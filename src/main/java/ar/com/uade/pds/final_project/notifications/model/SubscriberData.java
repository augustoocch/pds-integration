package ar.com.uade.pds.final_project.notifications.model;

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

    public Long getUserId() { return userId; }
    public String getAddress() { return address; }
    public String getType() { return type; }
    public String getEvent() { return event; }
}
