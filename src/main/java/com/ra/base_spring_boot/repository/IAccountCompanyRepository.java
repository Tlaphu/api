package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.AccountCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IAccountCompanyRepository extends JpaRepository<AccountCompany, String> {
    boolean existsByEmail(String email);
    Optional<AccountCompany> findByEmail(String email);
    Optional<AccountCompany> findByEmailAndRoles_RoleName(String email, String roleName);

}

