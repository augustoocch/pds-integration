package ar.com.uade.pds.final_project.domain.controller;

import ar.com.uade.pds.final_project.domain.dto.request.AuthenticationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.EmailVerificationRequest;
import ar.com.uade.pds.final_project.domain.dto.request.RegisterRequest;
import ar.com.uade.pds.final_project.domain.dto.response.AuthenticationDTOResponse;
import ar.com.uade.pds.final_project.domain.dto.response.ResponseWrapper;
import ar.com.uade.pds.final_project.domain.dto.response.ValidationDTOResponse;
import ar.com.uade.pds.final_project.users.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private AuthenticationRequest authenticationRequest;
    private EmailVerificationRequest emailVerificationRequest;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .region("NA")
                .preferredRole("SNIPER")
                .preferredNotificationChannel("EMAIL")
                .build();

        authenticationRequest = new AuthenticationRequest("testuser", "password123");
        emailVerificationRequest = new EmailVerificationRequest("test@example.com", "token123");
    }

    @Test
    void testRegister_Success() {
        ValidationDTOResponse response = new ValidationDTOResponse(true, "User registered successfully");
        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        ResponseWrapper result = authController.register(registerRequest);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatus());
        assertEquals("Registration successful", result.getMessage());
        verify(authService, times(1)).register(registerRequest);
    }

    @Test
    void testRegister_Failure() {
        ValidationDTOResponse response = new ValidationDTOResponse(false, "Email already exists");
        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        ResponseWrapper result = authController.register(registerRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(409, result.getStatus());
        assertEquals("Registration failed", result.getMessage());
        verify(authService, times(1)).register(registerRequest);
    }

    @Test
    void testRegister_Exception() {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Database error"));

        ResponseWrapper result = authController.register(registerRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(500, result.getStatus());
        verify(authService, times(1)).register(registerRequest);
    }

    @Test
    void testAuthenticate_Success() {
        AuthenticationDTOResponse response = new AuthenticationDTOResponse("token123", "testuser", "test@example.com");
        when(authService.authenticate(any(AuthenticationRequest.class))).thenReturn(response);

        ResponseWrapper result = authController.authenticate(authenticationRequest);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatus());
        assertEquals("Authentication successful", result.getMessage());
        assertEquals("token123", result.getData());
        verify(authService, times(1)).authenticate(authenticationRequest);
    }

    @Test
    void testAuthenticate_NullResponse() {
        when(authService.authenticate(any(AuthenticationRequest.class))).thenReturn(null);

        ResponseWrapper result = authController.authenticate(authenticationRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getStatus());
        assertEquals("Authentication failed", result.getMessage());
        verify(authService, times(1)).authenticate(authenticationRequest);
    }

    @Test
    void testAuthenticate_Exception() {
        when(authService.authenticate(any(AuthenticationRequest.class)))
                .thenThrow(new RuntimeException("Authentication error"));

        ResponseWrapper result = authController.authenticate(authenticationRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(500, result.getStatus());
        verify(authService, times(1)).authenticate(authenticationRequest);
    }

    @Test
    void testValidateToken_Success() {
        ValidationDTOResponse response = new ValidationDTOResponse(true, null);
        when(authService.verifyEmail(any(EmailVerificationRequest.class))).thenReturn(response);

        ResponseWrapper result = authController.validateToken(emailVerificationRequest);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatus());
        assertEquals("Token is valid", result.getMessage());
        verify(authService, times(1)).verifyEmail(emailVerificationRequest);
    }

    @Test
    void testValidateToken_InvalidToken() {
        ValidationDTOResponse response = new ValidationDTOResponse(false, null);
        when(authService.verifyEmail(any(EmailVerificationRequest.class))).thenReturn(response);

        ResponseWrapper result = authController.validateToken(emailVerificationRequest);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatus());
        assertEquals("Token is valid", result.getMessage());
        verify(authService, times(1)).verifyEmail(emailVerificationRequest);
    }

    @Test
    void testValidateToken_Exception() {
        when(authService.verifyEmail(any(EmailVerificationRequest.class)))
                .thenThrow(new RuntimeException("Token validation error"));

        ResponseWrapper result = authController.validateToken(emailVerificationRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(500, result.getStatus());
        verify(authService, times(1)).verifyEmail(emailVerificationRequest);
    }

    @Test
    void testLogout_Success() {
        doNothing().when(authService).logout();

        ResponseWrapper result = authController.logout();

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatus());
        assertEquals("Logout successful", result.getMessage());
        verify(authService, times(1)).logout();
    }

    @Test
    void testLogout_Exception() {
        doThrow(new RuntimeException("Logout error")).when(authService).logout();

        ResponseWrapper result = authController.logout();

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(500, result.getStatus());
        verify(authService, times(1)).logout();
    }
}

