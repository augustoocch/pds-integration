package ar.com.uade.pds.final_project.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String region;
}
