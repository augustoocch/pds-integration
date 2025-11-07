package ar.com.uade.pds.final_project.scrim.service;

import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.scrim.entity.Team;
import ar.com.uade.pds.final_project.users.entity.Role;

import java.util.List;
import java.util.Set;

public interface TeamManagementService {
    ValidationDTOResponse assignRole(Long scrimId, Long userId, Role newRole);
    ValidationDTOResponse swapPlayers(Long scrimId, Long userAId, Long userBId);
    ValidationDTOResponse undoLastAction();
    List<Team> saveScrimTeams(List<Team> teams);
}
