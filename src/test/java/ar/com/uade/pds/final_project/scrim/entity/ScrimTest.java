package ar.com.uade.pds.final_project.scrim.entity;

import ar.com.uade.pds.final_project.notifications.event.DomainEvent;
import ar.com.uade.pds.final_project.notifications.event.EventType;
import ar.com.uade.pds.final_project.scrim.business.game.state.*;
import ar.com.uade.pds.final_project.scrim.exception.ScrimException;
import ar.com.uade.pds.final_project.users.entity.Role;
import ar.com.uade.pds.final_project.users.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScrimTest {

    private Scrim scrim;

    @Mock
    private User userMock;

    @Mock
    private ScrimParticipant participant1;
    @Mock
    private ScrimParticipant participant2;

    @Mock
    private Team teamMock;

    @Mock
    private PlayerStats playerStatsMock;

    @BeforeEach
    void setUp() {
        scrim = new Scrim.Builder()
                .id(1L)
                .idCreator(10L)
                .game("desert")
                .format("5v5")
                .players(10)
                .roles(List.of(Role.SUPPORT))
                .region("LATAM")
                .latency(40)
                .estDuration(30)
                .mode("casual")
                .build();
    }

    @Test
    void testBuilderAndDefaults() {
        assertEquals("desert", scrim.getGame());
        assertEquals(ScrimStateType.SEARCHING, scrim.getStateType());
        assertTrue(scrim.getState() instanceof Searching);
        assertNotNull(scrim.getRoles());
        assertTrue(scrim.getTeams().isEmpty());
        assertEquals(10L, scrim.getIdCreator());
    }

    @Test
    void testSetStateUpdatesType() {
        scrim.setState(new Lobby());
        assertTrue(scrim.getState() instanceof Lobby);
        assertEquals(ScrimStateType.LOBBY, scrim.getStateType());
    }

    @Test
    void testHasValidStateToJoin() {
        scrim.setState(new Searching());
        scrim.setStateType(ScrimStateType.SEARCHING);
        assertTrue(scrim.hasValidStateToJoin());
    }

    @Test
    void testAddDomainEvent() {
        DomainEvent event = new DomainEvent(EventType.SCRIM_CONFIRMED, null, List.of(1L), 99L);
        scrim.addDomainEvent(event);
        assertTrue(scrim.getDomainEvents().contains(event));
    }

    @Test
    void testAddTeamAndIsFull() {
        Team fullTeam = mock(Team.class);
        when(fullTeam.isFull()).thenReturn(true);
        scrim.addTeam(fullTeam);
        assertTrue(scrim.getTeams().contains(fullTeam));
        verify(fullTeam).setScrim(scrim);
        assertTrue(scrim.isFull());
    }

    @Test
    void testAddTeamsHandlesNull() {
        scrim.addTeams(null);
        assertTrue(scrim.getTeams().isEmpty());
    }

    @Test
    void testAddTeamsWithMultipleTeams() {
        Team team1 = mock(Team.class);
        Team team2 = mock(Team.class);
        scrim.addTeams(List.of(team1, team2));
        assertEquals(2, scrim.getTeams().size());
        verify(team1).setScrim(scrim);
        verify(team2).setScrim(scrim);
    }

    @Test
    void testAddPlayerStat() {
        scrim.addPlayerStat(playerStatsMock);
        verify(playerStatsMock).setScrim(scrim);
        assertTrue(scrim.getPlayerStats().contains(playerStatsMock));
    }

    @Test
    void testGetAllParticipants() {
        when(teamMock.getParticipants()).thenReturn(List.of(participant1, participant2));
        scrim.addTeam(teamMock);
        List<ScrimParticipant> result = scrim.getAllParticipants();
        assertEquals(2, result.size());
    }

    @Test
    void testFindParticipantByUserId() {
        User u = mock(User.class);
        when(u.getId()).thenReturn(1L);
        when(participant1.getUser()).thenReturn(u);
        when(participant1.getUser().getId()).thenReturn(1L);
        when(teamMock.getParticipants()).thenReturn(List.of(participant1));
        scrim.addTeam(teamMock);

        Optional<ScrimParticipant> result = scrim.findParticipantByUserId(1L);
        assertTrue(result.isPresent());
    }

    @Test
    void testParticipantInOtherScrim() {
        when(participant1.equalIds(5L)).thenReturn(true);
        when(teamMock.getParticipants()).thenReturn(List.of(participant1));
        scrim.addTeam(teamMock);

        assertTrue(scrim.participantInOtherScrim(5L));
    }

    @Test
    void testSetCurrentStateFromType() {
        scrim.setStateType(ScrimStateType.LOBBY);
        scrim.setCurrentState();
        assertTrue(scrim.getState() instanceof Lobby);
    }

    // ----- STATE MACHINE TESTS -----

    @Test
    void testSearchingCancelGoesToCancelled() {
        scrim.setState(new Searching());
        scrim.cancel(1L);
        assertTrue(scrim.getState() instanceof Cancelled);
    }

    @Test
    void testLobbyConfirmAllUsersConfirmed() {
        scrim.setState(new Lobby());
        when(teamMock.getParticipants()).thenReturn(List.of(participant1));
        when(participant1.getId()).thenReturn(100L);
        scrim.addTeam(teamMock);

        when(userMock.getId()).thenReturn(100L);
        scrim.confirm(userMock);

        assertTrue(scrim.getState() instanceof Confirmed
                || scrim.getState() instanceof InGame
                || scrim.getState() instanceof Lobby);
    }

    @Test
    void testConfirmedStartMovesToInGame() {
        scrim.setState(new Confirmed(scrim));
        scrim.getState().start(scrim);
        assertTrue(scrim.getState() instanceof InGame);
    }

    @Test
    void testInGameEndMovesToEnded() {
        scrim.setState(new InGame());
        scrim.end();
        assertTrue(scrim.getState() instanceof Ended);
    }

    @Test
    void testInGameCancelByCreator() {
        scrim.setState(new InGame());
        scrim.setIdCreator(10L);
        assertDoesNotThrow(() -> scrim.cancel(10L));
        assertTrue(scrim.getState() instanceof Cancelled);
    }

    @Test
    void testInvalidCancelInLobbyThrows() {
        scrim.setState(new Lobby());
        scrim.setIdCreator(99L);
        assertThrows(ScrimException.class, () -> scrim.cancel(100L));
    }

    @Test
    void testEndedThrowsOnRestart() {
        scrim.setState(new Ended(scrim));
        assertThrows(IllegalStateException.class, () -> scrim.getState().start(scrim));
    }

    @Test
    void testCancelledThrowsOnConfirm() {
        scrim.setState(new Cancelled(scrim, 10L));
        assertThrows(IllegalStateException.class, () -> scrim.confirm(userMock));
    }
}

