package com.abid.ml.movieapi.repository;

import com.abid.ml.movieapi.models.ERole;
import com.abid.ml.movieapi.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}