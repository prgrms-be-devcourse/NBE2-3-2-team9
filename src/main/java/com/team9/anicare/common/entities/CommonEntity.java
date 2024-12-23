package com.team9.anicare.common.entities;


import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) // Auditing 활성화
public abstract class CommonEntity {

    @CreatedDate
    @Column(updatable = false, nullable = false) // 생성 시에만 값 설정
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false) // 수정 시에 값 변경
    private LocalDateTime updatedAt;

}