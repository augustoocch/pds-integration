package ar.com.uade.pds.final_project.scrim.service;

import ar.com.uade.pds.final_project.domain.dto.request.JoinScrimRequest;
import ar.com.uade.pds.final_project.domain.dto.request.ScrimCreationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.SearchRequest;
import ar.com.uade.pds.final_project.domain.dto.response.ScrimDTO;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.notifications.event.DomainEvent;
import ar.com.uade.pds.final_project.notifications.service.NotificationService;
import ar.com.uade.pds.final_project.scrim.business.game.state.InGame;
import ar.com.uade.pds.final_project.scrim.business.game.state.Lobby;
import ar.com.uade.pds.final_project.scrim.business.game.state.ScrimStateType;
import ar.com.uade.pds.final_project.scrim.business.game.state.Searching;
import ar.com.uade.pds.final_project.scrim.constants.ErrorDescription;
import ar.com.uade.pds.final_project.scrim.entity.PlayerStats;
import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.scrim.entity.ScrimParticipant;
import ar.com.uade.pds.final_project.scrim.entity.Team;
import ar.com.uade.pds.final_project.scrim.exception.ScrimException;
import ar.com.uade.pds.final_project.scrim.repository.IScrimRepository;
import ar.com.uade.pds.final_project.scrim.service.impl.ScrimServiceImpl;
import ar.com.uade.pds.final_project.users.constants.UsersErrorDetails;
import ar.com.uade.pds.final_project.users.entity.Role;
import ar.com.uade.pds.final_project.users.entity.User;
import ar.com.uade.pds.final_project.users.exception.UsersException;
import ar.com.uade.pds.final_project.users.repository.IUserRepository;
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
class ScrimServiceTest {

    @Mock
    private IScrimRepository scrimRepository;

    @Mock
    private DataService dataService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private TeamManagementService teamManagementService;

    @InjectMocks
    private ScrimServiceImpl scrimService;

    private User currentUser;
    private Scrim scrim;
    private ScrimCreationRequest scrimCreationRequest;
    private SearchRequest searchRequest;
    private JoinScrimRequest joinScrimRequest;
    private Team team;

    @BeforeEach
    void setUp() {
        currentUser = new User.Builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .region("NA")
                .mmr(1500)
                .preferredRoles(List.of(Role.SNIPER))
                .build();

        scrim = new Scrim.Builder()
                .id(1L)
                .game("Game1")
                .format("5v5")
                .players(10)
                .idCreator(1L)
                .region("NA")
                .latency(100)
                .estDuration(30)
                .mode("Ranked")
                .stateType(ScrimStateType.SEARCHING)
                .state(new Searching())
                .build();

        scrimCreationRequest = new ScrimCreationRequest();
        scrimCreationRequest.setGame("desert");
        scrimCreationRequest.setFormat("5v5");
        scrimCreationRequest.setMode("ranked");

        searchRequest = new SearchRequest("desert", "NA", "5v5");
        joinScrimRequest = new JoinScrimRequest(1L);

        team = new Team.Builder()
                .id(1L)
                .scrim(scrim)
                .build();
    }

    @Test
    void testCreateScrim_Success() {
        Team teamAlpha = new Team.Builder()
                .id(1L)
                .scrim(scrim)
                .build();
        Team teamBravo = new Team.Builder()
                .id(2L)
                .scrim(scrim)
                .build();
        List<Team> teams = List.of(teamAlpha, teamBravo);
        
        when(dataService.checkIsAuthenticated()).thenReturn(true);
        when(dataService.findUserWithToken()).thenReturn(currentUser);
        when(scrimRepository.findAllWithActiveStates()).thenReturn(new ArrayList<>());
        when(scrimRepository.save(any(Scrim.class))).thenAnswer(invocation -> {
            Scrim s = invocation.getArgument(0);
            s.setId(1L);
            return s;
        });
        when(scrimRepository.findById(1L)).thenAnswer(invocation -> {
            Scrim savedScrim = new Scrim.Builder()
                    .id(1L)
                    .game("desert")
                    .format("5v5")
                    .players(10)
                    .idCreator(1L)
                    .region("NA")
                    .latency(100)
                    .estDuration(30)
                    .mode("ranked")
                    .stateType(ScrimStateType.SEARCHING)
                    .state(new Searching())
                    .roles(List.of(Role.SNIPER))
                    .build();
            savedScrim.addTeams(teams);
            return Optional.of(savedScrim);
        });
        when(teamManagementService.constructTeams(any(Scrim.class))).thenReturn(teams);
        when(teamManagementService.selectTeam(any(Scrim.class))).thenReturn(teamAlpha);
        doNothing().when(notificationService).process(any(DomainEvent.class));

        ValidationDTOResponse result = scrimService.createScrim(scrimCreationRequest);

        assertNotNull(result);
        assertTrue(result.isValid());
        verify(dataService, times(2)).checkIsAuthenticated();
        verify(dataService, times(2)).findUserWithToken();
        verify(scrimRepository, atLeast(2)).save(any(Scrim.class));
        verify(notificationService, times(1)).process(any(DomainEvent.class));
    }

    @Test
    void testCreateScrim_NotAuthenticated() {
        when(dataService.checkIsAuthenticated()).thenReturn(false);

        UsersException exception = assertThrows(UsersException.class,
                () -> scrimService.createScrim(scrimCreationRequest));

        assertEquals(UsersErrorDetails.USER_NOT_AUTHENTICATED.getMessage(), exception.getMessage());
        verify(scrimRepository, never()).save(any(Scrim.class));
    }

    @Test
    void testEndScrim_Success() {
        ScrimParticipant participant = new ScrimParticipant.Builder()
                .setId(1L)
                .setUser(currentUser)
                .build();
        participant.setWinner(true);
        participant.setScore(100);
        team.setParticipants(List.of(participant));
        scrim.addTeams(List.of(team));
        scrim.setRoles(List.of(Role.SNIPER));
        scrim.setState(new InGame());
        scrim.setStateType(ScrimStateType.IN_GAME);

        when(dataService.checkIsAuthenticated()).thenReturn(true);
        when(scrimRepository.findById(1L)).thenReturn(Optional.of(scrim));
        when(dataService.findUserWithToken()).thenReturn(currentUser);
        when(dataService.findUserById(1L)).thenReturn(currentUser);
        when(userRepository.save(any(User.class))).thenReturn(currentUser);
        when(scrimRepository.save(any(Scrim.class))).thenReturn(scrim);
        doNothing().when(notificationService).process(any(DomainEvent.class));

        ValidationDTOResponse result = scrimService.endScrim(1L);

        assertNotNull(result);
        assertTrue(result.isValid());
        verify(scrimRepository, times(1)).findById(1L);
        verify(scrimRepository, times(1)).save(scrim);
    }


    @Test
    void testEndScrim_ThrowsError() {
        ScrimParticipant participant = new ScrimParticipant.Builder()
                .setId(1L)
                .setUser(currentUser)
                .build();
        participant.setWinner(true);
        participant.setScore(100);
        team.setParticipants(List.of(participant));
        scrim.addTeams(List.of(team));
        scrim.setRoles(List.of(Role.SNIPER));
        scrim.setState(new InGame());
        scrim.setStateType(ScrimStateType.IN_GAME);

        User userCreator = new User.Builder()
                .id(2L)
                .username("anotheruser")
                .build();

        when(dataService.checkIsAuthenticated()).thenReturn(true);
        when(dataService.findUserWithToken()).thenReturn(userCreator);
        when(scrimRepository.findById(1L)).thenReturn(Optional.of(scrim));

        assertThrows(ScrimException.class,
                () -> scrimService.endScrim(1L));
    }

    @Test
    void testEndScrim_ScrimNotFound() {
        when(dataService.checkIsAuthenticated()).thenReturn(true);
        when(scrimRepository.findById(1L)).thenReturn(Optional.empty());

        ScrimException exception = assertThrows(ScrimException.class,
                () -> scrimService.endScrim(1L));

        assertEquals(ErrorDescription.SCRIM_NOT_FOUND.getDescription(), exception.getMessage());
    }

    @Test
    void testCancelScrim_Success() {
        when(dataService.checkIsAuthenticated()).thenReturn(true);
        when(scrimRepository.findById(1L)).thenReturn(Optional.of(scrim));
        when(dataService.findUserWithToken()).thenReturn(currentUser);
        when(scrimRepository.save(any(Scrim.class))).thenReturn(scrim);
        doNothing().when(notificationService).process(any(DomainEvent.class));

        ValidationDTOResponse result = scrimService.cancelScrim(1L);

        assertNotNull(result);
        assertTrue(result.isValid());
        verify(scrimRepository, times(1)).save(scrim);
    }


    @Test
    void testCancelScrim_ThrowsError() {
        when(dataService.checkIsAuthenticated()).thenReturn(true);
        when(scrimRepository.findById(1L)).thenReturn(Optional.of(scrim));
        when(dataService.findUserWithToken()).thenReturn(currentUser);
        User userCreator = new User.Builder()
                .id(2L)
                .username("anotheruser")
                .build();
        when(dataService.findUserWithToken()).thenReturn(userCreator);

        assertThrows(ScrimException.class,
                () -> scrimService.cancelScrim(1L));
    }

    @Test
    void testCancelScrim_ScrimNotFound() {
        when(dataService.checkIsAuthenticated()).thenReturn(true);
        when(scrimRepository.findById(1L)).thenReturn(Optional.empty());

        ScrimException exception = assertThrows(ScrimException.class,
                () -> scrimService.cancelScrim(1L));

        assertEquals(ErrorDescription.SCRIM_NOT_FOUND.getDescription(), exception.getMessage());
        verify(scrimRepository, times(1)).findById(1L);
    }

    @Test
    void testConfirmScrim_Success() {
        ScrimParticipant participant = new ScrimParticipant.Builder()
                .setId(1L)
                .setUser(currentUser)
                .build();
        team.setParticipants(List.of(participant));
        scrim.addTeams(List.of(team));
        scrim.setRoles(List.of(Role.SNIPER));
        scrim.setState(new Lobby());
        scrim.setStateType(ScrimStateType.LOBBY);
        
        when(dataService.checkIsAuthenticated()).thenReturn(true);
        when(scrimRepository.findById(1L)).thenReturn(Optional.of(scrim));
        when(dataService.findUserWithToken()).thenReturn(currentUser);
        when(scrimRepository.save(any(Scrim.class))).thenReturn(scrim);
        doNothing().when(notificationService).process(any(DomainEvent.class));

        ValidationDTOResponse result = scrimService.confirmScrim(1L);

        assertNotNull(result);
        assertTrue(result.isValid());
        verify(scrimRepository, times(1)).save(scrim);
    }

    @Test
    void testSearchScrims_Success() {
        scrim.setRoles(List.of(Role.SNIPER));
        when(scrimRepository.findByFilters("desert", "NA", "5v5")).thenReturn(List.of(scrim));

        List<ScrimDTO> result = scrimService.searchScrims(searchRequest);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(scrimRepository, times(1)).findByFilters("desert", "NA", "5v5");
    }

    @Test
    void testSearchScrims_EmptyResults_FallbackToAvailable() {
        scrim.setRoles(List.of(Role.SNIPER));
        when(scrimRepository.findByFilters("desert", "NA", "5v5")).thenReturn(new ArrayList<>());
        when(scrimRepository.findAllByStateType(ScrimStateType.SEARCHING)).thenReturn(List.of(scrim));

        List<ScrimDTO> result = scrimService.searchScrims(searchRequest);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(scrimRepository, times(1)).findByFilters("desert", "NA", "5v5");
        verify(scrimRepository, times(1)).findAllByStateType(ScrimStateType.SEARCHING);
    }

    @Test
    void testJoinQueue_Success() {
        Team teamAlpha = new Team.Builder()
                .id(1L)
                .scrim(scrim)
                .build();
        Team teamBravo = new Team.Builder()
                .id(2L)
                .scrim(scrim)
                .build();
        scrim.addTeams(List.of(teamAlpha, teamBravo));
        
        when(dataService.checkIsAuthenticated()).thenReturn(true);
        when(scrimRepository.findById(1L)).thenReturn(Optional.of(scrim));
        when(dataService.findUserWithToken()).thenReturn(currentUser);
        when(scrimRepository.findAllWithActiveStates()).thenReturn(new ArrayList<>());
        when(teamManagementService.selectTeam(scrim)).thenReturn(teamAlpha);
        when(scrimRepository.save(any(Scrim.class))).thenReturn(scrim);

        ValidationDTOResponse result = scrimService.joinQueue(joinScrimRequest);

        assertNotNull(result);
        assertTrue(result.isValid());
        verify(scrimRepository, times(1)).findById(1L);
        verify(teamManagementService, times(1)).selectTeam(scrim);
        verify(scrimRepository, times(1)).save(scrim);
    }

    @Test
    void testJoinQueue_NotAuthenticated() {
        when(dataService.checkIsAuthenticated()).thenReturn(false);

        UsersException exception = assertThrows(UsersException.class,
                () -> scrimService.joinQueue(joinScrimRequest));

        assertEquals(UsersErrorDetails.USER_NOT_AUTHENTICATED.getMessage(), exception.getMessage());
    }

    @Test
    void testJoinQueue_ScrimNotFound() {
        when(dataService.checkIsAuthenticated()).thenReturn(true);
        when(scrimRepository.findById(1L)).thenReturn(Optional.empty());

        ScrimException exception = assertThrows(ScrimException.class,
                () -> scrimService.joinQueue(joinScrimRequest));

        assertEquals(ErrorDescription.SCRIM_NOT_FOUND.getDescription(), exception.getMessage());
    }

    @Test
    void testSearchAvailableScrims_Success() {
        scrim.setRoles(List.of(Role.SNIPER));
        when(scrimRepository.findAllByStateType(ScrimStateType.SEARCHING)).thenReturn(List.of(scrim));

        List<ScrimDTO> result = scrimService.searchAvailableScrims();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(scrimRepository, times(1)).findAllByStateType(ScrimStateType.SEARCHING);
    }

    @Test
    void testSearchAvailableScrims_NoScrimsAvailable() {
        when(scrimRepository.findAllByStateType(ScrimStateType.SEARCHING)).thenReturn(new ArrayList<>());

        ScrimException exception = assertThrows(ScrimException.class,
                () -> scrimService.searchAvailableScrims());

        assertEquals(ErrorDescription.NOT_AVAILABLE_SCRIMS.getDescription(), exception.getMessage());
    }

    @Test
    void testFindAllByStateType_Success() {
        when(scrimRepository.findAllByStateType(ScrimStateType.SEARCHING)).thenReturn(List.of(scrim));

        List<Scrim> result = scrimService.findAllByStateType(ScrimStateType.SEARCHING);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(scrimRepository, times(1)).findAllByStateType(ScrimStateType.SEARCHING);
    }

    @Test
    void testGetUsersFromParticipants_Success() {
        ScrimParticipant participant = new ScrimParticipant.Builder()
                .setId(1L)
                .setUser(currentUser)
                .build();

        when(dataService.findUserById(1L)).thenReturn(currentUser);

        List<User> result = scrimService.getUsersFromParticipants(List.of(participant));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(currentUser, result.get(0));
        verify(dataService, times(1)).findUserById(1L);
    }
}

