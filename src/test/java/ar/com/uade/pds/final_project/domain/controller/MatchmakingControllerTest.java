package ar.com.uade.pds.final_project.domain.controller;

import ar.com.uade.pds.final_project.domain.dto.request.MatchmakingRequest;
import ar.com.uade.pds.final_project.domain.dto.response.ResponseWrapper;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.scrim.service.MatchMakingService;
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
class MatchmakingControllerTest {

    @Mock
    private MatchMakingService matchMakingService;

    @InjectMocks
    private MatchmakingController matchmakingController;

    private MatchmakingRequest matchmakingRequest;

    @BeforeEach
    void setUp() {
        matchmakingRequest = new MatchmakingRequest("RANGE");
    }

    @Test
    void testJoinMatchmakingScrim_Success() {
        ValidationDTOResponse response = new ValidationDTOResponse(true, "Joined matchmaking");
        when(matchMakingService.joinScrim(any(MatchmakingRequest.class))).thenReturn(response);

        ResponseWrapper result = matchmakingController.joinMatchmakingScrim(matchmakingRequest);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatus());
        assertEquals("Joining success", result.getMessage());
        verify(matchMakingService, times(1)).joinScrim(matchmakingRequest);
    }

    @Test
    void testJoinMatchmakingScrim_Failure() {
        ValidationDTOResponse response = new ValidationDTOResponse(false, "Cannot join matchmaking");
        when(matchMakingService.joinScrim(any(MatchmakingRequest.class))).thenReturn(response);

        ResponseWrapper result = matchmakingController.joinMatchmakingScrim(matchmakingRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(409, result.getStatus());
        assertEquals("Joining failed", result.getMessage());
        verify(matchMakingService, times(1)).joinScrim(matchmakingRequest);
    }

    @Test
    void testJoinMatchmakingScrim_Exception() {
        when(matchMakingService.joinScrim(any(MatchmakingRequest.class)))
                .thenThrow(new RuntimeException("Matchmaking error"));

        ResponseWrapper result = matchmakingController.joinMatchmakingScrim(matchmakingRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(500, result.getStatus());
        verify(matchMakingService, times(1)).joinScrim(matchmakingRequest);
    }
}

