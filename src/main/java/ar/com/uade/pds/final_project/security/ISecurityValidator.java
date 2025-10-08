package ar.com.uade.pds.final_project.security;

import ar.com.uade.pds.final_project.users.entity.User;

public interface ISecurityValidator {
    String hashPassword(String password);
    boolean matchesPassword(String rawPassword, String hashedPassword);
    User getUserFromToken(String token);
    String generateToken(User user);
}
