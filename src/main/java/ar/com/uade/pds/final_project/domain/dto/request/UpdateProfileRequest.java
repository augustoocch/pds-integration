package ar.com.uade.pds.final_project.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateProfileRequest {
    private String username;
    private String email;
    private String region;
}
