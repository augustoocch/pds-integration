package ar.com.uade.pds.final_project.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ValidationDTOResponse {
    private boolean valid;
    private Object data;

    public ValidationDTOResponse(boolean isValid, Object data) {
        this.data = data;
        this.valid = isValid;
    }
}
