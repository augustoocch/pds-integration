package ar.com.uade.pds.final_project.scrim.strategy;

import ar.com.uade.pds.final_project.domain.dto.request.JoinScrimRequest;
import ar.com.uade.pds.final_project.domain.dto.request.MatchmakingRequest;
import ar.com.uade.pds.final_project.scrim.business.game.state.ScrimStateType;
import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.scrim.exception.MatchmakingException;
import ar.com.uade.pds.final_project.scrim.service.ScrimService;
import ar.com.uade.pds.final_project.users.constants.UsersErrorDetails;
import ar.com.uade.pds.final_project.users.entity.User;
import ar.com.uade.pds.final_project.users.exception.UsersException;
import ar.com.uade.pds.final_project.users.service.DataService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.Comparator;
import java.util.List;

import static ar.com.uade.pds.final_project.scrim.constants.ErrorDescription.*;


@AllArgsConstructor
public class LatencyStrategy implements MatchMakingStrategy {

    private final ScrimService scrimService;
    private final DataService dataService;

    private static final int MAX_LATENCY_DIFFERENCE = 50;
    private static final int MAX_ACCEPTABLE_LATENCY = 180;

    @Override
    @Transactional
    public void execute(MatchmakingRequest request) {
        User currentUser = dataService.findUserWithToken();
        if (currentUser == null) {
            throw new UsersException(UsersErrorDetails.USER_NOT_AUTHENTICATED.getMessage());
        }

        int userLatency = currentUser.getLatency();
        List<Scrim> availableScrims = scrimService.findAllByStateType(ScrimStateType.SEARCHING);

        Scrim suitableScrim = availableScrims.stream()
                .filter(scrim -> {
                    int scrimLatency = scrim.getLatency();
                    if (scrimLatency > MAX_ACCEPTABLE_LATENCY) return false;
                    return Math.abs(scrimLatency - userLatency) <= MAX_LATENCY_DIFFERENCE;
                })
                // Ordenar por latencia más cercana al usuario
                .min(Comparator.comparingInt(scrim ->
                        Math.abs(scrim.getLatency() - userLatency)))
                .orElseThrow(() -> new MatchmakingException(LATENCY_MATCHMAKING_ERROR.getDescription()));

        scrimService.joinQueue(new JoinScrimRequest(suitableScrim.getId()));
    }
}
