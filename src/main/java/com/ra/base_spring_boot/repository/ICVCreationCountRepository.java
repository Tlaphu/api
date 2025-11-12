package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.CVCreationCount;
import com.ra.base_spring_boot.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.Optional;

@Repository
public interface ICVCreationCountRepository extends JpaRepository<CVCreationCount, Long> {


    Optional<CVCreationCount> findByCandidateAndDate(Candidate candidate, Date date);
    @Query("SELECT COALESCE(SUM(c.count), 0) FROM CVCreationCount c " +
            "WHERE c.candidate.id = :candidateId AND c.date >= :startDate AND c.date < :endDate")
    Integer countCVCreatedInMonth(@Param("candidateId") Long candidateId,
                                  @Param("startDate") Date startDate,
                                  @Param("endDate") Date endDate);
}