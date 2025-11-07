package ar.com.uade.pds.final_project.scrim.business.command;


import ar.com.uade.pds.final_project.notifications.event.DomainEvent;
import ar.com.uade.pds.final_project.notifications.event.EventType;
import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.scrim.entity.ScrimParticipant;
import ar.com.uade.pds.final_project.users.entity.Role;
import java.util.List;

public class AssignRoleCommand implements ScrimCommand {

    private final Scrim scrim;
    private final ScrimParticipant participant;
    private final Role newRole;
    private Role previousRole;

    public AssignRoleCommand(Scrim scrim, ScrimParticipant participant, Role newRole) {
        this.scrim = scrim;
        this.participant = participant;
        this.newRole = newRole;
    }

    @Override
    public void execute() {
        previousRole = participant.getAssignedRole();

        participant.setAssignedRole(newRole);

        scrim.getDomainEvents().add(new DomainEvent(
                EventType.ROLE_ASSIGNED,
                scrim.getIdCreator(),
                List.of(participant.getUser().getId()),
                scrim.getId()
        ));
    }

    @Override
    public void undo() {
        participant.setAssignedRole(previousRole);
    }
}

