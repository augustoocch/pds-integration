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
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
public class CompatibilityStrategy implements MatchMakingStrategy {

    private final ScrimService scrimService;
    private final DataService dataService;

    private static final int MMR_TOLERANCE = 300;

    @Override
    @Transactional
    public void execute(MatchmakingRequest request) {
        User currentUser = dataService.findUserWithToken();
        if (currentUser == null) {
            throw new UsersException(UsersErrorDetails.USER_NOT_AUTHENTICATED.getMessage());
        }

        List<Scrim> availableScrims = scrimService.findAllByStateType(ScrimStateType.SEARCHING);

        Scrim compatibleScrim = availableScrims.stream()
                .max(Comparator.comparingDouble(scrim -> calculateCompatibilityScore(currentUser, scrim)))
                .orElseThrow(() -> new MatchmakingException("No compatible scrim found"));

        scrimService.joinQueue(new JoinScrimRequest(compatibleScrim.getId()));
    }

    private double calculateCompatibilityScore(User user, Scrim scrim) {
        double score = 0.0;
        Set<User> participants = scrim.getParticipants();

        // Preferencia de rol (evita duplicar)
        boolean roleConflict = participants.stream()
                .anyMatch(p -> Objects.equals(p.getPreferredRoles(), user.getPreferredRoles()));
        if (!roleConflict) score += 0.4;

        // Región similar
        long sameRegionCount = participants.stream()
                .filter(p -> Objects.equals(p.getRegion(), user.getRegion()))
                .count();
        score += 0.1 * sameRegionCount;

        // MMR promedio compatible
        double avgMmr = participants.stream()
                .map(User::getMmr)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(user.getMmr());
        if (Math.abs(avgMmr - user.getMmr()) <= MMR_TOLERANCE)
            score += 0.3;

        // Latencia promedio (si existe)
        double avgLatency = participants.stream()
                .map(User::getLatency)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(100);
        score += Math.max(0, (180 - avgLatency) / 180.0 * 0.2); // penaliza scrims lentos

        return score; // va de 0 a 1.0
    }
}

