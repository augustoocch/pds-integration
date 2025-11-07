package ar.com.uade.pds.final_project.scrim.service.impl;

import static ar.com.uade.pds.final_project.scrim.constants.ErrorDescription.*;

import ar.com.uade.pds.final_project.domain.dto.request.RoleAssignmentRequest;
import ar.com.uade.pds.final_project.domain.dto.request.SwapRequest;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.scrim.business.command.AssignRoleCommand;
import ar.com.uade.pds.final_project.scrim.business.command.ScrimCommandInvoker;
import ar.com.uade.pds.final_project.scrim.business.command.SwapPlayersCommand;
import ar.com.uade.pds.final_project.scrim.constants.TeamName;
import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.scrim.entity.ScrimParticipant;
import ar.com.uade.pds.final_project.scrim.entity.Team;
import ar.com.uade.pds.final_project.scrim.exception.ScrimException;
import ar.com.uade.pds.final_project.scrim.repository.IScrimRepository;
import ar.com.uade.pds.final_project.scrim.service.TeamManagementService;
import ar.com.uade.pds.final_project.users.entity.Role;
import ar.com.uade.pds.final_project.users.entity.User;
import ar.com.uade.pds.final_project.users.service.DataService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
public class TeamManagementServiceImpl implements TeamManagementService {

    private final IScrimRepository scrimRepository;
    private final DataService dataService;
    private final ScrimCommandInvoker invoker;

    public ValidationDTOResponse assignRole(RoleAssignmentRequest request) {
        User currentUser = dataService.findUserWithToken();
        Scrim scrim = scrimRepository.findById(request.scrimId())
                .orElseThrow(() -> new ScrimException(SCRIM_NOT_FOUND.getDescription()));

        if(!scrim.validStateToSwitch()) {
            throw new ScrimException(INVALID_SCRIM_STATE.getDescription());
        }

        if (!scrim.getIdCreator().equals(currentUser.getId())) {
            throw new ScrimException(MUST_BE_ORGANIZER_TO_ASSIGN.getDescription());
        }

        ScrimParticipant participant = scrim.findParticipantByUserId(request.userId())
                .orElseThrow(() -> new ScrimException(PLAYER_NOT_IN_SCRIM.getDescription()));

        Role role = Role.fromString(request.newRole());
        invoker.executeCommand(new AssignRoleCommand(scrim, participant, role));
        scrimRepository.save(scrim);

        return new ValidationDTOResponse(true, "Role assigned successfully.");
    }

    @Override
    public ValidationDTOResponse swapPlayers(SwapRequest request) {
        User currentUser = dataService.findUserWithToken();
        Scrim scrim = scrimRepository.findById(request.scrimId())
                .orElseThrow(() -> new ScrimException(SCRIM_NOT_FOUND.getDescription()));

        if(!scrim.validStateToSwitch()) {
            throw new ScrimException(INVALID_SCRIM_STATE.getDescription());
        }

        if (!scrim.getIdCreator().equals(currentUser.getId())) {
            throw new ScrimException(MUST_BE_ORGANIZER_TO_SWAP.getDescription());
        }

        ScrimParticipant participantA = scrim.findParticipantByUserId(request.userAId())
                .orElseThrow(() -> new ScrimException(PLAYER_A_NOT_IN_SCRIM.getDescription()));
        ScrimParticipant participantB = scrim.findParticipantByUserId(request.userBId())
                .orElseThrow(() -> new ScrimException(PLAYER_B_NOT_IN_SCRIM.getDescription()));

        invoker.executeCommand(new SwapPlayersCommand(scrim, participantA, participantB));
        scrimRepository.save(scrim);

        return new ValidationDTOResponse(true, "Players swapped successfully.");
    }

    @Override
    public ValidationDTOResponse undoLastAction() {
        invoker.undoLastCommand();
        return new ValidationDTOResponse(true, "Last action undone.");
    }

    /**
     * Selecciona un equipo de manera balanceada para un nuevo participante en el scrim.
     *
     * @param scrim El scrim al que se unirá el participante.
     * @return El equipo asignado (Team.A o Team.B).
     */
    @Override
    public Team selectTeam(Scrim scrim) {
        // Obtenemos los equipos
        Optional<Team> teamAlphaOpt = scrim.getTeams().stream()
                .filter(t -> t.getName() == TeamName.ALPHA)
                .findFirst();

        Optional<Team> teamBravoOpt = scrim.getTeams().stream()
                .filter(t -> t.getName() == TeamName.BRAVO)
                .findFirst();

        if (teamAlphaOpt.isEmpty() || teamBravoOpt.isEmpty()) {
            throw new IllegalStateException("Los equipos ALPHA y BRAVO no fueron inicializados en el scrim.");
        }

        Team teamAlpha = teamAlphaOpt.get();
        Team teamBravo = teamBravoOpt.get();

        long countAlpha = teamAlpha.getParticipants().size();
        long countBravo = teamBravo.getParticipants().size();

        Team assignedTeam;
        if (countAlpha < countBravo) {
            assignedTeam = teamAlpha;
        } else if (countBravo < countAlpha) {
            assignedTeam = teamBravo;
        } else {
            assignedTeam = Math.random() < 0.5 ? teamAlpha : teamBravo;
        }

        return assignedTeam;
    }

    @Override
    public List<Team> constructTeams(Scrim scrim) {
        Team alpha = new Team.Builder()
                .name(TeamName.ALPHA)
                .scrim(scrim)
                .build();

        Team bravo = new Team.Builder()
                .name(TeamName.BRAVO)
                .scrim(scrim)
                .build();

        List<Team> teams = new ArrayList<>();
        teams.add(alpha);
        teams.add(bravo);
        return teams;
    }
}
