package com.team9.anicare.common.dto;

import lombok.Getter;

@Getter
public class PageMetaDTO {
    private final int page;
    private final int size;
    private final long totalItems;
    private final int totalPages;
    private final boolean hasNext;
    private final boolean hasPrevious;

    public PageMetaDTO(int page, int size, long totalItems) {
        this.page = page;
        this.size = size;
        this.totalItems = totalItems;
        this.totalPages = (int) Math.ceil((double) totalItems / size);
        this.hasNext = page < totalPages;
        this.hasPrevious = page > 1;
    }
}
