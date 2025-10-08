package ar.com.uade.pds.final_project.domain.config;

import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;


@Configuration
public class ProjectConfig {

    @Value("${app.security.secret-key}")
    private String secretKeyValue;

    @Bean
    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(secretKeyValue.getBytes(StandardCharsets.UTF_8));
    }
}
