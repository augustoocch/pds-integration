package ar.com.uade.pds.final_project.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScrimCreationRequest {
    private String game;
    private String format;
    private String mode;
}
