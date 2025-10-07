package ar.com.uade.pds.final_project.domain.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionDTO {

    private String dateTime;
    private int code;

    public ExceptionDTO(String message, int code) {
        this.dateTime = message;
        this.code = code;
    }
}