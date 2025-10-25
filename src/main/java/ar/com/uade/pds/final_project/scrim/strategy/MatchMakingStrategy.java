package ar.com.uade.pds.final_project.scrim.strategy;

import ar.com.uade.pds.final_project.domain.dto.request.MatchmakingRequest;

public interface MatchMakingStrategy {
    void execute(MatchmakingRequest request);
}
