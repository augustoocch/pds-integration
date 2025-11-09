package ar.com.uade.pds.final_project.domain.controller;

import ar.com.uade.pds.final_project.domain.dto.request.JoinScrimRequest;
import ar.com.uade.pds.final_project.domain.dto.request.ScrimCreationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.SearchRequest;
import ar.com.uade.pds.final_project.domain.dto.response.ResponseWrapper;
import ar.com.uade.pds.final_project.domain.dto.response.ScrimDTO;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.scrim.service.ScrimService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScrimControllerTest {

    @Mock
    private ScrimService scrimService;

    @InjectMocks
    private ScrimController scrimController;

    private ScrimCreationRequest scrimCreationRequest;
    private SearchRequest searchRequest;
    private JoinScrimRequest joinScrimRequest;
    private Long scrimId;

    @BeforeEach
    void setUp() {
        scrimCreationRequest = new ScrimCreationRequest();
        scrimCreationRequest.setGame("Game1");
        scrimCreationRequest.setFormat("5v5");
        scrimCreationRequest.setMode("Ranked");

        searchRequest = new SearchRequest("Game1", "NA", "5v5");
        joinScrimRequest = new JoinScrimRequest(1L);
        scrimId = 1L;
    }

    @Test
    void testCreateScrim_Success() {
        ValidationDTOResponse response = new ValidationDTOResponse(true, "Scrim created successfully");
        when(scrimService.createScrim(any(ScrimCreationRequest.class))).thenReturn(response);

        ResponseWrapper result = scrimController.createScrim(scrimCreationRequest);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatus());
        assertEquals("Scrim creation success", result.getMessage());
        verify(scrimService, times(1)).createScrim(scrimCreationRequest);
    }

    @Test
    void testCreateScrim_Failure() {
        ValidationDTOResponse response = new ValidationDTOResponse(false, "Invalid scrim data");
        when(scrimService.createScrim(any(ScrimCreationRequest.class))).thenReturn(response);

        ResponseWrapper result = scrimController.createScrim(scrimCreationRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(409, result.getStatus());
        assertEquals("Scrim creation failed", result.getMessage());
        verify(scrimService, times(1)).createScrim(scrimCreationRequest);
    }

    @Test
    void testCreateScrim_Exception() {
        when(scrimService.createScrim(any(ScrimCreationRequest.class)))
                .thenThrow(new RuntimeException("Database error"));

        ResponseWrapper result = scrimController.createScrim(scrimCreationRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(500, result.getStatus());
        verify(scrimService, times(1)).createScrim(scrimCreationRequest);
    }

    @Test
    void testEndScrim_Success() {
        ValidationDTOResponse response = new ValidationDTOResponse(true, "Scrim ended");
        when(scrimService.endScrim(scrimId)).thenReturn(response);

        ResponseWrapper result = scrimController.endScrim(scrimId);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatus());
        assertEquals("Scrim ended successfully", result.getMessage());
        verify(scrimService, times(1)).endScrim(scrimId);
    }

    @Test
    void testEndScrim_Failure() {
        ValidationDTOResponse response = new ValidationDTOResponse(false, "Cannot end scrim");
        when(scrimService.endScrim(scrimId)).thenReturn(response);

        ResponseWrapper result = scrimController.endScrim(scrimId);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(409, result.getStatus());
        assertEquals("Ending scrim failed", result.getMessage());
        verify(scrimService, times(1)).endScrim(scrimId);
    }

    @Test
    void testEndScrim_Exception() {
        when(scrimService.endScrim(scrimId))
                .thenThrow(new RuntimeException("Error ending scrim"));

        ResponseWrapper result = scrimController.endScrim(scrimId);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(500, result.getStatus());
        verify(scrimService, times(1)).endScrim(scrimId);
    }

    @Test
    void testCancelScrim_Success() {
        ValidationDTOResponse response = new ValidationDTOResponse(true, "Scrim cancelled");
        when(scrimService.cancelScrim(scrimId)).thenReturn(response);

        ResponseWrapper result = scrimController.cancelScrim(scrimId);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatus());
        assertEquals("Scrim cancelled successfully", result.getMessage());
        verify(scrimService, times(1)).cancelScrim(scrimId);
    }

    @Test
    void testCancelScrim_Failure() {
        ValidationDTOResponse response = new ValidationDTOResponse(false, "Cannot cancel scrim");
        when(scrimService.cancelScrim(scrimId)).thenReturn(response);

        ResponseWrapper result = scrimController.cancelScrim(scrimId);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(409, result.getStatus());
        assertEquals("Cancelling scrim failed", result.getMessage());
        verify(scrimService, times(1)).cancelScrim(scrimId);
    }

    @Test
    void testCancelScrim_Exception() {
        when(scrimService.cancelScrim(scrimId))
                .thenThrow(new RuntimeException("Error cancelling scrim"));

        ResponseWrapper result = scrimController.cancelScrim(scrimId);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(500, result.getStatus());
        verify(scrimService, times(1)).cancelScrim(scrimId);
    }

    @Test
    void testSearchScrim_Success() {
        List<ScrimDTO> scrims = new ArrayList<>();
        ScrimDTO scrim1 = new ScrimDTO.Builder()
                .id(1L)
                .game("Game1")
                .format("5v5")
                .players(10)
                .region("NA")
                .latency(100)
                .estDuration(30)
                .mode("Ranked")
                .state("SEARCHING")
                .roles(List.of("SNIPER"))
                .build();
        scrims.add(scrim1);
        when(scrimService.searchScrims(any(SearchRequest.class))).thenReturn(scrims);

        ResponseWrapper result = scrimController.searchScrim(searchRequest);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatus());
        assertEquals("Scrim search successful", result.getMessage());
        assertNotNull(result.getData());
        verify(scrimService, times(1)).searchScrims(searchRequest);
    }

    @Test
    void testSearchScrim_EmptyList() {
        when(scrimService.searchScrims(any(SearchRequest.class))).thenReturn(new ArrayList<>());

        ResponseWrapper result = scrimController.searchScrim(searchRequest);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatus());
        assertEquals("Scrim search successful", result.getMessage());
        verify(scrimService, times(1)).searchScrims(searchRequest);
    }

    @Test
    void testSearchScrim_Exception() {
        when(scrimService.searchScrims(any(SearchRequest.class)))
                .thenThrow(new RuntimeException("Search error"));

        ResponseWrapper result = scrimController.searchScrim(searchRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(500, result.getStatus());
        verify(scrimService, times(1)).searchScrims(searchRequest);
    }

    @Test
    void testConfirmScrim_Success() {
        ValidationDTOResponse response = new ValidationDTOResponse(true, "Scrim confirmed");
        when(scrimService.confirmScrim(scrimId)).thenReturn(response);

        ResponseWrapper result = scrimController.confirmScrim(scrimId);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatus());
        assertEquals("Scrim confirmed successfully", result.getMessage());
        verify(scrimService, times(1)).confirmScrim(scrimId);
    }

    @Test
    void testConfirmScrim_Failure() {
        ValidationDTOResponse response = new ValidationDTOResponse(false, "Cannot confirm scrim");
        when(scrimService.confirmScrim(scrimId)).thenReturn(response);

        ResponseWrapper result = scrimController.confirmScrim(scrimId);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(409, result.getStatus());
        assertEquals("Confirming scrim failed", result.getMessage());
        verify(scrimService, times(1)).confirmScrim(scrimId);
    }

    @Test
    void testConfirmScrim_Exception() {
        when(scrimService.confirmScrim(scrimId))
                .thenThrow(new RuntimeException("Error confirming scrim"));

        ResponseWrapper result = scrimController.confirmScrim(scrimId);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(500, result.getStatus());
        verify(scrimService, times(1)).confirmScrim(scrimId);
    }

    @Test
    void testJoinQueue_Success() {
        ValidationDTOResponse response = new ValidationDTOResponse(true, "Joined queue");
        when(scrimService.joinQueue(any(JoinScrimRequest.class))).thenReturn(response);

        ResponseWrapper result = scrimController.joinQueue(joinScrimRequest);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatus());
        assertEquals("Joined queue successfully", result.getMessage());
        verify(scrimService, times(1)).joinQueue(joinScrimRequest);
    }

    @Test
    void testJoinQueue_Failure() {
        ValidationDTOResponse response = new ValidationDTOResponse(false, "Cannot join queue");
        when(scrimService.joinQueue(any(JoinScrimRequest.class))).thenReturn(response);

        ResponseWrapper result = scrimController.joinQueue(joinScrimRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(409, result.getStatus());
        assertEquals("Joining queue failed", result.getMessage());
        verify(scrimService, times(1)).joinQueue(joinScrimRequest);
    }

    @Test
    void testJoinQueue_Exception() {
        when(scrimService.joinQueue(any(JoinScrimRequest.class)))
                .thenThrow(new RuntimeException("Error joining queue"));

        ResponseWrapper result = scrimController.joinQueue(joinScrimRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(500, result.getStatus());
        verify(scrimService, times(1)).joinQueue(joinScrimRequest);
    }
}

