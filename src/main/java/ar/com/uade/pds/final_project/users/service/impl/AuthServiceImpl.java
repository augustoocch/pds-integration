package ar.com.uade.pds.final_project.users.service.impl;

import ar.com.uade.pds.final_project.domain.dto.request.AuthenticationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.EmailVerificationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.RegisterRequest;
import ar.com.uade.pds.final_project.domain.dto.response.AuthenticationDTOResponse;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.scrim.constants.Region;
import ar.com.uade.pds.final_project.security.ISecurityValidator;
import ar.com.uade.pds.final_project.users.Business.SessionContext;
import ar.com.uade.pds.final_project.users.constants.UsersErrorDetails;
import ar.com.uade.pds.final_project.users.entity.User;
import ar.com.uade.pds.final_project.users.exception.UsersException;
import ar.com.uade.pds.final_project.users.repository.IUserRepository;
import ar.com.uade.pds.final_project.users.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final IUserRepository userRepository;
    private final ISecurityValidator securityValidator;
    private final SessionContext sessionContext;

    @Override
    public AuthenticationDTOResponse authenticate(AuthenticationRequest request) {
        User user = userRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new UsersException(UsersErrorDetails.USER_NOT_FOUND.getMessage()));
        if(!user.isEmailVerified()) {
            throw new UsersException(UsersErrorDetails.EMAIL_NOT_VERIFIED.getMessage());
        }
        if (!securityValidator.matchesPassword(request.getPassword(), user.getPasswordHash())) {
            throw new UsersException(UsersErrorDetails.INVALID_CREDENTIALS.getMessage());
        }
        String token = securityValidator.generateToken(user);
        sessionContext.setSession(user.getEmail(), user.getUsername(), token);
        return new AuthenticationDTOResponse(token, user.getUsername(), user.getEmail());
    }

    @Override
    public ValidationDTOResponse register(RegisterRequest request) {
        validateRegisterRequest(request);
        String hashedPassword = securityValidator.hashPassword(request.getPassword());

        Region region = Region.fromValue(request.getRegion());
        User user = new User.Builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .passwordHash(hashedPassword)
                .region(region.name())
                .emailVerified(false)
                .build();

        String registerToken = securityValidator.generateToken(user);
        userRepository.save(user);
        return new ValidationDTOResponse(true, registerToken);
    }

    @Override
    public ValidationDTOResponse verifyEmail(EmailVerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsersException(UsersErrorDetails.USER_NOT_FOUND.getMessage()));
        User tokenUser = securityValidator.getUserFromToken(request.getToken());
        if (!tokenUser.getEmail().equals(user.getEmail())) {
            throw new SecurityException(UsersErrorDetails.INVALID_CREDENTIALS.getMessage());
        }
        user.verifyEmail();
        userRepository.save(user);
        return new ValidationDTOResponse(true, null);
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UsersException(UsersErrorDetails.USER_EMAIL_ALREADY_EXISTS.getMessage());
        }
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new UsersException(UsersErrorDetails.USERNAME_ALREADY_EXISTS.getMessage());
        }
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new UsersException(UsersErrorDetails.INVALID_CREDENTIALS.getMessage());
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new UsersException(UsersErrorDetails.INVALID_CREDENTIALS.getMessage());
        }
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new UsersException(UsersErrorDetails.INVALID_CREDENTIALS.getMessage());
        }
    }
}