package ar.com.uade.pds.final_project.domain.controller;

import ar.com.uade.pds.final_project.domain.dto.request.JoinScrimRequest;
import ar.com.uade.pds.final_project.domain.dto.request.MatchmakingRequest;
import ar.com.uade.pds.final_project.domain.dto.request.ScrimCreationRequest;
import ar.com.uade.pds.final_project.domain.dto.response.ResponseWrapper;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.scrim.service.MatchMakingService;
import ar.com.uade.pds.final_project.scrim.service.ScrimService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import static ar.com.uade.pds.final_project.domain.controller.handler.ResponseHandler.buildResponse;
import static ar.com.uade.pds.final_project.domain.controller.handler.ResponseHandler.handleError;

@Controller
@AllArgsConstructor
public class MatchmakingController {
    private final MatchMakingService matchMakingService;

    public ResponseWrapper joinMatchmakingScrim(MatchmakingRequest request) {
        try {
            ValidationDTOResponse response = matchMakingService.joinScrim(request);
            if (!response.isValid()) {
                return buildResponse("Joining failed", HttpStatus.CONFLICT, false, response);
            }
            return buildResponse("Joining success", HttpStatus.OK, true, response);
        } catch (Exception e) {
            return handleError(e);
        }
    }
}
