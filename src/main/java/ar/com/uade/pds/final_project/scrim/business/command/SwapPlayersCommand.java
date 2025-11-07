package ar.com.uade.pds.final_project.scrim.business.command;

import ar.com.uade.pds.final_project.notifications.event.DomainEvent;
import ar.com.uade.pds.final_project.notifications.event.EventType;
import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.scrim.entity.ScrimParticipant;
import ar.com.uade.pds.final_project.users.entity.Role;
import lombok.AllArgsConstructor;
import java.util.List;

@AllArgsConstructor
public class SwapPlayersCommand implements ScrimCommand {

    private final Scrim scrim;
    private final ScrimParticipant participantA;
    private final ScrimParticipant participantB;

    @Override
    public void execute() {
        Role roleA = participantA.getAssignedRole();
        Role roleB = participantB.getAssignedRole();

        // swap
        participantA.setAssignedRole(roleB);
        participantB.setAssignedRole(roleA);

        scrim.getDomainEvents().add(new DomainEvent(
                EventType.PLAYERS_SWAPPED,
                scrim.getIdCreator(),
                List.of(participantA.getUser().getId(), participantB.getUser().getId()),
                scrim.getId()
        ));
    }

    @Override
    public void undo() {
        execute();
    }
}


