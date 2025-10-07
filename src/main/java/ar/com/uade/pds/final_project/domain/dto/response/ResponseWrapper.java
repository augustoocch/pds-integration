package ar.com.uade.pds.final_project.domain.dto.response;

import lombok.Builder;

@Builder
public class ResponseWrapper {
    private boolean success;
    private int status;
    private String message;
    private Object data;
}
