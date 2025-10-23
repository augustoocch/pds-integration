package ar.com.uade.pds.final_project.scrim.business.game.format;

import ar.com.uade.pds.final_project.domain.dto.request.GameFormatValue;
import ar.com.uade.pds.final_project.users.entity.Role;

import java.util.List;

public class FiveVsFive extends GameFormat {

    @Override
    public String getName() {
        return GameFormatValue.FIVE_VS_FIVE.getValue();
    }

    @Override
    public int getPlayersNumber() {
        return 10;
    }

    @Override
    public int getGameEstDuration() {
        return 45;
    }

    @Override
    public List<Role> getAvailableRoles() {
        return Role.fiveVsFiveRoles();
    }
}
