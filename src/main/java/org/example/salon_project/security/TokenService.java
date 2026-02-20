package org.example.salon_project.security;

public interface TokenService {
    boolean isValidToken(String token);

    /**
     * Subject / user id from token (наприклад clientId/adminId).
     */
    String getId(String token);

    /**
     * Upper-bound role from token.
     */
    RoleType getType(String token);
}
