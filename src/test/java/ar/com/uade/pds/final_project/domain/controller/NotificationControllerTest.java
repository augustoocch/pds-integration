package ar.com.uade.pds.final_project.domain.controller;

import ar.com.uade.pds.final_project.domain.dto.response.ResponseWrapper;
import ar.com.uade.pds.final_project.notifications.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @Test
    void testUnsubscribeNotifications_Success() {
        doNothing().when(notificationService).unsubscribe();

        ResponseWrapper result = notificationController.unsubscribeNotifications();

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatus());
        assertEquals("Joining success", result.getMessage());
        verify(notificationService, times(1)).unsubscribe();
    }

    @Test
    void testUnsubscribeNotifications_Exception() {
        doThrow(new RuntimeException("Unsubscribe error")).when(notificationService).unsubscribe();

        ResponseWrapper result = notificationController.unsubscribeNotifications();

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(500, result.getStatus());
        verify(notificationService, times(1)).unsubscribe();
    }
}

