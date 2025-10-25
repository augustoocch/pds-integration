package ar.com.uade.pds.final_project.scrim.strategy;

import ar.com.uade.pds.final_project.domain.dto.request.JoinScrimRequest;
import ar.com.uade.pds.final_project.domain.dto.request.MatchmakingRequest;
import ar.com.uade.pds.final_project.scrim.business.game.state.ScrimStateType;
import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.scrim.exception.MatchmakingException;
import ar.com.uade.pds.final_project.scrim.service.ScrimService;
import ar.com.uade.pds.final_project.users.entity.User;
import ar.com.uade.pds.final_project.users.service.DataService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static ar.com.uade.pds.final_project.scrim.constants.ErrorDescription.RANGE_MATCHMAKING_ERROR;


@Component
@AllArgsConstructor
public class RangeStrategy implements MatchMakingStrategy {

    private final ScrimService scrimService;
    private final DataService dataService;

    private static final int DEFAULT_MAX_MMR_DIFF = 300;

    @Override
    @Transactional
    public void execute(MatchmakingRequest request) {
        User currentUser = dataService.findUserWithToken();
        if (currentUser == null) {
            throw new MatchmakingException("User not authenticated");
        }

        Integer userMmr = currentUser.getMmr();
        List<Scrim> availableScrims = scrimService.findAllByStateType(ScrimStateType.SEARCHING);

        Scrim suitableScrim = availableScrims.stream()
                .filter(scrim -> {
                    Integer min = scrim.getMmrMin();
                    Integer max = scrim.getMmrMax();
                    if (min != null && max != null) {
                        return userMmr >= min && userMmr <= max;
                    }
                    Double avg = scrim.getParticipants().stream()
                            .map(User::getMmr)
                            .filter(Objects::nonNull)
                            .mapToInt(Integer::intValue)
                            .average()
                            .orElse(userMmr);

                    return Math.abs(avg - userMmr) <= DEFAULT_MAX_MMR_DIFF;
                })
                .findFirst()
                .orElseThrow(() -> new MatchmakingException(RANGE_MATCHMAKING_ERROR.getDescription()));

        scrimService.joinQueue(new JoinScrimRequest(suitableScrim.getId()));
    }
}
