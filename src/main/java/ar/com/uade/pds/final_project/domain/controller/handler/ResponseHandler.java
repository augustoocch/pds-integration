package ar.com.uade.pds.final_project.domain.controller.handler;

import ar.com.uade.pds.final_project.domain.dto.response.ExceptionDTO;
import ar.com.uade.pds.final_project.domain.dto.response.ResponseWrapper;
import ar.com.uade.pds.final_project.scrim.exception.ScrimException;
import ar.com.uade.pds.final_project.security.exception.SecurityException;
import ar.com.uade.pds.final_project.users.exception.UsersException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
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
        ResponseWrapper response = handleKnownErrors(ex);
        if (response == null) {
            ExceptionDTO errorResponse = new ExceptionDTO(
                    "Ocurrio un error inesperado: " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return buildResponse(errorResponse.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR, false, errorResponse);
        }
        return response;
    }

    private static ResponseWrapper handleKnownErrors(Exception ex) {
        if (ex instanceof SecurityException) {
            log.error("SecurityException: {}", ex.getMessage());
            ExceptionDTO errorResponse = new ExceptionDTO(
                    ex.getMessage(),
                    HttpStatus.UNAUTHORIZED.value()
            );
            return buildResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, false, errorResponse);
        } else if (ex instanceof ScrimException) {
            log.error("ScrimException: {}", ex.getMessage());
            ExceptionDTO errorResponse = new ExceptionDTO(
                    ex.getMessage(),
                    HttpStatus.BAD_REQUEST.value()
            );
            return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, false, errorResponse);
        } else if (ex instanceof UsersException) {
            log.error("UsersException: {}", ex.getMessage());
            ExceptionDTO errorResponse = new ExceptionDTO(
                    ex.getMessage(),
                    HttpStatus.BAD_REQUEST.value()
            );
            return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, false, errorResponse);
        }
        return null;
    }
}
