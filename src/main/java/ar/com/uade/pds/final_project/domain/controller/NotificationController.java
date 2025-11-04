package ar.com.uade.pds.final_project.domain.controller;

import ar.com.uade.pds.final_project.domain.dto.response.ResponseWrapper;
import ar.com.uade.pds.final_project.notifications.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import static ar.com.uade.pds.final_project.domain.controller.handler.ResponseHandler.buildResponse;
import static ar.com.uade.pds.final_project.domain.controller.handler.ResponseHandler.handleError;

@Controller
@AllArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    public ResponseWrapper unsubscribeNotifications() {
        try {
            notificationService.unsubscribe();
            return buildResponse("Joining success", HttpStatus.OK, true, null);
        } catch (Exception e) {
            return handleError(e);
        }
    }
}
