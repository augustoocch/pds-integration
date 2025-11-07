package ar.com.uade.pds.final_project.scrim.service;

import ar.com.uade.pds.final_project.domain.dto.request.MatchmakingRequest;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;

public interface MatchMakingService {
    ValidationDTOResponse joinScrim(MatchmakingRequest request);
}
