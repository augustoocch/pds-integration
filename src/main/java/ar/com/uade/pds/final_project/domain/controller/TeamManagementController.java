package ar.com.uade.pds.final_project.domain.controller;

import ar.com.uade.pds.final_project.domain.dto.response.ResponseWrapper;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.scrim.exception.ScrimException;
import ar.com.uade.pds.final_project.scrim.service.TeamManagementService;
import ar.com.uade.pds.final_project.users.entity.Role;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import static ar.com.uade.pds.final_project.domain.controller.handler.ResponseHandler.buildResponse;
import static ar.com.uade.pds.final_project.domain.controller.handler.ResponseHandler.handleError;

@Controller
@AllArgsConstructor
public class TeamManagementController {

    private final TeamManagementService teamManagementService;

    public ResponseWrapper assignRole(Long scrimId, Long userId, Role newRole) {
        try {
            ValidationDTOResponse response = teamManagementService.assignRole(scrimId, userId, newRole);
            if (!response.isValid()) {
                return buildResponse("Role assignment failed", HttpStatus.BAD_REQUEST, false, response);
            }
            return buildResponse("Role assigned successfully", HttpStatus.OK, true, response);
        } catch (ScrimException e) {
            return buildResponse(e.getMessage(), HttpStatus.CONFLICT, false, null);
        } catch (Exception e) {
            return handleError(e);
        }
    }

    public ResponseWrapper swapPlayers(Long scrimId, Long userAId, Long userBId) {
        try {
            ValidationDTOResponse response = teamManagementService.swapPlayers(scrimId, userAId, userBId);
            if (!response.isValid()) {
                return buildResponse("Player swap failed", HttpStatus.BAD_REQUEST, false, response);
            }
            return buildResponse("Players swapped successfully", HttpStatus.OK, true, response);
        } catch (ScrimException e) {
            return buildResponse(e.getMessage(), HttpStatus.CONFLICT, false, null);
        } catch (Exception e) {
            return handleError(e);
        }
    }

    public ResponseWrapper undoLastAction() {
        try {
            ValidationDTOResponse response = teamManagementService.undoLastAction();
            return buildResponse("Last action undone", HttpStatus.OK, true, response);
        } catch (Exception e) {
            return handleError(e);
        }
    }
}