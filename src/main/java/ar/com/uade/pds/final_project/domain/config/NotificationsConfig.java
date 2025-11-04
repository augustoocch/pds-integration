package ar.com.uade.pds.final_project.domain.config;


import ar.com.uade.pds.final_project.notifications.repository.NotificationRepository;
import ar.com.uade.pds.final_project.notifications.service.NotificationFactory;
import ar.com.uade.pds.final_project.notifications.service.NotificationService;
import ar.com.uade.pds.final_project.notifications.service.impl.NotificationManager;
import ar.com.uade.pds.final_project.notifications.service.impl.NotificationServiceImpl;
import ar.com.uade.pds.final_project.users.service.DataService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationsConfig {

    @Bean
    public NotificationFactory notificationFactory() {
        return new NotificationFactory();
    }

    @Bean
    public NotificationManager notificationManager(
            NotificationRepository notificationRepository,
            NotificationFactory notificationFactory) {
        return new NotificationManager(notificationRepository, notificationFactory);
    }


    @Bean
    public NotificationService notificationService(
            DataService dataService,
            NotificationRepository notificationRepository,
            NotificationManager notificationManager) {
        return new NotificationServiceImpl(dataService,
                notificationRepository, notificationManager);
    }
}
