package ar.com.uade.pds.final_project.scrim.business.game.state;

import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.scrim.entity.ScrimParticipant;
import ar.com.uade.pds.final_project.scrim.exception.ScrimException;
import ar.com.uade.pds.final_project.users.entity.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class Lobby implements ScrimState {

    public Lobby() {
        log.info("Scrim in Lobby state!");
    }

    @Override
    public void start(Scrim scrim) {
        throw new IllegalStateException("Scrim must be confirmed before starting");
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
        scrim.getConfirmedUsers().add(user.getId());
        boolean allConfirmed = scrim.getConfirmedUsers()
                .containsAll(scrim.getAllParticipants()
                        .stream()
                        .map(ScrimParticipant::getId)
                        .toList()
                );

        if (allConfirmed) {
            log.info("Todos los usuarios confirmaron. Scrim confirmado!");
            scrim.setState(new Confirmed(scrim));
            scrim.start();
        }
    }

    @Override
    public void end(Scrim scrim) {
        throw new IllegalStateException("Scrim didn't start yet");
    }
}