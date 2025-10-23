package ar.com.uade.pds.final_project.domain.dto.response;

import ar.com.uade.pds.final_project.users.entity.Role;
import lombok.Getter;

import java.util.List;

@Getter
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private List<Role> preferredRoles;
    private String region;
    private String preference;

    // Constructor privado para Builder
    private UserDTO(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.email = builder.email;
        this.preferredRoles = builder.preferredRoles;
        this.region = builder.region;
        this.preference = builder.preference;
    }

    // Builder manual
    public static class Builder {
        private Long id;
        private String username;
        private String email;
        private List<Role> preferredRoles;
        private String region;
        private String preference;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder preferredRoles(List<Role> preferredRoles) { this.preferredRoles = preferredRoles; return this; }
        public Builder region(String region) { this.region = region; return this; }
        public Builder preference(String preference) { this.preference = preference; return this; }

        public UserDTO build() {
            return new UserDTO(this);
        }
    }
}

