package ar.com.uade.pds.final_project.domain.controller;


import ar.com.uade.pds.final_project.domain.dto.request.NotificationRequest;
import ar.com.uade.pds.final_project.notifications.event.DomainEvent;
import ar.com.uade.pds.final_project.notifications.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody NotificationRequest request) {
        DomainEvent event = new DomainEvent(
                request.getEventType(),
                request.getNotificationType(),
                request.getUserId(),
                request.getScrimId()
        );
        notificationService.subscribe(event);
        return ResponseEntity.ok("Usuario suscripto correctamente");
    }

    @PostMapping("/notify")
    public ResponseEntity<?> notifyEvent(@RequestBody NotificationRequest request) {
        DomainEvent event = new DomainEvent(
                request.getEventType(),
                request.getNotificationType(),
                request.getUserId(),
                request.getScrimId()
        );
        notificationService.notify(event);
        return ResponseEntity.ok("Notificaciones enviadas");
    }
}
