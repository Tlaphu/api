package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface ICompanyRepository extends JpaRepository<Company, Long> {
     List<Company> findAllByOrderByFollowerDesc(Pageable pageable);
    Optional<Company> findByEmail(String email);
    Optional<Company> findByName(String name);

    @Query("SELECT c FROM Company c JOIN c.accounts a WHERE a.id = :accountId")
    Optional<Company> findByAccountId(@Param("accountId") Long accountId);

}
