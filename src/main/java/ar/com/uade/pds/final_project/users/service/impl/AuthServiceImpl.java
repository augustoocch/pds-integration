package ar.com.uade.pds.final_project.users.service.impl;

import ar.com.uade.pds.final_project.domain.dto.request.AuthenticationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.EmailVerificationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.RegisterRequest;
import ar.com.uade.pds.final_project.domain.dto.response.AuthenticationDTOResponse;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.security.ISecurityValidator;
import ar.com.uade.pds.final_project.users.entity.User;
import ar.com.uade.pds.final_project.users.exception.UsersException;
import ar.com.uade.pds.final_project.users.repository.IUserRepository;
import ar.com.uade.pds.final_project.users.service.AuthService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final IUserRepository userRepository;
    private final ISecurityValidator securityValidator;

    @Override
    public AuthenticationDTOResponse authenticate(AuthenticationRequest request) {
        User user = userRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new UsersException("Invalid email"));
        if (!securityValidator.matchesPassword(request.getPassword(), user.getPasswordHash())) {
            throw new UsersException("Invalid email or password");
        }
        String token = securityValidator.generateToken(user);
        return new AuthenticationDTOResponse(token, user.getUsername(), user.getEmail());
    }

    @Override
    public ValidationDTOResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return new ValidationDTOResponse(false);
        }

        validateRegisterRequest(request);
        String hashedPassword = securityValidator.hashPassword(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .passwordHash(hashedPassword)
                .emailVerified(false)
                .build();

        userRepository.save(user);
        return new ValidationDTOResponse(true);
    }

    @Override
    public ValidationDTOResponse verifyEmail(EmailVerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsersException("User not found"));
        User tokenUser = securityValidator.getUserFromToken(request.getToken());
        if (!tokenUser.getEmail().equals(user.getEmail())) {
            throw new SecurityException("Invalid verification token");
        }
        user.verifyEmail();
        userRepository.save(user);
        return new ValidationDTOResponse(true);
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new UsersException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new UsersException("Password is required");
        }
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new UsersException("Username is required");
        }
    }
}