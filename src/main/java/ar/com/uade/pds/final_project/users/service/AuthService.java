package ar.com.uade.pds.final_project.users.service;

import ar.com.uade.pds.final_project.domain.dto.request.AuthenticationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.EmailVerificationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.RegisterRequest;
import ar.com.uade.pds.final_project.domain.dto.response.AuthenticationDTOResponse;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;

public interface AuthService {
    AuthenticationDTOResponse authenticate(AuthenticationRequest request);
    ValidationDTOResponse register(RegisterRequest request);
    ValidationDTOResponse verifyEmail(EmailVerificationRequest request);
}
