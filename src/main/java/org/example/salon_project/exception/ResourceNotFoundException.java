package org.example.salon_project.exception;

/**
 * Thrown when a requested resource does not exist in the database.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String entity, Long id) {
        super(entity + " not found with id: " + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
