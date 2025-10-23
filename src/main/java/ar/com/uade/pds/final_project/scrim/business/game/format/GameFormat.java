package ar.com.uade.pds.final_project.scrim.business.game.format;

import ar.com.uade.pds.final_project.domain.dto.request.GameFormatValue;
import ar.com.uade.pds.final_project.users.entity.Role;

import java.util.List;

import static ar.com.uade.pds.final_project.domain.dto.request.GameFormatValue.ONE_VS_ONE;

public abstract class GameFormat {
    public abstract String getName();
    public abstract int getPlayersNumber();
    public abstract int getGameEstDuration();
    public abstract List<Role> getAvailableRoles();

    public static GameFormat getFormatFromValue(String format) {
        GameFormatValue formatValue = GameFormatValue.fromString(format);
        switch (formatValue) {
            case ONE_VS_ONE:
                return new OneVsOne();
            case TWO_VS_TWO:
                return new TwoVsTwo();
            case FIVE_VS_FIVE:
                return new FiveVsFive();
            default:
                throw new IllegalArgumentException("Invalid game format: " + format);
        }
    }

    public static GameFormat fromString(String format) {
        String toUpperCaseFormat = format.toUpperCase();
        return getFormatFromValue(toUpperCaseFormat);
    }
}
