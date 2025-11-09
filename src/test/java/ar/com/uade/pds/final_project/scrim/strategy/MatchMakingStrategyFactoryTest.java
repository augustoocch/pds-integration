package ar.com.uade.pds.final_project.scrim.strategy;

import ar.com.uade.pds.final_project.domain.dto.request.MatchmakingRequest;
import ar.com.uade.pds.final_project.scrim.business.game.state.ScrimStateType;
import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.scrim.entity.ScrimParticipant;
import ar.com.uade.pds.final_project.scrim.entity.Team;
import ar.com.uade.pds.final_project.scrim.service.ScrimService;
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
class MatchMakingStrategyFactoryTest {

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

    // --- FACTORY TESTS ---

    @Test
    void testGetStrategy_Range_ShouldReturnRangeStrategy() {
        var strategy = strategyFactory.getStrategy(new MatchmakingRequest("RANGE"));
        assertTrue(strategy instanceof RangeStrategy);
    }

    @Test
    void testGetStrategy_Latency_ShouldReturnLatencyStrategy() {
        var strategy = strategyFactory.getStrategy(new MatchmakingRequest("LATENCY"));
        assertTrue(strategy instanceof LatencyStrategy);
    }

    @Test
    void testGetStrategy_Compatibility_ShouldReturnCompatibilityStrategy() {
        var strategy = strategyFactory.getStrategy(new MatchmakingRequest("COMPATIBILITY"));
        assertTrue(strategy instanceof CompatibilityStrategy);
    }

    @Test
    void testGetStrategy_Invalid_ShouldThrowException() {
        MatchmakingRequest request = new MatchmakingRequest("UNKNOWN");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> strategyFactory.getStrategy(request));
        assertEquals("Invalid strategy: UNKNOWN", ex.getMessage());
    }

    @Test
    void testGetStrategy_CaseInsensitive_ShouldReturnSameResult() {
        var lower = strategyFactory.getStrategy(new MatchmakingRequest("range"));
        var mixed = strategyFactory.getStrategy(new MatchmakingRequest("RaNgE"));
        var upper = strategyFactory.getStrategy(new MatchmakingRequest("RANGE"));

        assertAll(
                () -> assertTrue(lower instanceof RangeStrategy),
                () -> assertTrue(mixed instanceof RangeStrategy),
                () -> assertTrue(upper instanceof RangeStrategy)
        );
    }

    // --- INTEGRATION TESTS ---

    @Test
    void testRangeStrategy_ShouldJoinSuitableScrim() {
        MatchmakingRequest request = new MatchmakingRequest("RANGE");
        Scrim suitableScrim = createScrimWithMMRRange(1400, 1600, 1L);

        when(dataService.findUserWithToken()).thenReturn(testUser);
        when(scrimService.findAllByStateType(ScrimStateType.SEARCHING)).thenReturn(List.of(suitableScrim));

        var strategy = strategyFactory.getStrategy(request);
        strategy.execute(request);

        verify(scrimService).joinQueue(any());
    }

    @Test
    void testLatencyStrategy_ShouldJoinLowLatencyScrim() {
        MatchmakingRequest request = new MatchmakingRequest("LATENCY");
        Scrim suitableScrim = createScrimWithLatency(105, 1L);

        when(dataService.findUserWithToken()).thenReturn(testUser);
        when(scrimService.findAllByStateType(ScrimStateType.SEARCHING)).thenReturn(List.of(suitableScrim));

        var strategy = strategyFactory.getStrategy(request);
        strategy.execute(request);

        verify(scrimService).joinQueue(any());
    }

    @Test
    void testCompatibilityStrategy_ShouldJoinCompatibleScrim() {
        MatchmakingRequest request = new MatchmakingRequest("COMPATIBILITY");
        Scrim suitableScrim = createScrimForCompatibility(1L);
        List<ScrimParticipant> participants = createParticipantsForCompatibility();
        suitableScrim.setTeams(createTeamsWithParticipants(participants));

        when(dataService.findUserWithToken()).thenReturn(testUser);
        when(scrimService.findAllByStateType(ScrimStateType.SEARCHING)).thenReturn(List.of(suitableScrim));

        var strategy = strategyFactory.getStrategy(request);
        strategy.execute(request);

        verify(scrimService).joinQueue(any());
    }

    @Test
    void testMultipleStrategies_ShouldOperateIndependently() {
        var rangeStrategy = spy(strategyFactory.getStrategy(new MatchmakingRequest("RANGE")));
        var latencyStrategy = spy(strategyFactory.getStrategy(new MatchmakingRequest("LATENCY")));
        var compatibilityStrategy = spy(strategyFactory.getStrategy(new MatchmakingRequest("COMPATIBILITY")));

        doNothing().when(rangeStrategy).execute(any());
        doNothing().when(latencyStrategy).execute(any());
        doNothing().when(compatibilityStrategy).execute(any());

        assertAll(
                () -> assertTrue(rangeStrategy instanceof RangeStrategy),
                () -> assertTrue(latencyStrategy instanceof LatencyStrategy),
                () -> assertTrue(compatibilityStrategy instanceof CompatibilityStrategy)
        );

        rangeStrategy.execute(new MatchmakingRequest("RANGE"));
        latencyStrategy.execute(new MatchmakingRequest("LATENCY"));
        compatibilityStrategy.execute(new MatchmakingRequest("COMPATIBILITY"));

        verify(rangeStrategy).execute(any());
        verify(latencyStrategy).execute(any());
        verify(compatibilityStrategy).execute(any());
    }


    // --- HELPERS ---

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
                .latency(599)
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

