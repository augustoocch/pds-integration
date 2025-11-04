package ar.com.uade.pds.final_project.notifications.event;

public enum NotificationType {
    DISCORD,
    EMAIL,
    PUSH;

    public static NotificationType fromString(String type) {
        for (NotificationType nt : NotificationType.values()) {
            if (nt.name().equalsIgnoreCase(type)) {
                return nt;
            }
        }
        return EMAIL;
    }
}

