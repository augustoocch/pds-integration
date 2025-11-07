package ar.com.uade.pds.final_project.scrim.service.impl;

import static ar.com.uade.pds.final_project.scrim.constants.ErrorDescription.*;

import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.scrim.business.command.AssignRoleCommand;
import ar.com.uade.pds.final_project.scrim.business.command.ScrimCommandInvoker;
import ar.com.uade.pds.final_project.scrim.business.command.SwapPlayersCommand;
import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.scrim.entity.ScrimParticipant;
import ar.com.uade.pds.final_project.scrim.entity.Team;
import ar.com.uade.pds.final_project.scrim.exception.ScrimException;
import ar.com.uade.pds.final_project.scrim.repository.IScrimRepository;
import ar.com.uade.pds.final_project.scrim.repository.ITeamRepository;
import ar.com.uade.pds.final_project.scrim.service.TeamManagementService;
import ar.com.uade.pds.final_project.users.entity.Role;
import ar.com.uade.pds.final_project.users.entity.User;
import ar.com.uade.pds.final_project.users.service.DataService;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RequiredArgsConstructor
public class TeamManagementServiceImpl implements TeamManagementService {

    private final ITeamRepository teamRepository;
    private final IScrimRepository scrimRepository;
    private final DataService dataService;
    private final ScrimCommandInvoker invoker;

    public ValidationDTOResponse assignRole(Long scrimId, Long userId, Role newRole) {
        User currentUser = dataService.findUserWithToken();
        Scrim scrim = scrimRepository.findById(scrimId)
                .orElseThrow(() -> new ScrimException(SCRIM_NOT_FOUND.getDescription()));

        if(!scrim.validStateToSwitch()) {
            throw new ScrimException(INVALID_SCRIM_STATE.getDescription());
        }

        if (!scrim.getIdCreator().equals(currentUser.getId())) {
            throw new ScrimException(MUST_BE_ORGANIZER_TO_ASSIGN.getDescription());
        }

        ScrimParticipant participant = scrim.findParticipantByUserId(userId)
                .orElseThrow(() -> new ScrimException(PLAYER_NOT_IN_SCRIM.getDescription()));

        invoker.executeCommand(new AssignRoleCommand(scrim, participant, newRole));
        scrimRepository.save(scrim);

        return new ValidationDTOResponse(true, "Role assigned successfully.");
    }

    public ValidationDTOResponse swapPlayers(Long scrimId, Long userAId, Long userBId) {
        User currentUser = dataService.findUserWithToken();
        Scrim scrim = scrimRepository.findById(scrimId)
                .orElseThrow(() -> new ScrimException(SCRIM_NOT_FOUND.getDescription()));

        if(!scrim.validStateToSwitch()) {
            throw new ScrimException(INVALID_SCRIM_STATE.getDescription());
        }

        if (!scrim.getIdCreator().equals(currentUser.getId())) {
            throw new ScrimException(MUST_BE_ORGANIZER_TO_SWAP.getDescription());
        }

        ScrimParticipant participantA = scrim.findParticipantByUserId(userAId)
                .orElseThrow(() -> new ScrimException(PLAYER_A_NOT_IN_SCRIM.getDescription()));
        ScrimParticipant participantB = scrim.findParticipantByUserId(userBId)
                .orElseThrow(() -> new ScrimException(PLAYER_B_NOT_IN_SCRIM.getDescription()));

        invoker.executeCommand(new SwapPlayersCommand(scrim, participantA, participantB));
        scrimRepository.save(scrim);

        return new ValidationDTOResponse(true, "Players swapped successfully.");
    }

    public ValidationDTOResponse undoLastAction() {
        invoker.undoLastCommand();
        return new ValidationDTOResponse(true, "Last action undone.");
    }

    public List<Team> saveScrimTeams(List<Team> teams) {
        return teamRepository.saveAll(teams);
    }

    public Team saveTeam(Team team) {
        return teamRepository.save(team);
    }
}
