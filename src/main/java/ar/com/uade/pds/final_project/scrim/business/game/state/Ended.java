package ar.com.uade.pds.final_project.scrim.business.game.state;

import ar.com.uade.pds.final_project.notifications.event.DomainEvent;
import ar.com.uade.pds.final_project.notifications.event.EventType;
import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.users.entity.User;

import java.util.List;

public class Ended implements ScrimState {

    public Ended(Scrim scrim) {
        List<Long> subscribersToNotify = scrim.getParticipants()
                .stream()
                .map(User::getId)
                .toList();

        scrim.addDomainEvent(new DomainEvent(
                EventType.SCRIM_ENDED,
                null,
                subscribersToNotify,
                scrim.getId()
        ));
    }

    @Override
    public void start(Scrim scrim) {
        throw new IllegalStateException("Scrim ended");
    }

    @Override
    public void cancel(Scrim scrim, Long userId) {
        throw new IllegalStateException("Scrim ended");
    }

    @Override
    public void confirm(Scrim scrim, User user) {
        throw new IllegalStateException("Scrim ended");
    }

    @Override
    public void end(Scrim scrim) {
        throw new IllegalStateException("Scrim ended");
    }
}
