package ar.com.uade.pds.final_project.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameFormatValue {
    ONE_VS_ONE("1V1"),
    TWO_VS_TWO("2V2"),
    FIVE_VS_FIVE("5V5");

    private final String value;

    public static GameFormatValue fromString(String value) {
        for (GameFormatValue format : GameFormatValue.values()) {
            if (format.getValue().equalsIgnoreCase(value)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Invalid game format value: " + value);
    }
}
