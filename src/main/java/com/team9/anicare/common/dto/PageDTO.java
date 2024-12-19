package com.team9.anicare.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageDTO<T> {
    private final List<T> data;
    private final PageMetaDTO meta;
}
