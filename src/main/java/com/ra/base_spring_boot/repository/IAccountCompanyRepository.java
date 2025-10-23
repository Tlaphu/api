package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.AccountCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IAccountCompanyRepository extends JpaRepository<AccountCompany, Long> {
    boolean existsByEmail(String email);
    Optional<AccountCompany> findByEmail(String email);
    List<AccountCompany> findAllByCompany_Id(Long companyId);
    Optional<AccountCompany> findByEmailAndRoles_RoleName(String email, String roleName);
    Optional<AccountCompany> findByVerificationToken(String verificationToken);
   Optional<AccountCompany> findByResetToken(String resetToken);
}

