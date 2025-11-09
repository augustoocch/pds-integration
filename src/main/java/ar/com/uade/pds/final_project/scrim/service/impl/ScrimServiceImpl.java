package ar.com.uade.pds.final_project.scrim.service.impl;

import ar.com.uade.pds.final_project.domain.dto.request.*;
import ar.com.uade.pds.final_project.domain.dto.response.ScrimDTO;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.notifications.event.DomainEvent;
import ar.com.uade.pds.final_project.notifications.event.EventType;
import ar.com.uade.pds.final_project.notifications.service.NotificationService;
import ar.com.uade.pds.final_project.scrim.business.game.format.GameFormat;
import ar.com.uade.pds.final_project.scrim.constants.ErrorDescription;
import ar.com.uade.pds.final_project.scrim.constants.Region;
import ar.com.uade.pds.final_project.scrim.entity.PlayerStats;
import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.scrim.business.game.state.ScrimStateType;
import ar.com.uade.pds.final_project.scrim.business.game.state.Searching;
import ar.com.uade.pds.final_project.scrim.entity.ScrimParticipant;
import ar.com.uade.pds.final_project.scrim.entity.Team;
import ar.com.uade.pds.final_project.scrim.exception.ScrimException;
import ar.com.uade.pds.final_project.scrim.repository.IScrimRepository;
import ar.com.uade.pds.final_project.scrim.service.ScrimService;
import ar.com.uade.pds.final_project.scrim.service.TeamManagementService;
import ar.com.uade.pds.final_project.users.constants.UsersErrorDetails;
import ar.com.uade.pds.final_project.users.entity.Role;
import ar.com.uade.pds.final_project.users.entity.User;
import ar.com.uade.pds.final_project.users.exception.UsersException;
import ar.com.uade.pds.final_project.users.repository.IUserRepository;
import ar.com.uade.pds.final_project.users.service.DataService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class ScrimServiceImpl implements ScrimService {

    private final IScrimRepository scrimRepository;
    private final DataService dataService;
    private final NotificationService notificationService;
    private final IUserRepository userRepository;
    private final TeamManagementService teamManagementService;


    /**
     * Crea un nuevo scrim basado en la solicitud proporcionada.
     * El usuario autenticado se establece como el creador del scrim.
     * Al crear el scrim, el creador se une automáticamente a la cola del scrim y se asigna como capitan.
     *
     * @param request
     * @return
     */
    @Override
    public ValidationDTOResponse createScrim(ScrimCreationRequest request) {
        if (!dataService.checkIsAuthenticated()) {
            throw new UsersException(UsersErrorDetails.USER_NOT_AUTHENTICATED.getMessage());
        }

        GameFormat gameFormat = GameFormat.fromString(request.getFormat());
        GameValue game = GameValue.fromString(request.getGame().toLowerCase());
        GameMode mode = GameMode.fromString(request.getMode().toLowerCase());
        int playersNumber = gameFormat.getPlayersNumber();
        int estimatedDuration = gameFormat.getGameEstDuration();
        List<Role> roles = gameFormat.getAvailableRoles();

        User currentUser = dataService.findUserWithToken();
        validateUserNotInOtherScrim(currentUser.getId());
        Region region = Region.fromValue(currentUser.getRegion());

        Scrim scrim = new Scrim.Builder()
                .game(game.getValue())
                .idCreator(currentUser.getId())
                .format(gameFormat.getName())
                .players(playersNumber)
                .region(currentUser.getRegion())
                .latency(Region.latencyByRegion(region))
                .estDuration(estimatedDuration)
                .mode(mode.getValue())
                .roles(roles)
                .mmrMin(currentUser.getMmr())
                .mmrMax(currentUser.getMmr() + 200)
                .stateType(ScrimStateType.SEARCHING)
                .state(new Searching())
                .build();

        Scrim saved = scrimRepository.save(scrim);
        List<Team> teams = teamManagementService.constructTeams(saved);
        saved.addTeams(teams);
        // El creador se une a la cola
        joinQueue(new JoinScrimRequest(saved.getId()));
        DomainEvent event = getEventForScrimCreation(saved, currentUser.getId());
        notificationService.process(event);
        return new ValidationDTOResponse(true, null);
    }


    @Override
    public ValidationDTOResponse endScrim(Long id) {
        if (!dataService.checkIsAuthenticated()) {
            throw new UsersException(UsersErrorDetails.USER_NOT_AUTHENTICATED.getMessage());
        }

        Scrim scrim = scrimRepository.findById(id)
                .orElseThrow(() -> new ScrimException(ErrorDescription.SCRIM_NOT_FOUND.getDescription()));

        scrim.setCurrentState();
        scrim.end();
        for (ScrimParticipant participant : scrim.getAllParticipants()) {
            log.info("Registrando estadística para participante {}", participant.getId());

            PlayerStats stats = new PlayerStats.Builder()
                    .id(participant.getId())
                    .scrim(scrim)
                    .assignedRole(participant.getAssignedRole())
                    .game(scrim.getGame())
                    .region(scrim.getRegion())
                    .mmrBefore(dataService.findUserWithToken().getMmr())
                    .mmrAfter(calculateNewMmr(participant))
                    .score(participant.getScore())
                    .result(participant.isWinner()
                            ? PlayerStats.MatchResult.WIN
                            : PlayerStats.MatchResult.LOSE)
                    .build();

            User user = dataService.findUserById(participant.getId());
            user.addPlayerStat(stats);
            scrim.addPlayerStat(stats);
            userRepository.save(user);
        }
        scrimRepository.save(scrim);
        scrim.getDomainEvents()
                .forEach(notificationService::process);

        return new ValidationDTOResponse(true, null);
    }

    @Override
    public ValidationDTOResponse cancelScrim(Long id) {
        if (!dataService.checkIsAuthenticated()) {
            throw new UsersException(UsersErrorDetails.USER_NOT_AUTHENTICATED.getMessage());
        }
        Scrim scrim = scrimRepository.findById(id)
                .orElseThrow(() -> new ScrimException(ErrorDescription.SCRIM_NOT_FOUND.getDescription()));
        User currentUser = dataService.findUserWithToken();
        if (currentUser == null) {
            throw new ScrimException(ErrorDescription.USER_NOT_FOUND.getDescription());
        }
        scrim.setCurrentState();
        scrim.cancel(currentUser.getId());
        scrimRepository.save(scrim);
        scrim.getDomainEvents()
                .forEach(notificationService::process);
        return new ValidationDTOResponse(true, null);
    }

    @Override
    public ValidationDTOResponse confirmScrim(Long id) {
        if (!dataService.checkIsAuthenticated()) {
            throw new UsersException(UsersErrorDetails.USER_NOT_AUTHENTICATED.getMessage());
        }
        Scrim scrim = scrimRepository.findById(id)
                .orElseThrow(() -> new ScrimException(ErrorDescription.SCRIM_NOT_FOUND.getDescription()));
        User currentUser = dataService.findUserWithToken();
        if (currentUser == null) {
            throw new ScrimException(ErrorDescription.USER_NOT_FOUND.getDescription());
        }
        scrim.setCurrentState();
        scrim.confirm(currentUser);
        scrimRepository.save(scrim);
        scrim.getDomainEvents()
                .forEach(notificationService::process);
        return new ValidationDTOResponse(true, null);
    }

    @Override
    public List<ScrimDTO> searchScrims(SearchRequest request) {
        List<Scrim> scrims = scrimRepository.findByFilters(
                request.getGame(),
                request.getRegion(),
                request.getFormat()
        );

        if (scrims.isEmpty()) {
            System.out.println("No se encontraron scrims con los filtros proporcionados.");
            List<ScrimDTO> others = this.searchAvailableScrims();
            if (others.isEmpty()) {
                throw new ScrimException(ErrorDescription.NOT_AVAILABLE_SCRIMS.getDescription());
            }
            System.out.println("Sin embargo, Se encontraron scrims disponibles sin filtros");
            return others;
        }

        return scrims.stream()
                .map(scrim -> new ScrimDTO.Builder()
                        .id(scrim.getId())
                        .game(scrim.getGame())
                        .format(scrim.getFormat())
                        .latency(scrim.getLatency())
                        .region(scrim.getRegion())
                        .mode(scrim.getMode())
                        .players(scrim.getPlayers())
                        .roles(scrim.getRoles().stream().map(Enum::name).collect(Collectors.toList()))
                        .estDuration(scrim.getEstDuration())
                        .state(scrim.getStateType().name())
                        .build()
                ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ValidationDTOResponse joinQueue(JoinScrimRequest request) {
        if (!dataService.checkIsAuthenticated()) {
            throw new UsersException(UsersErrorDetails.USER_NOT_AUTHENTICATED.getMessage());
        }

        Scrim scrim = scrimRepository.findById(request.getIdScrim())
                .orElseThrow(() -> new ScrimException(ErrorDescription.SCRIM_NOT_FOUND.getDescription()));

        User currentUser = dataService.findUserWithToken();
        if (currentUser == null) {
            throw new ScrimException(ErrorDescription.USER_NOT_FOUND.getDescription());
        }

        validateUserNotInOtherScrim(currentUser.getId());
        validateJoinableScrim(scrim, currentUser.getId());
        System.out.println("Scrim válido para unirse — agregando participante...");
        Team assignedTeam = teamManagementService.selectTeam(scrim);
        System.out.println("Equipo asignado: " + assignedTeam.getName());

        boolean captain = Objects.equals(currentUser.getId(), scrim.getIdCreator());

        ScrimParticipant participant = new ScrimParticipant.Builder()
                .setUser(currentUser)
                .setCaptain(captain)
                .setAssignedRole(currentUser.getPreferredRoles().get(0))
                .setConfirmed(false)
                .setTeam(assignedTeam)
                .build();

        assignedTeam.addParticipant(participant);

        if (scrim.isFull()) {
            scrim.setStateType(ScrimStateType.LOBBY);
            System.out.println("Scrim lleno — cambiando estado a LOBBY...");
        }

        scrimRepository.save(scrim);
        return new ValidationDTOResponse(true, null);
    }


    @Override
    public List<ScrimDTO> searchAvailableScrims() {
        List<Scrim> scrim = scrimRepository.findAllByStateType(ScrimStateType.SEARCHING);
        if (scrim.isEmpty()) {
            throw new ScrimException(ErrorDescription.NOT_AVAILABLE_SCRIMS.getDescription());
        }
        return scrim.stream()
                .map(s -> new ScrimDTO.Builder()
                        .id(s.getId())
                        .game(s.getGame())
                        .format(s.getFormat())
                        .latency(s.getLatency())
                        .region(s.getRegion())
                        .mode(s.getMode())
                        .players(s.getPlayers())
                        .roles(s.getRoles().stream().map(Enum::name).collect(Collectors.toList()))
                        .estDuration(s.getEstDuration())
                        .state(s.getStateType().name())
                        .build()
                ).collect(Collectors.toList());
    }

    @Override
    public List<Scrim> findAllByStateType(ScrimStateType stateType) {
        try {
            return scrimRepository.findAllByStateType(stateType);
        } catch (IllegalArgumentException e) {
            throw new ScrimException(ErrorDescription.INVALID_SCRIM_STATE.getDescription());
        }
    }

    @Override
    public List<User> getUsersFromParticipants(List<ScrimParticipant> participants) {
        return participants.stream()
                .map(participant -> dataService.findUserById(participant.getId()))
                .collect(Collectors.toList());
    }

    private void validateJoinableScrim(Scrim scrim, Long userId) {
        if (scrim == null) {
            throw new ScrimException(ErrorDescription.SCRIM_NOT_FOUND.getDescription());
        }
        if (!scrim.hasValidStateToJoin()) {
            throw new ScrimException(ErrorDescription.INVALID_SCRIM_STATE.getDescription());
        }
        if (scrim.isFull()) {
            throw new ScrimException(ErrorDescription.SCRIM_FULL.getDescription());
        }
        scrim.getAllParticipants().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .ifPresent(user -> {
                    throw new ScrimException(ErrorDescription.USER_ALREADY_IN_SCRIM
                            .getDescription());
                });
    }

    private DomainEvent getEventForScrimCreation(Scrim scrim, Long userId) {
        return new DomainEvent(
                EventType.SCRIM_CREATED,
                userId,
                List.of(),
                scrim.getId()
        );
    }

    private Integer calculateNewMmr(ScrimParticipant participant) {
        Integer currentMmr = dataService.findUserById(participant.getId()).getMmr();
        int mmrChange = participant.isWinner()
                ? 25
                : -15;
        return currentMmr + mmrChange;
    }


    private void validateUserNotInOtherScrim(Long userId) {
        List<Scrim> activeScrims = scrimRepository.findAllWithActiveStates();
        for (Scrim scrim : activeScrims) {
            boolean userInScrim = scrim.participantInOtherScrim(userId);
            if (userInScrim) {
                throw new ScrimException(ErrorDescription.USER_ALREADY_IN_OTHER_SCRIM.getDescription());
            }
        }
    }
}
