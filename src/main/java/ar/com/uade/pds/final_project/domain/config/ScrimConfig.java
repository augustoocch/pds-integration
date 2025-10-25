package ar.com.uade.pds.final_project.domain.config;

import ar.com.uade.pds.final_project.scrim.repository.IScrimRepository;
import ar.com.uade.pds.final_project.scrim.service.MatchMakingService;
import ar.com.uade.pds.final_project.scrim.service.ScrimService;
import ar.com.uade.pds.final_project.scrim.service.impl.MatchMakingServiceImpl;
import ar.com.uade.pds.final_project.scrim.service.impl.ScrimServiceImpl;
import ar.com.uade.pds.final_project.scrim.strategy.MatchMakingStrategyFactory;
import ar.com.uade.pds.final_project.users.service.DataService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScrimConfig {

    @Bean
    public ScrimService scrimService(IScrimRepository iScrimRepository,
                                      DataService dataService) {
        return new ScrimServiceImpl(iScrimRepository, dataService);
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
}
