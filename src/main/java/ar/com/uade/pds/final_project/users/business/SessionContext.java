package ar.com.uade.pds.final_project.users.business;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class SessionContext {

    private static final SessionContext INSTANCE = new SessionContext();

    private String token;
    private String email;
    private String username;
    private SessionContext() {}

    public static SessionContext getInstance() {
        return INSTANCE;
    }

    public boolean isAuthenticated() {
        log.info("Verificando si la sesión está autenticada: {}, {}", username, email);
        return token != null;
    }

    public void setSession(String email, String username, String token) {
        log.info("Sesión iniciada para el usuario: {}", username);
        this.email = email;
        this.username = username;
        this.token = token;
    }

    public void clearSession() {
        log.info("Sesión limpiada");
        this.token = null;
        this.email = null;
        this.username = null;
    }
}