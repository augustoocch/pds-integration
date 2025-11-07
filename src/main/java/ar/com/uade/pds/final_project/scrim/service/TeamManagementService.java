package ar.com.uade.pds.final_project.scrim.service;

import ar.com.uade.pds.final_project.domain.dto.request.RoleAssignmentRequest;
import ar.com.uade.pds.final_project.domain.dto.request.SwapRequest;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.scrim.entity.Team;

import java.util.List;

public interface TeamManagementService {
    ValidationDTOResponse assignRole(RoleAssignmentRequest request);
    ValidationDTOResponse swapPlayers(SwapRequest request);
    ValidationDTOResponse undoLastAction();
    Team selectTeam(Scrim scrim);
    List<Team> constructTeams(Scrim scrim);
}
