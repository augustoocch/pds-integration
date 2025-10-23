package ar.com.uade.pds.final_project.domain.config;


import ar.com.uade.pds.final_project.notifications.repository.NotificationRepository;
import ar.com.uade.pds.final_project.notifications.repository.impl.NotificationRepositoryImpl;
import ar.com.uade.pds.final_project.notifications.service.NotificationService;
import ar.com.uade.pds.final_project.notifications.service.impl.NotificationManager;
import ar.com.uade.pds.final_project.notifications.service.impl.NotificationServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationsConfig {

    @Bean
    public NotificationRepository notificationRepository() {
        return new NotificationRepositoryImpl();
    }

    @Bean
    public NotificationService notificationService(NotificationRepository notificationRepository,
                                           NotificationManager notificationManager) {
        return new NotificationServiceImpl(notificationRepository, notificationManager);
    }
}
