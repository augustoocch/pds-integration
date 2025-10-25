package ar.com.uade.pds.final_project.scrim.service;

import ar.com.uade.pds.final_project.domain.dto.request.JoinScrimRequest;
import ar.com.uade.pds.final_project.domain.dto.request.ScrimCreationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.SearchRequest;
import ar.com.uade.pds.final_project.domain.dto.response.ScrimDTO;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.scrim.business.game.state.ScrimStateType;
import ar.com.uade.pds.final_project.scrim.entity.Scrim;

import java.util.List;

public interface ScrimService {
    ValidationDTOResponse createScrim(ScrimCreationRequest request);
    ValidationDTOResponse endScrim(Long id);
    ValidationDTOResponse cancelScrim(Long id);
    ValidationDTOResponse confirmScrim(Long id);
    List<ScrimDTO> searchScrims(SearchRequest request);
    ValidationDTOResponse joinQueue(JoinScrimRequest request);
    List<ScrimDTO> searchAvailableScrims();
    List<Scrim> findAllByStateType(ScrimStateType stateType);
}
