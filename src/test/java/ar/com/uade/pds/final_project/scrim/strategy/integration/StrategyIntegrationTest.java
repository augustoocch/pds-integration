package ar.com.uade.pds.final_project.scrim.strategy.integration;

import ar.com.uade.pds.final_project.domain.dto.request.MatchmakingRequest;
import ar.com.uade.pds.final_project.scrim.business.game.state.ScrimStateType;
import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.scrim.entity.ScrimParticipant;
import ar.com.uade.pds.final_project.scrim.entity.Team;
import ar.com.uade.pds.final_project.scrim.exception.MatchmakingException;
import ar.com.uade.pds.final_project.scrim.service.ScrimService;
import ar.com.uade.pds.final_project.scrim.strategy.CompatibilityStrategy;
import ar.com.uade.pds.final_project.scrim.strategy.LatencyStrategy;
import ar.com.uade.pds.final_project.scrim.strategy.MatchMakingStrategyFactory;
import ar.com.uade.pds.final_project.scrim.strategy.RangeStrategy;
import ar.com.uade.pds.final_project.users.entity.Role;
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
class StrategyIntegrationTest {

    @Mock
    private ScrimService scrimService;

    @Mock
    private DataService dataService;

    private MatchMakingStrategyFactory strategyFactory;
    private User testUser;

    @BeforeEach
    void setUp() {
        strategyFactory = new MatchMakingStrategyFactory(scrimService, dataService);
        testUser = new User();
        testUser.setId(1L);
        testUser.setMmr(1500);
        testUser.setLatency(100);
        testUser.setRegion("NA");
        testUser.setPreferredRoles(List.of(Role.ASSASSIN));
    }

    @Test
    void testRangeStrategy_Integration_ShouldFindSuitableScrim() {
        MatchmakingRequest request = new MatchmakingRequest("RANGE");
        Scrim suitableScrim = createScrimWithMMRRange(1400, 1600, 1L);
        List<Scrim> availableScrims = List.of(suitableScrim);

        when(dataService.findUserWithToken()).thenReturn(testUser);
        when(scrimService.findAllByStateType(ScrimStateType.SEARCHING)).thenReturn(availableScrims);
        doNothing().when(scrimService).joinQueue(any());

        var strategy = strategyFactory.getStrategy(request);
        strategy.execute(request);

        verify(scrimService, times(1)).joinQueue(any());
    }

    @Test
    void testLatencyStrategy_Integration_ShouldFindSuitableScrim() {
        MatchmakingRequest request = new MatchmakingRequest("LATENCY");
        Scrim suitableScrim = createScrimWithLatency(110, 1L);
        List<Scrim> availableScrims = List.of(suitableScrim);

        when(dataService.findUserWithToken()).thenReturn(testUser);
        when(scrimService.findAllByStateType(ScrimStateType.SEARCHING)).thenReturn(availableScrims);
        doNothing().when(scrimService).joinQueue(any());

        var strategy = strategyFactory.getStrategy(request);
        strategy.execute(request);

        verify(scrimService, times(1)).joinQueue(any());
    }

    @Test
    void testCompatibilityStrategy_Integration_ShouldFindSuitableScrim() {
        MatchmakingRequest request = new MatchmakingRequest("COMPATIBILITY");
        Scrim suitableScrim = createScrimForCompatibility(1L);
        List<ScrimParticipant> participants = createParticipantsForCompatibility();
        suitableScrim.setTeams(createTeamsWithParticipants(participants));
        List<Scrim> availableScrims = List.of(suitableScrim);

        when(dataService.findUserWithToken()).thenReturn(testUser);
        when(scrimService.findAllByStateType(ScrimStateType.SEARCHING)).thenReturn(availableScrims);
        when(scrimService.getUsersFromParticipants(any())).thenReturn(createUsersForCompatibility());
        doNothing().when(scrimService).joinQueue(any());

        var strategy = strategyFactory.getStrategy(request);
        strategy.execute(request);

        verify(scrimService, times(1)).joinQueue(any());
    }

    @Test
    void testStrategyFactory_WhenInvalidStrategy_ShouldThrowException() {
        MatchmakingRequest request = new MatchmakingRequest("INVALID");

        assertThrows(IllegalArgumentException.class, () -> strategyFactory.getStrategy(request));
    }

    @Test
    void testStrategyFactory_WhenRangeStrategy_ShouldReturnRangeStrategy() {
        MatchmakingRequest request = new MatchmakingRequest("RANGE");

        var strategy = strategyFactory.getStrategy(request);

        assertTrue(strategy instanceof RangeStrategy);
    }

    @Test
    void testStrategyFactory_WhenLatencyStrategy_ShouldReturnLatencyStrategy() {
        MatchmakingRequest request = new MatchmakingRequest("LATENCY");

        var strategy = strategyFactory.getStrategy(request);

        assertTrue(strategy instanceof LatencyStrategy);
    }

    @Test
    void testStrategyFactory_WhenCompatibilityStrategy_ShouldReturnCompatibilityStrategy() {
        MatchmakingRequest request = new MatchmakingRequest("COMPATIBILITY");

        var strategy = strategyFactory.getStrategy(request);

        assertTrue(strategy instanceof CompatibilityStrategy);
    }

    @Test
    void testStrategyFactory_CaseInsensitive_ShouldWork() {
        MatchmakingRequest request1 = new MatchmakingRequest("range");
        MatchmakingRequest request2 = new MatchmakingRequest("RANGE");
        MatchmakingRequest request3 = new MatchmakingRequest("Range");

        assertDoesNotThrow(() -> {
            var strategy1 = strategyFactory.getStrategy(request1);
            var strategy2 = strategyFactory.getStrategy(request2);
            var strategy3 = strategyFactory.getStrategy(request3);
            assertTrue(strategy1 instanceof RangeStrategy);
            assertTrue(strategy2 instanceof RangeStrategy);
            assertTrue(strategy3 instanceof RangeStrategy);
        });
    }

    @Test
    void testMultipleStrategies_Integration_ShouldWorkIndependently() {
        MatchmakingRequest rangeRequest = new MatchmakingRequest("RANGE");
        MatchmakingRequest latencyRequest = new MatchmakingRequest("LATENCY");
        MatchmakingRequest compatibilityRequest = new MatchmakingRequest("COMPATIBILITY");

        when(dataService.findUserWithToken()).thenReturn(testUser);
        when(scrimService.findAllByStateType(ScrimStateType.SEARCHING)).thenReturn(List.of());
        doNothing().when(scrimService).joinQueue(any());

        var rangeStrategy = strategyFactory.getStrategy(rangeRequest);
        var latencyStrategy = strategyFactory.getStrategy(latencyRequest);
        var compatibilityStrategy = strategyFactory.getStrategy(compatibilityRequest);

        assertTrue(rangeStrategy instanceof RangeStrategy);
        assertTrue(latencyStrategy instanceof LatencyStrategy);
        assertTrue(compatibilityStrategy instanceof CompatibilityStrategy);
    }
    private Scrim createScrimWithMMRRange(Integer mmrMin, Integer mmrMax, Long id) {
        Scrim scrim = new Scrim.Builder()
                .id(id)
                .mmrMin(mmrMin)
                .mmrMax(mmrMax)
                .stateType(ScrimStateType.SEARCHING)
                .build();
        scrim.setTeams(new ArrayList<>());
        return scrim;
    }

    private Scrim createScrimWithLatency(int latency, Long id) {
        Scrim scrim = new Scrim.Builder()
                .id(id)
                .stateType(ScrimStateType.SEARCHING)
                .build();
        scrim.setLatency(latency);
        scrim.setTeams(new ArrayList<>());
        return scrim;
    }

    private Scrim createScrimForCompatibility(Long id) {
        Scrim scrim = new Scrim.Builder()
                .id(id)
                .stateType(ScrimStateType.SEARCHING)
                .build();
        scrim.setTeams(new ArrayList<>());
        return scrim;
    }

    private List<User> createUsersForCompatibility() {
        User user1 = new User();
        user1.setId(2L);
        user1.setMmr(1450);
        user1.setLatency(90);
        user1.setRegion("NA");
        user1.setPreferredRoles(List.of(Role.SNIPER));

        User user2 = new User();
        user2.setId(3L);
        user2.setMmr(1550);
        user2.setLatency(110);
        user2.setRegion("NA");
        user2.setPreferredRoles(List.of(Role.SUPPORT));

        return List.of(user1, user2);
    }

    private List<ScrimParticipant> createParticipantsForCompatibility() {
        List<ScrimParticipant> participants = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            ScrimParticipant participant = new ScrimParticipant.Builder()
                    .setId((long) (i + 2))
                    .build();
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

