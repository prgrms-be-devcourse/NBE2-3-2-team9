package com.team9.anicare.domain.hospital.repository;

import com.team9.anicare.domain.hospital.model.HospitalLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface HospitalLikeRepository extends JpaRepository<HospitalLike, Long> {

    Optional<HospitalLike> findByMgtNoAndUserId(String mgtNo, String userId);

}
