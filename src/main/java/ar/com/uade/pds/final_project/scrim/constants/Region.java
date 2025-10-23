package ar.com.uade.pds.final_project.scrim.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Region {
    LATAM,
    US,
    EU,
    ASIA,
    DEFAULT;

    public static Region fromValue(String value) {
        for (Region region : Region.values()) {
            if (region.name().equalsIgnoreCase(value)) {
                return region;
            }
        }
        return DEFAULT;
    }

    public static String latencyByRegion(Region region) {
        switch (region) {
            case LATAM:
                return "100";
            case US:
                return "80";
            case EU:
                return "70";
            case ASIA:
                return "150";
            default:
                return "120";
        }
    }
}
