package ar.com.uade.pds.final_project.users.service;

import ar.com.uade.pds.final_project.domain.dto.request.AuthenticationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.EmailVerificationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.RegisterRequest;
import ar.com.uade.pds.final_project.domain.dto.response.AuthenticationDTOResponse;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.notifications.event.NotificationType;
import ar.com.uade.pds.final_project.notifications.event.SubscribeRequest;
import ar.com.uade.pds.final_project.notifications.service.NotificationService;
import ar.com.uade.pds.final_project.security.ISecurityValidator;
import ar.com.uade.pds.final_project.users.business.SessionContext;
import ar.com.uade.pds.final_project.users.constants.UsersErrorDetails;
import ar.com.uade.pds.final_project.users.entity.Role;
import ar.com.uade.pds.final_project.users.entity.User;
import ar.com.uade.pds.final_project.users.exception.UsersException;
import ar.com.uade.pds.final_project.users.repository.IUserRepository;
import ar.com.uade.pds.final_project.users.service.impl.AuthServiceImpl;
import ar.com.uade.pds.final_project.security.exception.SecurityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private ISecurityValidator securityValidator;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private AuthenticationRequest authenticationRequest;
    private EmailVerificationRequest emailVerificationRequest;

    @BeforeEach
    void setUp() {
        SessionContext.getInstance().clearSession();
        
        testUser = new User.Builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .passwordHash("hashedPassword")
                .emailVerified(true)
                .mmr(1500)
                .region("NA")
                .preferredRoles(List.of(Role.SNIPER))
                .build();

        registerRequest = RegisterRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .region("NA")
                .preferredRole("SNIPER")
                .preferredNotificationChannel("EMAIL")
                .build();

        authenticationRequest = new AuthenticationRequest("test@example.com", "password123");
        emailVerificationRequest = new EmailVerificationRequest("test@example.com", "token123");
    }

    @Test
    void testAuthenticate_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(securityValidator.matchesPassword("password123", "hashedPassword")).thenReturn(true);
        when(securityValidator.generateToken(testUser)).thenReturn("token123");

        AuthenticationDTOResponse result = authService.authenticate(authenticationRequest);

        assertNotNull(result);
        assertEquals("token123", result.getToken());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(securityValidator, times(1)).matchesPassword("password123", "hashedPassword");
        verify(securityValidator, times(1)).generateToken(testUser);
    }

    @Test
    void testAuthenticate_UserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        UsersException exception = assertThrows(UsersException.class,
                () -> authService.authenticate(authenticationRequest));

        assertEquals(UsersErrorDetails.USER_NOT_FOUND.getMessage(), exception.getMessage());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testAuthenticate_EmailNotVerified() {
        testUser.setEmailVerified(false);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UsersException exception = assertThrows(UsersException.class,
                () -> authService.authenticate(authenticationRequest));

        assertEquals(UsersErrorDetails.EMAIL_NOT_VERIFIED.getMessage(), exception.getMessage());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testAuthenticate_InvalidPassword() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(securityValidator.matchesPassword("password123", "hashedPassword")).thenReturn(false);

        UsersException exception = assertThrows(UsersException.class,
                () -> authService.authenticate(authenticationRequest));

        assertEquals(UsersErrorDetails.INVALID_CREDENTIALS.getMessage(), exception.getMessage());
        verify(securityValidator, times(1)).matchesPassword("password123", "hashedPassword");
    }

    @Test
    void testRegister_Success() {
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(securityValidator.hashPassword("password123")).thenReturn("hashedPassword");
        when(securityValidator.generateToken(any(User.class))).thenReturn("registerToken");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        ValidationDTOResponse result = authService.register(registerRequest);

        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("registerToken", result.getData());
        verify(userRepository, times(1)).existsByEmail("newuser@example.com");
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).save(any(User.class));
        verify(notificationService, times(1)).subscribe(any(SubscribeRequest.class));
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(true);

        UsersException exception = assertThrows(UsersException.class,
                () -> authService.register(registerRequest));

        assertEquals(UsersErrorDetails.USER_EMAIL_ALREADY_EXISTS.getMessage(), exception.getMessage());
        verify(userRepository, times(1)).existsByEmail("newuser@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_UsernameAlreadyExists() {
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        UsersException exception = assertThrows(UsersException.class,
                () -> authService.register(registerRequest));

        assertEquals(UsersErrorDetails.USERNAME_ALREADY_EXISTS.getMessage(), exception.getMessage());
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_InvalidEmail() {
        registerRequest = RegisterRequest.builder()
                .username("newuser")
                .email("")
                .password("password123")
                .region("NA")
                .build();

        UsersException exception = assertThrows(UsersException.class,
                () -> authService.register(registerRequest));

        assertEquals(UsersErrorDetails.INVALID_CREDENTIALS.getMessage(), exception.getMessage());
    }

    @Test
    void testRegister_InvalidPassword() {
        registerRequest = RegisterRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("")
                .region("NA")
                .build();

        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);

        UsersException exception = assertThrows(UsersException.class,
                () -> authService.register(registerRequest));

        assertEquals(UsersErrorDetails.INVALID_CREDENTIALS.getMessage(), exception.getMessage());
    }

    @Test
    void testVerifyEmail_Success() {
        User tokenUser = new User.Builder()
                .email("test@example.com")
                .build();
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(securityValidator.getUserFromToken("token123")).thenReturn(tokenUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ValidationDTOResponse result = authService.verifyEmail(emailVerificationRequest);

        assertNotNull(result);
        assertTrue(result.isValid());
        assertTrue(testUser.isEmailVerified());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(securityValidator, times(1)).getUserFromToken("token123");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testVerifyEmail_UserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        UsersException exception = assertThrows(UsersException.class,
                () -> authService.verifyEmail(emailVerificationRequest));

        assertEquals(UsersErrorDetails.USER_NOT_FOUND.getMessage(), exception.getMessage());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testLogout_Success() {
        SessionContext.getInstance().setSession("test@example.com", "testuser", "token123");
        
        authService.logout();

        assertFalse(SessionContext.getInstance().isAuthenticated());
    }
}

