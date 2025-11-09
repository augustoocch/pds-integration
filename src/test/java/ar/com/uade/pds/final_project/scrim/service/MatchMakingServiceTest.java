package ar.com.uade.pds.final_project.scrim.service;

import ar.com.uade.pds.final_project.domain.dto.request.MatchmakingRequest;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.scrim.service.impl.MatchMakingServiceImpl;
import ar.com.uade.pds.final_project.scrim.strategy.MatchMakingStrategy;
import ar.com.uade.pds.final_project.scrim.strategy.MatchMakingStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchMakingServiceTest {

    @Mock
    private MatchMakingStrategyFactory strategyFactory;

    @Mock
    private MatchMakingStrategy strategy;

    @InjectMocks
    private MatchMakingServiceImpl matchMakingService;

    private MatchmakingRequest matchmakingRequest;

    @BeforeEach
    void setUp() {
        matchmakingRequest = new MatchmakingRequest("RANGE");
    }

    @Test
    void testJoinScrim_Success() {
        when(strategyFactory.getStrategy(any(MatchmakingRequest.class))).thenReturn(strategy);
        doNothing().when(strategy).execute(any(MatchmakingRequest.class));

        ValidationDTOResponse result = matchMakingService.joinScrim(matchmakingRequest);

        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("Player successfully joined the scrim.", result.getData());
        verify(strategyFactory, times(1)).getStrategy(matchmakingRequest);
        verify(strategy, times(1)).execute(matchmakingRequest);
    }

    @Test
    void testJoinScrim_StrategyExecution() {
        when(strategyFactory.getStrategy(any(MatchmakingRequest.class))).thenReturn(strategy);
        doNothing().when(strategy).execute(any(MatchmakingRequest.class));

        matchMakingService.joinScrim(matchmakingRequest);

        verify(strategyFactory, times(1)).getStrategy(matchmakingRequest);
        verify(strategy, times(1)).execute(matchmakingRequest);
    }
}

