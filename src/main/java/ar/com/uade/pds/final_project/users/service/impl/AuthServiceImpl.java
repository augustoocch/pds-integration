package ar.com.uade.pds.final_project.users.service.impl;

import ar.com.uade.pds.final_project.domain.dto.request.AuthenticationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.EmailVerificationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.RegisterRequest;
import ar.com.uade.pds.final_project.domain.dto.response.AuthenticationDTOResponse;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.scrim.service.ScrimService;
import ar.com.uade.pds.final_project.users.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public AuthenticationDTOResponse authenticate(AuthenticationRequest request) {
        return null;
    }

    @Override
    public ValidationDTOResponse register(RegisterRequest request) {
        return null;
    }

    @Override
    public ValidationDTOResponse verifyEmail(EmailVerificationRequest request) {
        return null;
    }
}
