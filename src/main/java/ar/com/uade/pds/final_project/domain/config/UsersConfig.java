package ar.com.uade.pds.final_project.domain.config;


import ar.com.uade.pds.final_project.notifications.service.NotificationService;
import ar.com.uade.pds.final_project.security.ISecurityValidator;
import ar.com.uade.pds.final_project.security.SecurityValidator;
import ar.com.uade.pds.final_project.users.business.SessionContext;
import ar.com.uade.pds.final_project.users.repository.IUserRepository;
import ar.com.uade.pds.final_project.users.service.AuthService;
import ar.com.uade.pds.final_project.users.service.DataService;
import ar.com.uade.pds.final_project.users.service.impl.AuthServiceImpl;
import ar.com.uade.pds.final_project.users.service.impl.DataServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class UsersConfig {

    @Bean
    public ISecurityValidator securityValidator(SecretKey secretKey) {
        return new SecurityValidator(secretKey);
    }

    @Bean
    public AuthService authService(IUserRepository iUserRepository,
                                   ISecurityValidator securityValidator,
                                   NotificationService notificationService
                                   ) {
        return new AuthServiceImpl(iUserRepository, securityValidator,
                notificationService);
    }

    @Bean
    public DataService dataService(IUserRepository iUserRepository,
                                   ISecurityValidator securityValidator
                                   ) {
        return new DataServiceImpl(iUserRepository, securityValidator);
    }
}
