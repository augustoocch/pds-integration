package ar.com.uade.pds.final_project.scrim.strategy;

import ar.com.uade.pds.final_project.domain.dto.request.JoinScrimRequest;
import ar.com.uade.pds.final_project.domain.dto.request.MatchmakingRequest;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.scrim.business.game.state.ScrimStateType;
import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.scrim.entity.ScrimParticipant;
import ar.com.uade.pds.final_project.scrim.entity.Team;
import ar.com.uade.pds.final_project.scrim.exception.MatchmakingException;
import ar.com.uade.pds.final_project.scrim.service.ScrimService;
import ar.com.uade.pds.final_project.users.entity.User;
import ar.com.uade.pds.final_project.users.service.DataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RangeStrategyTest {

    @Mock
    private ScrimService scrimService;

    @Mock
    private DataService dataService;

    private RangeStrategy rangeStrategy;
    private User testUser;
    private MatchmakingRequest request;

    @BeforeEach
    void setUp() {
        rangeStrategy = new RangeStrategy(scrimService, dataService);
        testUser = new User();
        testUser.setId(1L);
        testUser.setMmr(1500);
        request = new MatchmakingRequest("RANGE");
    }

    @Test
    void testExecute_WhenUserIsAuthenticated_ShouldFindSuitableScrimByMMRRange() {
        Scrim suitableScrim = createScrimWithMMRRange(1400, 1600, 1L);
        List<Scrim> availableScrims = List.of(suitableScrim);

        when(dataService.findUserWithToken()).thenReturn(testUser);
        when(scrimService.findAllByStateType(ScrimStateType.SEARCHING)).thenReturn(availableScrims);
        when(scrimService.joinQueue(any())).thenReturn(new ValidationDTOResponse(true, null));


        rangeStrategy.execute(request);

        verify(dataService, times(1)).findUserWithToken();
        verify(scrimService, times(1)).findAllByStateType(ScrimStateType.SEARCHING);
        verify(scrimService, times(1)).joinQueue(any(JoinScrimRequest.class));
    }

    @Test
    void testExecute_WhenUserIsNotAuthenticated_ShouldThrowException() {
        when(dataService.findUserWithToken()).thenReturn(null);

        assertThrows(MatchmakingException.class, () -> rangeStrategy.execute(request));
        verify(dataService, times(1)).findUserWithToken();
        verify(scrimService, never()).findAllByStateType(any());
        verify(scrimService, never()).joinQueue(any());
    }

    @Test
    void testExecute_WhenUserMmrIsNull_ShouldThrowException() {
        User anon = new User();
        anon.setId(2L);
        anon.setMmr(null); // missing MMR
        when(dataService.findUserWithToken()).thenReturn(anon);

        assertThrows(MatchmakingException.class, () -> rangeStrategy.execute(request));
        verify(scrimService, never()).findAllByStateType(any());
        verify(scrimService, never()).joinQueue(any());
    }

    @Test
    void testExecute_WhenAvailableScrimsIsNull_ShouldThrowException() {
        when(dataService.findUserWithToken()).thenReturn(testUser);
        when(scrimService.findAllByStateType(ScrimStateType.SEARCHING)).thenReturn(null);

        assertThrows(MatchmakingException.class, () -> rangeStrategy.execute(request));
        verify(scrimService, times(1)).findAllByStateType(ScrimStateType.SEARCHING);
        verify(scrimService, never()).joinQueue(any());
    }

    @Test
    void testExecute_WhenNoSuitableScrimFound_ShouldThrowException() {
        Scrim unsuitableScrim = createScrimWithMMRRange(2000, 2500, 1L);
        List<Scrim> availableScrims = List.of(unsuitableScrim);

        when(dataService.findUserWithToken()).thenReturn(testUser);
        when(scrimService.findAllByStateType(ScrimStateType.SEARCHING)).thenReturn(availableScrims);

        assertThrows(MatchmakingException.class, () -> rangeStrategy.execute(request));
        verify(scrimService, never()).joinQueue(any());
    }

    @Test
    void testExecute_WhenScrimHasNoMMRRange_ShouldUseAverageMMR() {
        Scrim scrimWithoutRange = createScrimWithoutMMRRange(1L);
        List<ScrimParticipant> participants = createParticipantsWithMMR(List.of(1450, 1550, 1500));
        scrimWithoutRange.setTeams(createTeamsWithParticipants(participants));

        List<Scrim> availableScrims = List.of(scrimWithoutRange);

        when(dataService.findUserWithToken()).thenReturn(testUser);
        when(scrimService.findAllByStateType(ScrimStateType.SEARCHING)).thenReturn(availableScrims);
        when(scrimService.joinQueue(any())).thenReturn(new ValidationDTOResponse(true, null));


        rangeStrategy.execute(request);

        verify(scrimService, times(1)).joinQueue(any(JoinScrimRequest.class));
    }

    @Test
    void testExecute_WhenScrimHasNoMMRRangeAndAverageTooFar_ShouldThrowException() {
        Scrim scrimWithoutRange = createScrimWithoutMMRRange(1L);
        List<ScrimParticipant> participants = createParticipantsWithMMR(List.of(2500, 2600, 2700));
        scrimWithoutRange.setTeams(createTeamsWithParticipants(participants));

        List<Scrim> availableScrims = List.of(scrimWithoutRange);

        when(dataService.findUserWithToken()).thenReturn(testUser);
        when(scrimService.findAllByStateType(ScrimStateType.SEARCHING)).thenReturn(availableScrims);

        assertThrows(MatchmakingException.class, () -> rangeStrategy.execute(request));
        verify(scrimService, never()).joinQueue(any());
    }

    @Test
    void testExecute_WhenMultipleSuitableScrims_ShouldSelectFirstOne() {
        Scrim scrim1 = createScrimWithMMRRange(1400, 1600, 1L);
        Scrim scrim2 = createScrimWithMMRRange(1450, 1650, 2L);
        List<Scrim> availableScrims = List.of(scrim1, scrim2);

        when(dataService.findUserWithToken()).thenReturn(testUser);
        when(scrimService.findAllByStateType(ScrimStateType.SEARCHING)).thenReturn(availableScrims);
        when(scrimService.joinQueue(any())).thenReturn(new ValidationDTOResponse(true, null));


        rangeStrategy.execute(request);

        verify(scrimService, times(1)).joinQueue(argThat(req -> req.getIdScrim().equals(1L)));
    }

    @Test
    void testExecute_WhenUserMMRIsAtMinBoundary_ShouldMatch() {
        testUser.setMmr(1400);
        Scrim scrim = createScrimWithMMRRange(1400, 1600, 1L);
        List<Scrim> availableScrims = List.of(scrim);

        when(dataService.findUserWithToken()).thenReturn(testUser);
        when(scrimService.findAllByStateType(ScrimStateType.SEARCHING)).thenReturn(availableScrims);
        when(scrimService.joinQueue(any())).thenReturn(new ValidationDTOResponse(true, null));


        rangeStrategy.execute(request);

        verify(scrimService, times(1)).joinQueue(any(JoinScrimRequest.class));
    }

    @Test
    void testExecute_WhenUserMMRIsAtMaxBoundary_ShouldMatch() {
        testUser.setMmr(1600);
        Scrim scrim = createScrimWithMMRRange(1400, 1600, 1L);
        List<Scrim> availableScrims = List.of(scrim);

        when(dataService.findUserWithToken()).thenReturn(testUser);
        when(scrimService.findAllByStateType(ScrimStateType.SEARCHING)).thenReturn(availableScrims);
        when(scrimService.joinQueue(any())).thenReturn(new ValidationDTOResponse(true, null));

        rangeStrategy.execute(request);

        verify(scrimService, times(1)).joinQueue(any(JoinScrimRequest.class));
    }

    // --- HELPERS ---

    private Scrim createScrimWithMMRRange(Integer mmrMin, Integer mmrMax, Long id) {
        Scrim scrim = new Scrim.Builder()
                .id(id)
                .mmrMin(mmrMin)
                .mmrMax(mmrMax)
                .latency(500)
                .stateType(ScrimStateType.SEARCHING)
                .build();
        scrim.setTeams(new ArrayList<>());
        return scrim;
    }

    private Scrim createScrimWithoutMMRRange(Long id) {
        Scrim scrim = new Scrim.Builder()
                .id(id)
                .mmrMin(null)
                .mmrMax(null)
                .latency(500)
                .stateType(ScrimStateType.SEARCHING)
                .build();
        scrim.setTeams(new ArrayList<>());
        return scrim;
    }

    private List<ScrimParticipant> createParticipantsWithMMR(List<Integer> mmrValues) {
        List<ScrimParticipant> participants = new ArrayList<>();
        for (int i = 0; i < mmrValues.size(); i++) {
            ScrimParticipant participant = new ScrimParticipant.Builder()
                    .setId((long) (i + 1))
                    .build();
            participant.setMmr(mmrValues.get(i));
            participants.add(participant);
        }
        return participants;
    }

    private List<Team> createTeamsWithParticipants(List<ScrimParticipant> participants) {
        Team team = new Team.Builder()
                .id(1L)
                .build();
        team.setParticipants(participants);
        return List.of(team);
    }
}

