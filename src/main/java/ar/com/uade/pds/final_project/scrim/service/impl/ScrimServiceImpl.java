package ar.com.uade.pds.final_project.scrim.service.impl;

import ar.com.uade.pds.final_project.domain.dto.request.JoinScrimRequest;
import ar.com.uade.pds.final_project.domain.dto.request.ScrimCreationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.SearchRequest;
import ar.com.uade.pds.final_project.domain.dto.response.ScrimDTO;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.scrim.business.Lobby;
import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.scrim.business.ScrimStateType;
import ar.com.uade.pds.final_project.scrim.business.Searching;
import ar.com.uade.pds.final_project.scrim.repository.IScrimRepository;
import ar.com.uade.pds.final_project.scrim.service.ScrimService;
import ar.com.uade.pds.final_project.users.entity.User;
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
        Scrim scrim = Scrim.builder()
                .game(request.getGame())
                .format(request.getFormat())
                .players(request.getPlayers())
                .region(request.getRegion())
                .latency(request.getLatency())
                .estDuration(request.getEstDuration())
                .modal(request.getModal())
                .roles(request.getRoles())
                .stateType(ScrimStateType.SEARCHING)
                .state(new Searching())
                .build();

        scrimRepository.save(scrim);
        return new ValidationDTOResponse(true);
    }

    @Override
    public ValidationDTOResponse endScrim(Long id) {
        Scrim scrim = scrimRepository.findById(id).orElse(null);
        if (scrim == null)
            return new ValidationDTOResponse(true);

        try {
            scrim.end();
            scrimRepository.save(scrim);
            return new ValidationDTOResponse(true);
        } catch (Exception e) {
            return new ValidationDTOResponse(false);
        }
    }

    @Override
    public ValidationDTOResponse cancelScrim(Long id) {
        Scrim scrim = scrimRepository.findById(id).orElse(null);
        if (scrim == null)
            return new ValidationDTOResponse(false);

        try {
            scrim.cancel();
            scrimRepository.save(scrim);
            return new ValidationDTOResponse(true);
        } catch (Exception e) {
            return new ValidationDTOResponse(false);
        }
    }

    @Override
    public ValidationDTOResponse confirmScrim(Long id) {
        Scrim scrim = scrimRepository.findById(id).orElse(null);
        if (scrim == null)
            return new ValidationDTOResponse(false);
        try {
            scrim.confirm();
            scrimRepository.save(scrim);
            return new ValidationDTOResponse(true);
        } catch (Exception e) {
            return new ValidationDTOResponse(false);
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
                .map(scrim -> ScrimDTO.builder()
                        .game(scrim.getGame())
                        .format(scrim.getFormat())
                        .latency(scrim.getLatency())
                        .region(scrim.getRegion())
                        .modal(scrim.getModal())
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
        if (scrim == null) {
            return new ValidationDTOResponse(false);
        }

        if (!scrim.hasValidStateToJoin()) {
            return new ValidationDTOResponse(false);
        }

        User currentUser = dataService.findUser(request.getUserId());
        if(currentUser == null) {
            return new ValidationDTOResponse(false);
        }

        if (scrim.isFull()) {
            return new ValidationDTOResponse(false);
        }
        scrim.addParticipant(currentUser);
        if (scrim.isFull()) {
            scrim.setState(new Lobby());
        }
        scrimRepository.save(scrim);
        return new ValidationDTOResponse(true);
    }
}
