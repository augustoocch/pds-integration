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

    public static Integer latencyByRegion(Region region) {
        return switch (region) {
            case LATAM -> 100;
            case US -> 80;
            case EU -> 70;
            case ASIA -> 150;
            default -> 120;
        };
    }
}
