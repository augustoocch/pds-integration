package ar.com.uade.pds.final_project.scrim.business.game.state;

import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.scrim.exception.ScrimException;
import ar.com.uade.pds.final_project.users.entity.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class InGame implements ScrimState {

    @Override
    public void start(Scrim scrim) {
        throw new IllegalStateException("Scrim already in game");
    }

    @Override
    public void cancel(Scrim scrim, Long userId) {
        if (!Objects.equals(userId, scrim.getIdCreator()))
            throw new ScrimException("Solo el creador puede cancelar el scrim");

        scrim.setState(new Cancelled(scrim, userId));
        log.info("Scrim cancelado");
    }

    @Override
    public void confirm(Scrim scrim, User user) {
        throw new IllegalStateException("Scrim already In Game");
    }

    @Override
    public void end(Scrim scrim) {
        scrim.setState(new Ended(scrim));
    }
}
