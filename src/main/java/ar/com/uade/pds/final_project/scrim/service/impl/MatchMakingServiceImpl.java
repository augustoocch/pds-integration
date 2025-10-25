package ar.com.uade.pds.final_project.scrim.service.impl;

import ar.com.uade.pds.final_project.domain.dto.request.MatchmakingRequest;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.scrim.service.MatchMakingService;
import ar.com.uade.pds.final_project.scrim.strategy.MatchMakingStrategy;
import ar.com.uade.pds.final_project.scrim.strategy.MatchMakingStrategyFactory;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MatchMakingServiceImpl implements MatchMakingService {

    private final MatchMakingStrategyFactory strategyFactory;

    @Override
    public ValidationDTOResponse joinScrim(MatchmakingRequest request) {
        MatchMakingStrategy strategy = strategyFactory.getStrategy(request);
        strategy.execute(request);
        return new ValidationDTOResponse(true, "Player successfully joined the scrim.");
    }
}
