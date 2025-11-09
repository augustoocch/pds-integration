package ar.com.uade.pds.final_project.scrim.strategy;


import static ar.com.uade.pds.final_project.scrim.constants.ErrorDescription.RANGE_MATCHMAKING_ERROR;

import ar.com.uade.pds.final_project.domain.dto.request.JoinScrimRequest;
import ar.com.uade.pds.final_project.domain.dto.request.MatchmakingRequest;
import ar.com.uade.pds.final_project.scrim.business.game.state.ScrimStateType;
import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.scrim.entity.ScrimParticipant;
import ar.com.uade.pds.final_project.scrim.exception.MatchmakingException;
import ar.com.uade.pds.final_project.scrim.service.ScrimService;
import ar.com.uade.pds.final_project.users.entity.User;
import ar.com.uade.pds.final_project.users.service.DataService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class RangeStrategy implements MatchMakingStrategy {

    private final ScrimService scrimService;
    private final DataService dataService;

    private static final int DEFAULT_MAX_MMR_DIFF = 300;

    /**
     * RangeStrategy matches users to scrims based on MMR range.
     * MMR it means Match Making Rating, a skill rating system.
     * @param request
     */
    @Override
    @Transactional
    public void execute(MatchmakingRequest request) {
        User currentUser = dataService.findUserWithToken();
        if (currentUser == null) {
            throw new MatchmakingException("User not authenticated");
        }

        Integer userMmr = currentUser.getMmr();
        if (userMmr == null) {
            throw new MatchmakingException("User MMR unknown");
        }
        log.info("Executing RangeStrategy for user ID: " + currentUser.getId() + " with MMR: " + userMmr);
        List<Scrim> availableScrims = Optional.ofNullable(
                scrimService.findAllByStateType(ScrimStateType.SEARCHING)
        ).orElse(Collections.emptyList());

        Scrim suitableScrim = availableScrims.stream()
                .filter(scrim -> {
                    Integer min = scrim.getMmrMin();
                    Integer max = scrim.getMmrMax();
                    if (min != null && max != null) {
                        return userMmr >= min && userMmr <= max;
                    }

                    // compute average ignoring participants with null MMR
                    double avg = scrim.getAllParticipants().stream()
                            .map(ScrimParticipant::getMmr)
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