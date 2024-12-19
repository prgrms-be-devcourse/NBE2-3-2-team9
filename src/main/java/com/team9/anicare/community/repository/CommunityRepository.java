package com.team9.anicare.community.repository;

import com.team9.anicare.community.model.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    @Query("SELECT c FROM Community c WHERE " +
            "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Community> searchByKeyWord(
            String keyword, Pageable pageable);

    @Query("SELECT c FROM Community c WHERE c.animalSpecies = :category")
    Page<Community> searchByCategory(String category, Pageable pageable);

    @Query("SELECT c FROM Community c WHERE " +
            "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND c.animalSpecies = :category")
    Page<Community> searchByKeyWordAndCategory(
            String keyword, String category, Pageable pageable);

    List<Community> findByUserId(Long userId);
}
