package ar.com.uade.pds.final_project.domain.controller;

import ar.com.uade.pds.final_project.domain.dto.request.JoinScrimRequest;
import ar.com.uade.pds.final_project.domain.dto.request.ScrimCreationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.SearchRequest;
import ar.com.uade.pds.final_project.domain.dto.response.ResponseWrapper;
import ar.com.uade.pds.final_project.domain.dto.response.ScrimDTO;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.scrim.service.ScrimService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import java.util.List;

import static ar.com.uade.pds.final_project.domain.controller.handler.ResponseHandler.buildResponse;
import static ar.com.uade.pds.final_project.domain.controller.handler.ResponseHandler.handleError;

@Controller
@AllArgsConstructor
public class ScrimController {
    private final ScrimService scrimService;

    public ResponseWrapper createScrim(ScrimCreationRequest request) {
        try {
            ValidationDTOResponse response = scrimService.createScrim(request);
            if (!response.isValid()) {
                return buildResponse("Scrim creation failed", HttpStatus.CONFLICT, false, response);
            }
            return buildResponse("Scrim creation success", HttpStatus.OK, true, response);
        } catch (Exception e) {
            return handleError(e);
        }
    }

    public ResponseWrapper endScrim(Long id) {
        try {
            ValidationDTOResponse response = scrimService.endScrim(id);
            if (!response.isValid()) {
                return buildResponse("Ending scrim failed", HttpStatus.CONFLICT, false, response);
            }
            return buildResponse("Scrim ended successfully", HttpStatus.OK, true, response);
        } catch (Exception e) {
            return handleError(e);
        }
    }

    public ResponseWrapper cancelScrim(Long id) {
        try {
            ValidationDTOResponse response = scrimService.cancelScrim(id);
            if (!response.isValid()) {
                return buildResponse("Cancelling scrim failed", HttpStatus.CONFLICT, false, response);
            }
            return buildResponse("Scrim cancelled successfully", HttpStatus.OK, true, response);
        } catch (Exception e) {
            return handleError(e);
        }
    }

    public ResponseWrapper searchScrim(SearchRequest request) {
        try {
            List<ScrimDTO> response = scrimService.searchScrim(request);
            return buildResponse("Scrim search successful", HttpStatus.OK, true, response);
        } catch (Exception e) {
            return handleError(e);
        }
    }

    public ResponseWrapper confirmScrim(Long id) {
        try {
            ValidationDTOResponse response = scrimService.confirmScrim(id);
            if (!response.isValid()) {
                return buildResponse("Confirming scrim failed", HttpStatus.CONFLICT, false, response);
            }
            return buildResponse("Scrim confirmed successfully", HttpStatus.OK, true, response);
        } catch (Exception e) {
            return handleError(e);
        }
    }

    public ResponseWrapper joinQueue(JoinScrimRequest request) {
        try {
            ValidationDTOResponse response = scrimService.joinQueue(request);
            if (!response.isValid()) {
                return buildResponse("Joining queue failed", HttpStatus.CONFLICT, false, response);
            }
            return buildResponse("Joined queue successfully", HttpStatus.OK, true, response);
        } catch (Exception e) {
            return handleError(e);
        }
    }
}
