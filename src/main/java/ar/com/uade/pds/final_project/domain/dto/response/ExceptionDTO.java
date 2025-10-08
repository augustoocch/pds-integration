package ar.com.uade.pds.final_project.domain.dto.response;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExceptionDTO {
    private int code;
    private String dateTime;
    private String message;

    public ExceptionDTO(String message, int code) {
        this.dateTime = LocalDateTime.now().toString();
        this.code = code;
        this.message = message;
    }
}