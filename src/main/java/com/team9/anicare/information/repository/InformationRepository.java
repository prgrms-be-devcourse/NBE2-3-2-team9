package com.team9.anicare.information.repository;

import com.team9.anicare.information.model.Information;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InformationRepository extends JpaRepository<Information, Long> {

    @Query("SELECT i FROM Information i")
    Page<Information> findAllInformation(Pageable pageable);

    @Query("SELECT i FROM Information i WHERE i.breed.species.name = :speciesName")
    Page<Information> findBySpeciesName(String speciesName, Pageable pageable);

    @Query("SELECT i FROM Information i JOIN i.breed b JOIN b.species s WHERE s.name = :speciesName AND b.name = :breedName")
    Page<Information> findBySpeciesAndBreed(String speciesName, String breedName, Pageable pageable);

    @Query("SELECT i FROM Information i JOIN i.breed b JOIN b.species s WHERE s.name = :speciesName AND b.name = :breedName")
    Information findBySpeciesAndBreed(String speciesName, String breedName);

}