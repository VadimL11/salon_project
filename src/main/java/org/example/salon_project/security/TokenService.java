package org.example.salon_project.security;


public interface TokenService {


    boolean isValidToken(String token);


    String getId(String token);


    RoleType getType(String token);


    String generateToken(String id, RoleType type);
}
