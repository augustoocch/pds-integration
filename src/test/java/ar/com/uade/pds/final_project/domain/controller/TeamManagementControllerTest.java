package ar.com.uade.pds.final_project.domain.controller;

import ar.com.uade.pds.final_project.domain.dto.request.RoleAssignmentRequest;
import ar.com.uade.pds.final_project.domain.dto.request.SwapRequest;
import ar.com.uade.pds.final_project.domain.dto.response.ResponseWrapper;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.scrim.exception.ScrimException;
import ar.com.uade.pds.final_project.scrim.service.TeamManagementService;
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
class TeamManagementControllerTest {

    @Mock
    private TeamManagementService teamManagementService;

    @InjectMocks
    private TeamManagementController teamManagementController;

    private RoleAssignmentRequest roleAssignmentRequest;
    private SwapRequest swapRequest;

    @BeforeEach
    void setUp() {
        roleAssignmentRequest = new RoleAssignmentRequest(1L, 1L, "SNIPER");
        swapRequest = new SwapRequest(1L, 1L, 2L);
    }

    @Test
    void testAssignRole_Success() {
        ValidationDTOResponse response = new ValidationDTOResponse(true, "Role assigned");
        when(teamManagementService.assignRole(any(RoleAssignmentRequest.class))).thenReturn(response);

        ResponseWrapper result = teamManagementController.assignRole(roleAssignmentRequest);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatus());
        assertEquals("Role assigned successfully", result.getMessage());
        verify(teamManagementService, times(1)).assignRole(roleAssignmentRequest);
    }

    @Test
    void testAssignRole_Failure() {
        ValidationDTOResponse response = new ValidationDTOResponse(false, "Cannot assign role");
        when(teamManagementService.assignRole(any(RoleAssignmentRequest.class))).thenReturn(response);

        ResponseWrapper result = teamManagementController.assignRole(roleAssignmentRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(400, result.getStatus());
        assertEquals("Role assignment failed", result.getMessage());
        verify(teamManagementService, times(1)).assignRole(roleAssignmentRequest);
    }

    @Test
    void testAssignRole_ScrimException() {
        when(teamManagementService.assignRole(any(RoleAssignmentRequest.class)))
                .thenThrow(new ScrimException("Scrim not found"));

        ResponseWrapper result = teamManagementController.assignRole(roleAssignmentRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(409, result.getStatus());
        assertEquals("Scrim not found", result.getMessage());
        verify(teamManagementService, times(1)).assignRole(roleAssignmentRequest);
    }

    @Test
    void testAssignRole_GenericException() {
        when(teamManagementService.assignRole(any(RoleAssignmentRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseWrapper result = teamManagementController.assignRole(roleAssignmentRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(500, result.getStatus());
        verify(teamManagementService, times(1)).assignRole(roleAssignmentRequest);
    }

    @Test
    void testSwapPlayers_Success() {
        ValidationDTOResponse response = new ValidationDTOResponse(true, "Players swapped");
        when(teamManagementService.swapPlayers(any(SwapRequest.class))).thenReturn(response);

        ResponseWrapper result = teamManagementController.swapPlayers(swapRequest);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatus());
        assertEquals("Players swapped successfully", result.getMessage());
        verify(teamManagementService, times(1)).swapPlayers(swapRequest);
    }

    @Test
    void testSwapPlayers_Failure() {
        ValidationDTOResponse response = new ValidationDTOResponse(false, "Cannot swap players");
        when(teamManagementService.swapPlayers(any(SwapRequest.class))).thenReturn(response);

        ResponseWrapper result = teamManagementController.swapPlayers(swapRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(400, result.getStatus());
        assertEquals("Player swap failed", result.getMessage());
        verify(teamManagementService, times(1)).swapPlayers(swapRequest);
    }

    @Test
    void testSwapPlayers_ScrimException() {
        when(teamManagementService.swapPlayers(any(SwapRequest.class)))
                .thenThrow(new ScrimException("Invalid swap operation"));

        ResponseWrapper result = teamManagementController.swapPlayers(swapRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(409, result.getStatus());
        assertEquals("Invalid swap operation", result.getMessage());
        verify(teamManagementService, times(1)).swapPlayers(swapRequest);
    }

    @Test
    void testSwapPlayers_GenericException() {
        when(teamManagementService.swapPlayers(any(SwapRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseWrapper result = teamManagementController.swapPlayers(swapRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(500, result.getStatus());
        verify(teamManagementService, times(1)).swapPlayers(swapRequest);
    }

    @Test
    void testUndoLastAction_Success() {
        ValidationDTOResponse response = new ValidationDTOResponse(true, "Action undone");
        when(teamManagementService.undoLastAction()).thenReturn(response);

        ResponseWrapper result = teamManagementController.undoLastAction();

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatus());
        assertEquals("Last action undone", result.getMessage());
        verify(teamManagementService, times(1)).undoLastAction();
    }

    @Test
    void testUndoLastAction_Exception() {
        when(teamManagementService.undoLastAction())
                .thenThrow(new RuntimeException("Cannot undo action"));

        ResponseWrapper result = teamManagementController.undoLastAction();

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(500, result.getStatus());
        verify(teamManagementService, times(1)).undoLastAction();
    }
}

