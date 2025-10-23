package ar.com.uade.pds.final_project.scrim.service.impl;

import ar.com.uade.pds.final_project.domain.dto.request.*;
import ar.com.uade.pds.final_project.domain.dto.response.ScrimDTO;
import ar.com.uade.pds.final_project.domain.dto.response.UserDTO;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.scrim.business.game.state.Lobby;
import ar.com.uade.pds.final_project.scrim.business.game.format.GameFormat;
import ar.com.uade.pds.final_project.scrim.constants.ErrorDescription;
import ar.com.uade.pds.final_project.scrim.constants.Region;
import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.scrim.business.game.state.ScrimStateType;
import ar.com.uade.pds.final_project.scrim.business.game.state.Searching;
import ar.com.uade.pds.final_project.scrim.exception.ScrimException;
import ar.com.uade.pds.final_project.scrim.repository.IScrimRepository;
import ar.com.uade.pds.final_project.scrim.service.ScrimService;
import ar.com.uade.pds.final_project.users.constants.UsersErrorDetails;
import ar.com.uade.pds.final_project.users.entity.Role;
import ar.com.uade.pds.final_project.users.entity.User;
import ar.com.uade.pds.final_project.users.exception.UsersException;
import ar.com.uade.pds.final_project.users.service.DataService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ScrimServiceImpl implements ScrimService {

    private final IScrimRepository scrimRepository;
    private final DataService dataService;

    @Override
    public ValidationDTOResponse createScrim(ScrimCreationRequest request) {

        if(dataService.checkIsAuthenticated()) {
            throw new UsersException(UsersErrorDetails.USER_NOT_AUTHENTICATED.getMessage());
        }
        GameFormat gameFormat = GameFormat.fromString(request.getFormat());
        GameValue game = GameValue.fromString(request.getGame().toLowerCase());
        GameMode mode = GameMode.fromString(request.getMode().toLowerCase());
        int playersNumber = gameFormat.getPlayersNumber();
        int estimatedDuration = gameFormat.getGameEstDuration();
        List<Role> roles = gameFormat.getAvailableRoles();

        UserDTO currentUser = dataService.findDTOUserWithToken();
        Region region = Region.fromValue(currentUser.getRegion());

        Scrim scrim = new Scrim.Builder()
                .game(game.getValue())
                .format(gameFormat.getName())
                .players(playersNumber)
                .region(currentUser.getRegion())
                .latency(Region.latencyByRegion(region))
                .estDuration(estimatedDuration)
                .mode(mode.getValue())
                .roles(roles)
                .stateType(ScrimStateType.SEARCHING)
                .state(new Searching())
                .build();

        scrimRepository.save(scrim);
        return new ValidationDTOResponse(true, null);
    }

    @Override
    public ValidationDTOResponse endScrim(Long id) {
        Scrim scrim = scrimRepository.findById(id).orElse(null);
        if (scrim == null) {
            throw new ScrimException(ErrorDescription.SCRIM_NOT_FOUND.getDescription());
        }
        try {
            scrim.end();
            scrimRepository.save(scrim);
            return new ValidationDTOResponse(true, null);
        } catch (Exception e) {
            throw new ScrimException(ErrorDescription.SCRIM_CANNOT_CHANGE_STATE.getDescription());
        }
    }

    @Override
    public ValidationDTOResponse cancelScrim(Long id) {
        Scrim scrim = scrimRepository.findById(id).orElse(null);
        if (scrim == null) {
            throw new ScrimException(
                    ErrorDescription.SCRIM_NOT_FOUND.getDescription()
            );
        }

        try {
            scrim.cancel();
            scrimRepository.save(scrim);
            return new ValidationDTOResponse(true, null);
        } catch (Exception e) {
            throw new ScrimException(
                    ErrorDescription.SCRIM_CANNOT_CHANGE_STATE.getDescription()
            );
        }
    }

    @Override
    public ValidationDTOResponse confirmScrim(Long id) {
        Scrim scrim = scrimRepository.findById(id).orElse(null);
        if (scrim == null) {
            throw new ScrimException(ErrorDescription.SCRIM_NOT_FOUND.getDescription());
        }
        try {
            scrim.confirm();
            scrimRepository.save(scrim);
            return new ValidationDTOResponse(true, null);
        } catch (Exception e) {
            throw new ScrimException(ErrorDescription.SCRIM_CANNOT_CHANGE_STATE.getDescription());
        }
    }

    @Override
    public List<ScrimDTO> searchScrim(SearchRequest request) {
        List<Scrim> scrims = scrimRepository.findByFilters(
                request.getGame(),
                request.getRegion(),
                request.getFormat()
        );

        return scrims.stream()
                .map(scrim -> new ScrimDTO.Builder()
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
    public ValidationDTOResponse joinQueue(JoinScrimRequest request) {
        Scrim scrim = scrimRepository.findById(request.getIdScrim())
                .orElse(null);

        validateJoinableScrim(scrim);
        User currentUser = dataService.findUser(request.getUserId());
        if(currentUser == null) {
            throw new ScrimException(ErrorDescription.USER_NOT_FOUND.getDescription());
        }
        scrim.addParticipant(currentUser);
        if (scrim.isFull()) {
            scrim.setState(new Lobby());
        }
        scrimRepository.save(scrim);
        return new ValidationDTOResponse(true, null);
    }

    @Override
    public List<ScrimDTO> searchAvailableScrims() {
        List<Scrim> scrim = scrimRepository.findFirstByStateType(ScrimStateType.SEARCHING.name());
        if (scrim.isEmpty()) {
            throw new ScrimException(ErrorDescription.NOT_AVAILABLE_SCRIMS.getDescription());
        }
        return scrim.stream()
                .map(s -> new ScrimDTO.Builder()
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

    private void validateJoinableScrim(Scrim scrim) {
        if (scrim == null) {
            throw new ScrimException(ErrorDescription.SCRIM_NOT_FOUND.getDescription());
        }
        if (!scrim.hasValidStateToJoin()) {
            throw new ScrimException(ErrorDescription.INVALID_SCRIM_STATE.getDescription());
        }
        if (scrim.isFull()) {
            throw new ScrimException(ErrorDescription.SCRIM_FULL.getDescription());
        }
    }
}
