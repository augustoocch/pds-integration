package ar.com.uade.pds.final_project.domain.config;


import ar.com.uade.pds.final_project.security.ISecurityValidator;
import ar.com.uade.pds.final_project.security.SecurityValidator;
import ar.com.uade.pds.final_project.users.Business.SessionContext;
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
    public SessionContext sessionContext() {
        return new SessionContext();
    }

    @Bean
    public AuthService authService(IUserRepository iUserRepository,
                                   ISecurityValidator securityValidator,
                                   SessionContext sessionContext
                                   ) {
        return new AuthServiceImpl(iUserRepository, securityValidator, sessionContext);
    }

    @Bean
    public DataService dataService(IUserRepository iUserRepository,
                                   ISecurityValidator securityValidator,
                                   SessionContext sessionContext
                                   ) {
        return new DataServiceImpl(iUserRepository, sessionContext, securityValidator);
    }
}
