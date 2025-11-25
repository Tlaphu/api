package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.dto.resp.AccountCompanyResponse;
import com.ra.base_spring_boot.model.AccountCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import com.ra.base_spring_boot.model.Company;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IAccountCompanyRepository extends JpaRepository<AccountCompany, Long> {
    boolean existsByEmail(String email);
    Optional<AccountCompany> findByEmail(String email);
    List<AccountCompany> findAllByCompany_Id(Long companyId);
    Optional<AccountCompany> findByEmailAndRoles_RoleName(String email, String roleName);
    Optional<AccountCompany> findByVerificationToken(String verificationToken);
   Optional<AccountCompany> findByResetToken(String resetToken);
    List<AccountCompany> findByIsPremiumTrueAndPremiumUntilBefore(Date date);


    // (PHƯƠNG THỨC CẦN THÊM để tìm 3 tài khoản phụ)
    List<AccountCompany> findByCompanyAndEmailStartingWith(Company company, String emailPrefix);
    List<AccountCompany> findByCompanyAndEmailLike(Company company, String emailPattern);

}

