package ar.com.uade.pds.final_project.scrim.business.game.state;

import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.scrim.entity.ScrimParticipant;
import ar.com.uade.pds.final_project.scrim.entity.Team;
import ar.com.uade.pds.final_project.scrim.exception.ScrimException;
import ar.com.uade.pds.final_project.users.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ScrimStateTransitionsTest {

    private Scrim scrim;
    private User testUser;
    private Long creatorId;

    @BeforeEach
    void setUp() {
        creatorId = 1L;
        testUser = new User();
        testUser.setId(creatorId);
        
        scrim = new Scrim.Builder()
                .id(1L)
                .idCreator(creatorId)
                .stateType(ScrimStateType.SEARCHING)
                .build();
        scrim.setConfirmedUsers(new HashSet<>());
        scrim.setTeams(new ArrayList<>());
    }

    @Test
    void testSearchingToCancelled_ShouldTransitionSuccessfully() {
        ScrimState searching = new Searching();

        searching.cancel(scrim, creatorId);

        assertEquals(ScrimStateType.CANCELLED, scrim.getStateType());
        assertTrue(scrim.getState() instanceof Cancelled);
    }

    @Test
    void testSearchingToCancelled_WhenNotCreator_ShouldTransitionSuccessfully() {
        ScrimState searching = new Searching();
        Long nonCreatorId = 2L;

        searching.cancel(scrim, nonCreatorId);

        assertEquals(ScrimStateType.CANCELLED, scrim.getStateType());
        assertTrue(scrim.getState() instanceof Cancelled);
    }

    @Test
    void testSearchingStart_ShouldThrowIllegalStateException() {
        ScrimState searching = new Searching();

        assertThrows(IllegalStateException.class, () -> searching.start(scrim));
    }

    @Test
    void testSearchingConfirm_ShouldThrowIllegalStateException() {
        ScrimState searching = new Searching();

        assertThrows(IllegalStateException.class, () -> searching.confirm(scrim, testUser));
    }

    @Test
    void testLobbyToConfirmed_WhenAllUsersConfirmed_ShouldTransitionToInGame() {
        scrim.setStateType(ScrimStateType.LOBBY);
        scrim.setState(new Lobby());
        
        List<ScrimParticipant> participants = createParticipants(3);
        scrim.setTeams(createTeamsWithParticipants(participants));
        
        Set<Long> confirmedUsers = new HashSet<>();
        for (int i = 0; i < participants.size() - 1; i++) {
            confirmedUsers.add(participants.get(i).getId());
        }
        scrim.setConfirmedUsers(confirmedUsers);

        ScrimState lobby = new Lobby();
        User lastUser = new User();
        lastUser.setId(participants.get(participants.size() - 1).getId());
        lobby.confirm(scrim, lastUser);

        assertEquals(ScrimStateType.IN_GAME, scrim.getStateType());
        assertTrue(scrim.getState() instanceof InGame);
    }

    @Test
    void testLobbyToCancelled_WhenCreatorCancels_ShouldTransitionSuccessfully() {
        scrim.setStateType(ScrimStateType.LOBBY);
        scrim.setState(new Lobby());
        ScrimState lobby = new Lobby();

        lobby.cancel(scrim, creatorId);

        assertEquals(ScrimStateType.CANCELLED, scrim.getStateType());
        assertTrue(scrim.getState() instanceof Cancelled);
    }

    @Test
    void testLobbyToCancelled_WhenNotCreator_ShouldThrowException() {
        scrim.setStateType(ScrimStateType.LOBBY);
        scrim.setState(new Lobby());
        ScrimState lobby = new Lobby();
        Long nonCreatorId = 2L;

        assertThrows(ScrimException.class, () -> lobby.cancel(scrim, nonCreatorId));
    }

    @Test
    void testConfirmedToInGame_ShouldTransitionAutomatically() {
        List<ScrimParticipant> participants = createParticipants(2);
        scrim.setTeams(createTeamsWithParticipants(participants));

        ScrimState confirmed = new Confirmed(scrim);

        assertEquals(ScrimStateType.IN_GAME, scrim.getStateType());
        assertTrue(scrim.getState() instanceof InGame);
    }

    @Test
    void testConfirmedToCancelled_WhenCreatorCancels_ShouldTransitionSuccessfully() {
        List<ScrimParticipant> participants = createParticipants(2);
        scrim.setTeams(createTeamsWithParticipants(participants));
        ScrimState confirmed = new Confirmed(scrim);

        confirmed.cancel(scrim, creatorId);

        assertEquals(ScrimStateType.CANCELLED, scrim.getStateType());
        assertTrue(scrim.getState() instanceof Cancelled);
    }

    @Test
    void testConfirmedConfirm_ShouldThrowIllegalStateException() {
        List<ScrimParticipant> participants = createParticipants(2);
        scrim.setTeams(createTeamsWithParticipants(participants));
        ScrimState confirmed = new Confirmed(scrim);

        assertThrows(IllegalStateException.class, () -> confirmed.confirm(scrim, testUser));
    }

    @Test
    void testInGameToEnded_ShouldTransitionSuccessfully() {
        List<ScrimParticipant> participants = createParticipants(1);
        scrim.setTeams(createTeamsWithParticipants(participants));
        scrim.setStateType(ScrimStateType.IN_GAME);
        scrim.setState(new InGame());
        ScrimState inGame = new InGame();

        assertThrows(IllegalArgumentException.class, () -> inGame.end(scrim));
    }

    @Test
    void testInGameToCancelled_WhenCreatorCancels_ShouldTransitionSuccessfully() {
        scrim.setStateType(ScrimStateType.IN_GAME);
        scrim.setState(new InGame());
        ScrimState inGame = new InGame();

        inGame.cancel(scrim, creatorId);

        assertEquals(ScrimStateType.CANCELLED, scrim.getStateType());
        assertTrue(scrim.getState() instanceof Cancelled);
    }

    @Test
    void testInGameStart_ShouldThrowIllegalStateException() {
        ScrimState inGame = new InGame();

        assertThrows(IllegalStateException.class, () -> inGame.start(scrim));
    }

    @Test
    void testCancelledStart_ShouldThrowIllegalStateException() {
        List<ScrimParticipant> participants = createParticipants(1);
        scrim.setTeams(createTeamsWithParticipants(participants));
        ScrimState cancelled = new Cancelled(scrim, creatorId);

        assertThrows(IllegalStateException.class, () -> cancelled.start(scrim));
    }

    @Test
    void testCancelledCancel_ShouldThrowIllegalStateException() {
        List<ScrimParticipant> participants = createParticipants(1);
        scrim.setTeams(createTeamsWithParticipants(participants));
        ScrimState cancelled = new Cancelled(scrim, creatorId);

        assertThrows(IllegalStateException.class, () -> cancelled.cancel(scrim, creatorId));
    }

    @Test
    void testEndedStart_ShouldThrowIllegalStateException() {
        List<ScrimParticipant> participants = createParticipants(1);
        scrim.setTeams(createTeamsWithParticipants(participants));
        ScrimState ended = new Ended(scrim);

        assertThrows(IllegalStateException.class, () -> ended.start(scrim));
    }

    @Test
    void testEndedEnd_ShouldThrowIllegalStateException() {
        List<ScrimParticipant> participants = createParticipants(1);
        scrim.setTeams(createTeamsWithParticipants(participants));
        ScrimState ended = new Ended(scrim);

        assertThrows(IllegalStateException.class, () -> ended.end(scrim));
    }

    @Test
    void testStateTypeFromClass_ShouldReturnCorrectType() {
        ScrimStateType type = ScrimStateType.fromClass(Searching.class);

        assertEquals(ScrimStateType.SEARCHING, type);
    }

    @Test
    void testScrimStateFromString_ShouldCreateCorrectState() {
        ScrimState state = ScrimStateType.scrimStateFromString(ScrimStateType.SEARCHING);

        assertTrue(state instanceof Searching);
    }
    private List<ScrimParticipant> createParticipants(int count) {
        List<ScrimParticipant> participants = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ScrimParticipant participant = new ScrimParticipant.Builder()
                    .setId((long) (i + 1))
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

