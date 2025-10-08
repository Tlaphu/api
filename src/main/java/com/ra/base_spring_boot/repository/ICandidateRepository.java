package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.model.constants.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICandidateRepository extends JpaRepository<Candidate, String> {

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<Candidate> findByEmail(String email);

    Optional<Candidate> findByEmailAndRoles_RoleName(String email, RoleName roleName);
}
