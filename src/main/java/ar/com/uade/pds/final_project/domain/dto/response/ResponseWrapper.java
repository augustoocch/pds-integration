package ar.com.uade.pds.final_project.domain.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResponseWrapper {
    private boolean success;
    private int status;
    private String message;
    private Object data;
}
