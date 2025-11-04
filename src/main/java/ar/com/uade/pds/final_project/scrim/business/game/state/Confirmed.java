package ar.com.uade.pds.final_project.scrim.business.game.state;

import ar.com.uade.pds.final_project.notifications.event.DomainEvent;
import ar.com.uade.pds.final_project.notifications.event.EventType;
import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.scrim.exception.ScrimException;
import ar.com.uade.pds.final_project.users.entity.User;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
public class Confirmed implements ScrimState {

    public Confirmed(Scrim scrim) {
        List<Long> subscribersToNotify = scrim.getParticipants()
                .stream()
                .map(User::getId)
                .toList();

        scrim.addDomainEvent(new DomainEvent(
                EventType.SCRIM_CONFIRMED,
                null,
                subscribersToNotify,
                scrim.getId()
        ));
        this.start(scrim);
    }

    @Override
    public void start(Scrim scrim) {
        scrim.setState(new InGame());
        log.info("Scrim empezando!");
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
        throw new IllegalStateException("Scrim already confirmed");
    }

    @Override
    public void end(Scrim scrim) {
        throw new IllegalStateException("Scrim didn't start yet");
    }
}
