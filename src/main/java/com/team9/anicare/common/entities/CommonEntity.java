package com.team9.anicare.common.entities;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public abstract class CommonEntity {

    @Column(updatable = false) // 생성 시에만 값 설정
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist // 엔티티 저장 전에 실행
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate // 엔티티 업데이트 전에 실행
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
