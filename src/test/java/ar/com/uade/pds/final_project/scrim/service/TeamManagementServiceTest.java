package ar.com.uade.pds.final_project.scrim.service;

import ar.com.uade.pds.final_project.domain.dto.request.RoleAssignmentRequest;
import ar.com.uade.pds.final_project.domain.dto.request.SwapRequest;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.scrim.business.command.ScrimCommandInvoker;
import ar.com.uade.pds.final_project.scrim.business.game.state.ScrimStateType;
import ar.com.uade.pds.final_project.scrim.constants.ErrorDescription;
import ar.com.uade.pds.final_project.scrim.constants.TeamName;
import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.scrim.entity.ScrimParticipant;
import ar.com.uade.pds.final_project.scrim.entity.Team;
import ar.com.uade.pds.final_project.scrim.exception.ScrimException;
import ar.com.uade.pds.final_project.scrim.repository.IScrimRepository;
import ar.com.uade.pds.final_project.scrim.service.impl.TeamManagementServiceImpl;
import ar.com.uade.pds.final_project.users.entity.Role;
import ar.com.uade.pds.final_project.users.entity.User;
import ar.com.uade.pds.final_project.users.service.DataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamManagementServiceTest {

    @Mock
    private IScrimRepository scrimRepository;

    @Mock
    private DataService dataService;

    @Mock
    private ScrimCommandInvoker invoker;

    @InjectMocks
    private TeamManagementServiceImpl teamManagementService;

    private User currentUser;
    private Scrim scrim;
    private Team teamAlpha;
    private Team teamBravo;
    private ScrimParticipant participant1;
    private ScrimParticipant participant2;

    @BeforeEach
    void setUp() {
        currentUser = new User.Builder()
                .id(1L)
                .username("creator")
                .email("creator@example.com")
                .build();

        scrim = new Scrim.Builder()
                .id(1L)
                .idCreator(1L)
                .build();

        teamAlpha = new Team.Builder()
                .id(1L)
                .name(TeamName.ALPHA)
                .scrim(scrim)
                .build();

        teamBravo = new Team.Builder()
                .id(2L)
                .name(TeamName.BRAVO)
                .scrim(scrim)
                .build();

        scrim.addTeams(List.of(teamAlpha, teamBravo));

        participant1 = new ScrimParticipant.Builder()
                .setId(1L)
                .setUser(currentUser)
                .setTeam(teamAlpha)
                .build();

        participant2 = new ScrimParticipant.Builder()
                .setId(2L)
                .setUser(new User.Builder().id(2L).build())
                .setTeam(teamBravo)
                .build();
    }

    @Test
    void testAssignRole_Success() {
        RoleAssignmentRequest request = new RoleAssignmentRequest(1L, 1L, "SNIPER");
        
        teamAlpha.setParticipants(List.of(participant1));
        scrim.setStateType(ScrimStateType.LOBBY);
        
        when(dataService.findUserWithToken()).thenReturn(currentUser);
        when(scrimRepository.findById(1L)).thenReturn(Optional.of(scrim));
        doNothing().when(invoker).executeCommand(any());
        when(scrimRepository.save(any(Scrim.class))).thenReturn(scrim);

        ValidationDTOResponse result = teamManagementService.assignRole(request);

        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("Role assigned successfully.", result.getData());
        verify(dataService, times(1)).findUserWithToken();
        verify(scrimRepository, times(1)).findById(1L);
        verify(invoker, times(1)).executeCommand(any());
        verify(scrimRepository, times(1)).save(scrim);
    }

    @Test
    void testAssignRole_ScrimNotFound() {
        RoleAssignmentRequest request = new RoleAssignmentRequest(1L, 1L, "SNIPER");
        
        when(dataService.findUserWithToken()).thenReturn(currentUser);
        when(scrimRepository.findById(1L)).thenReturn(Optional.empty());

        ScrimException exception = assertThrows(ScrimException.class,
                () -> teamManagementService.assignRole(request));

        assertEquals(ErrorDescription.SCRIM_NOT_FOUND.getDescription(), exception.getMessage());
        verify(scrimRepository, times(1)).findById(1L);
        verify(invoker, never()).executeCommand(any());
    }

    @Test
    void testSwapPlayers_Success() {
        SwapRequest request = new SwapRequest(1L, 1L, 2L);
        
        teamAlpha.setParticipants(List.of(participant1));
        teamBravo.setParticipants(List.of(participant2));
        scrim.setStateType(ScrimStateType.LOBBY);
        
        when(dataService.findUserWithToken()).thenReturn(currentUser);
        when(scrimRepository.findById(1L)).thenReturn(Optional.of(scrim));
        doNothing().when(invoker).executeCommand(any());
        when(scrimRepository.save(any(Scrim.class))).thenReturn(scrim);

        ValidationDTOResponse result = teamManagementService.swapPlayers(request);

        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("Players swapped successfully.", result.getData());
        verify(dataService, times(1)).findUserWithToken();
        verify(scrimRepository, times(1)).findById(1L);
        verify(invoker, times(1)).executeCommand(any());
        verify(scrimRepository, times(1)).save(scrim);
    }

    @Test
    void testSwapPlayers_ScrimNotFound() {
        SwapRequest request = new SwapRequest(1L, 1L, 2L);
        
        when(dataService.findUserWithToken()).thenReturn(currentUser);
        when(scrimRepository.findById(1L)).thenReturn(Optional.empty());

        ScrimException exception = assertThrows(ScrimException.class,
                () -> teamManagementService.swapPlayers(request));

        assertEquals(ErrorDescription.SCRIM_NOT_FOUND.getDescription(), exception.getMessage());
        verify(invoker, never()).executeCommand(any());
    }

    @Test
    void testUndoLastAction_Success() {
        doNothing().when(invoker).undoLastCommand();

        ValidationDTOResponse result = teamManagementService.undoLastAction();

        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("Last action undone.", result.getData());
        verify(invoker, times(1)).undoLastCommand();
    }

    @Test
    void testSelectTeam_AlphaHasFewer() {
        teamAlpha.setParticipants(new ArrayList<>());
        teamBravo.setParticipants(List.of(participant2));

        Team result = teamManagementService.selectTeam(scrim);

        assertEquals(TeamName.ALPHA, result.getName());
    }

    @Test
    void testSelectTeam_BravoHasFewer() {
        teamAlpha.setParticipants(List.of(participant1));
        teamBravo.setParticipants(new ArrayList<>());

        Team result = teamManagementService.selectTeam(scrim);

        assertEquals(TeamName.BRAVO, result.getName());
    }

    @Test
    void testSelectTeam_EqualCount() {
        teamAlpha.setParticipants(List.of(participant1));
        teamBravo.setParticipants(List.of(participant2));

        Team result = teamManagementService.selectTeam(scrim);

        assertNotNull(result);
        assertTrue(result.getName() == TeamName.ALPHA || result.getName() == TeamName.BRAVO);
    }

    @Test
    void testConstructTeams_Success() {
        List<Team> teams = teamManagementService.constructTeams(scrim);

        assertNotNull(teams);
        assertEquals(2, teams.size());
        assertEquals(TeamName.ALPHA, teams.get(0).getName());
        assertEquals(TeamName.BRAVO, teams.get(1).getName());
    }
}

