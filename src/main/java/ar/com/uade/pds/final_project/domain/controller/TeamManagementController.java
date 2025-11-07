package ar.com.uade.pds.final_project.domain.controller;

import ar.com.uade.pds.final_project.domain.dto.request.RoleAssignmentRequest;
import ar.com.uade.pds.final_project.domain.dto.request.SwapRequest;
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

    public ResponseWrapper assignRole(RoleAssignmentRequest request) {
        try {
            ValidationDTOResponse response = teamManagementService.assignRole(request);
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

    public ResponseWrapper swapPlayers(SwapRequest request) {
        try {
            ValidationDTOResponse response = teamManagementService.swapPlayers(request);
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