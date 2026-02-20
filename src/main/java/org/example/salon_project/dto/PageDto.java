package org.example.salon_project.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PageDto<T> {
    private final List<T> items;
    private final int limit;
    private final int offset;
    private final long total;
}
