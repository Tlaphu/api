package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.model.constants.RoleName;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ICandidateRepository extends JpaRepository<Candidate, Long> {

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<Candidate> findByEmail(String email);

    Optional<Candidate> findByEmailAndRoles_RoleName(String email, RoleName roleName);
    Optional<Candidate> findByVerificationToken(String verificationToken);

    Optional<Candidate> findByResetToken(String resetToken);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM Candidate c JOIN c.favoriteJobs j WHERE c.id = :candidateId AND j.id = :jobId")
    boolean isJobFavorite(@Param("candidateId") Long candidateId, @Param("jobId") Long jobId);

    @Query("SELECT c FROM Candidate c LEFT JOIN FETCH c.favoriteJobs WHERE c.email = ?1")
    Optional<Candidate> findByEmailWithFavoriteJobs(String email);

    @Query("SELECT c FROM Candidate c LEFT JOIN FETCH c.favoriteCompanies WHERE c.id = :id")
    Optional<Candidate> findByIdWithFavoriteCompanies(@Param("id") Long id);


    @Query("SELECT c FROM Candidate c LEFT JOIN FETCH c.favoriteCompanies WHERE c.email = :candidateEmail")
    Optional<Candidate> findByEmailWithFavoriteCompanies(@Param("candidateEmail") String candidateEmail);

}