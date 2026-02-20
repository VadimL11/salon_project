package org.example.salon_project.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private final String code;
    private final String message;
    private final List<FieldError> details;

    @Builder.Default
    private final Instant timestamp = Instant.now();

    @Getter
    @Builder
    public static class FieldError {
        private final String field;
        private final String issue;
    }
}
