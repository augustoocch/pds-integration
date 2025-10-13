package ar.com.uade.pds.final_project.domain.dto.response;

import ar.com.uade.pds.final_project.users.entity.Role;
import lombok.Builder;

import java.util.List;

@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private List<Role> preferredRoles;
    private String region;
    private String preference;
}
