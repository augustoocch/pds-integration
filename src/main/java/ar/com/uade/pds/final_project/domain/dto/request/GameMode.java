package ar.com.uade.pds.final_project.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameMode {
    RANKED("ranked"),
    UNRANKED("casual");

    private final String value;

    public static GameMode fromString(String value) {
        for (GameMode format : GameMode.values()) {
            if (format.getValue().equalsIgnoreCase(value)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Invalid game format value: " + value);
    }
}
