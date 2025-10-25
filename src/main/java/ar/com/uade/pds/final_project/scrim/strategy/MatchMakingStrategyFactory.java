package ar.com.uade.pds.final_project.scrim.strategy;

import ar.com.uade.pds.final_project.domain.dto.request.MatchmakingRequest;
import ar.com.uade.pds.final_project.scrim.service.ScrimService;
import ar.com.uade.pds.final_project.users.service.DataService;
import java.util.Map;

public class MatchMakingStrategyFactory {
    private static final String RANGE = "RANGE";
    private static final String LATENCY = "LATENCY";
    private static final String COMPATIBILITY = "COMPATIBILITY";
    private final Map<String, MatchMakingStrategy> strategies;

    public MatchMakingStrategyFactory(ScrimService scrimService,
                                      DataService dataService) {
        this.strategies = Map.of(
            RANGE, new RangeStrategy(scrimService, dataService),
            LATENCY, new LatencyStrategy(scrimService, dataService),
            COMPATIBILITY, new CompatibilityStrategy(scrimService, dataService)
        );
    }


    public MatchMakingStrategy getStrategy(MatchmakingRequest request) {
        return switch (request.getStrategy().toUpperCase()) {
            case RANGE -> strategies.get(RANGE);
            case LATENCY -> strategies.get(LATENCY);
            case COMPATIBILITY -> strategies.get(COMPATIBILITY);
            default -> throw new IllegalArgumentException("Invalid strategy: " + request.getStrategy());
        };
    }
}
