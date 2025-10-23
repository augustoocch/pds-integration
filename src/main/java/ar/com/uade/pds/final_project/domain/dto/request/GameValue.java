package ar.com.uade.pds.final_project.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameValue {
    DESERT_STRIKE("desert"),
    URBAN_WARFARE("urban"),
    SPACE_BATTLE("space");

    private final String value;

    public static GameValue fromString(String value) {
        for (GameValue format : GameValue.values()) {
            if (format.getValue().equalsIgnoreCase(value)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Invalid game format value: " + value);
    }
}
