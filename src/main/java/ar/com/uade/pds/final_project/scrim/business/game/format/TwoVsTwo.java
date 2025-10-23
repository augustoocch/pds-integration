package ar.com.uade.pds.final_project.scrim.business.game.format;

import ar.com.uade.pds.final_project.domain.dto.request.GameFormatValue;
import ar.com.uade.pds.final_project.users.entity.Role;

import java.util.List;

public class TwoVsTwo extends GameFormat {

    @Override
    public String getName() {
        return GameFormatValue.TWO_VS_TWO.getValue();
    }

    @Override
    public int getPlayersNumber() {
        return 4;
    }

    @Override
    public int getGameEstDuration() {
        return 30;
    }

    @Override
    public List<Role> getAvailableRoles() {
        return Role.twoVsTwoRoles();
    }
}
