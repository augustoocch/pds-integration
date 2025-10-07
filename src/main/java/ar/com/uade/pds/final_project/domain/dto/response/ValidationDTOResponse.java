package ar.com.uade.pds.final_project.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ValidationDTOResponse {
    private boolean valid;

    public ValidationDTOResponse(boolean isValid) {
        this.valid = isValid;
    }
}
