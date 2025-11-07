package ar.com.uade.pds.final_project.domain.config;

import ar.com.uade.pds.final_project.notifications.service.NotificationService;
import ar.com.uade.pds.final_project.scrim.business.command.ScrimCommandInvoker;
import ar.com.uade.pds.final_project.scrim.repository.IScrimRepository;
import ar.com.uade.pds.final_project.scrim.service.MatchMakingService;
import ar.com.uade.pds.final_project.scrim.service.ScrimService;
import ar.com.uade.pds.final_project.scrim.service.TeamManagementService;
import ar.com.uade.pds.final_project.scrim.service.impl.MatchMakingServiceImpl;
import ar.com.uade.pds.final_project.scrim.service.impl.ScrimServiceImpl;
import ar.com.uade.pds.final_project.scrim.service.impl.TeamManagementServiceImpl;
import ar.com.uade.pds.final_project.scrim.strategy.MatchMakingStrategyFactory;
import ar.com.uade.pds.final_project.users.repository.IUserRepository;
import ar.com.uade.pds.final_project.users.service.DataService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScrimConfig {

    @Bean
    public ScrimService scrimService(
            IScrimRepository iScrimRepository,
                                     DataService dataService,
                                     NotificationService notificationService,
                                     IUserRepository iUserRepository,
            TeamManagementService teamManagementService
                                     ) {
        return new ScrimServiceImpl(iScrimRepository, dataService,
                notificationService, iUserRepository, teamManagementService);
    }

    @Bean
    public MatchMakingStrategyFactory matchMakingStrategyFactory(ScrimService scrimService,
                                                             DataService dataService) {
        return new MatchMakingStrategyFactory(scrimService, dataService);
    }

    @Bean
    public MatchMakingService matchMakingService (MatchMakingStrategyFactory strategyFactory) {
        return new MatchMakingServiceImpl(strategyFactory);
    }

    @Bean
    public ScrimCommandInvoker scrimCommandInvoker() {
        return new ScrimCommandInvoker();
    }

    @Bean
    public TeamManagementService teamManagementService(IScrimRepository iScrimRepository,
                                                       DataService dataService,
                                                       ScrimCommandInvoker scrimCommandInvoker) {
        return new TeamManagementServiceImpl(iScrimRepository, dataService, scrimCommandInvoker);
    }
}
