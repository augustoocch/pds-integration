package ar.com.uade.pds.final_project.domain.dto.request;

import ar.com.uade.pds.final_project.users.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScrimCreationRequest {
    private String game;
    private String format;
    private int players;
    private List<Role> roles;
    private String region;
    private String latency;
    private int estDuration;
    private String modal;
}
