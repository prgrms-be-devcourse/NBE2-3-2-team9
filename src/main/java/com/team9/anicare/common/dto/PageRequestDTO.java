package com.team9.anicare.common.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Setter
@Getter
public class PageRequestDTO {
    private int page = 1; // 기본 페이지 번호
    private int size = 5; // 한 페이지 당 크기
    private String sortBy = "id";
    private Sort.Direction direction = Sort.Direction.ASC;

    public PageRequest toPageRequest() {
        return PageRequest.of(
                page -1,
                size,
                Sort.by(direction, sortBy)
        );
    }
}
