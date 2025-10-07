package ar.com.uade.pds.final_project.domain.controller.handler;

import ar.com.uade.pds.final_project.domain.dto.response.ExceptionDTO;
import ar.com.uade.pds.final_project.domain.dto.response.ResponseWrapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public class ResponseHandler {

    public static ResponseWrapper buildResponse(String message, HttpStatus status,
                                                boolean success, Object data) {
        return ResponseWrapper.builder()
                .message(message)
                .status(status.value())
                .success(success)
                .data(data)
                .build();
    }

    public static ResponseWrapper handleError(Exception ex) {
        ExceptionDTO errorResponse = new ExceptionDTO(
                "An unexpected error occurred: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseWrapper.builder()
                .data(errorResponse)
                .message("An unexpected error occurred")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .success(false)
                .build();
    }
}
