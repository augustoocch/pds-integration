package ar.com.uade.pds.final_project.users.Business;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Getter
@Component
@Slf4j
public class SessionContext {

    private String token;
    private String email;
    private String username;

    public boolean isAuthenticated() {
        return token != null;
    }

    public void setSession(String email, String username, String token) {
        log.info("Sesion iniciada para el usuario: {}", username);
        this.email = email;
        this.username = username;
        this.token = token;
    }

    public void clearSession() {
        this.token = null;
        this.email = null;
        this.username = null;
    }
}