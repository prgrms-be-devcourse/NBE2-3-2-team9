package com.team9.anicare.domain.user.repository;

import com.team9.anicare.domain.user.model.Role;
import com.team9.anicare.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);


    Optional<User> findByEmail(String email);
//    Optional<User> findById(String email);

    List<User> findByRole(Role role);

}
