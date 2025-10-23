package ar.com.uade.pds.final_project.domain.controller;

import ar.com.uade.pds.final_project.domain.dto.response.ResponseWrapper;
import ar.com.uade.pds.final_project.domain.dto.request.AuthenticationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.EmailVerificationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.RegisterRequest;
import ar.com.uade.pds.final_project.domain.dto.response.AuthenticationDTOResponse;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.users.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import static ar.com.uade.pds.final_project.domain.controller.handler.ResponseHandler.buildResponse;
import static ar.com.uade.pds.final_project.domain.controller.handler.ResponseHandler.handleError;

@Controller
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    public ResponseWrapper register(RegisterRequest request) {
        try {
            ValidationDTOResponse response = authService.register(request);
            if (!response.isValid()) {
                return buildResponse("Registration failed", HttpStatus.CONFLICT, false, response);
            }
            return buildResponse("Registration successful", HttpStatus.OK, true, response);
        } catch (Exception e) {
            return handleError(e);
        }
    }

    public ResponseWrapper authenticate(AuthenticationRequest request) {
        try {
            AuthenticationDTOResponse response = authService.authenticate(request);
            if (response == null) {
                return buildResponse("Authentication failed", HttpStatus.UNAUTHORIZED, false, null);
            }
            return buildResponse("Authentication successful", HttpStatus.OK, true, response.getToken());
        } catch (Exception e) {
            return handleError(e);
        }
    }

    public ResponseWrapper validateToken(EmailVerificationRequest request) {
        try {
            boolean isValid = authService.verifyEmail(request).isValid();
            ValidationDTOResponse response = new ValidationDTOResponse(isValid, null);
            return buildResponse("Token is valid", HttpStatus.OK, true, response);
        } catch (Exception e) {
            return handleError(e);
        }
    }
}
