package ar.com.uade.pds.final_project.scrim.business.game.format;

import ar.com.uade.pds.final_project.domain.dto.request.GameFormatValue;
import ar.com.uade.pds.final_project.users.entity.Role;

import java.util.List;

public class OneVsOne extends GameFormat {

    @Override
    public String getName() {
        return GameFormatValue.ONE_VS_ONE.getValue();
    }

    @Override
    public int getPlayersNumber() {
        return 2;
    }

    @Override
    public int getGameEstDuration() {
        return 15;
    }

    @Override
    public List<Role> getAvailableRoles() {
        return Role.oneVsOneRoles();
    }
}
