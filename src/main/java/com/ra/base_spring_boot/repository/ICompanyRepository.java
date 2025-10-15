package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;
public interface ICompanyRepository extends JpaRepository<Company, String> {
     List<Company> findAllByOrderByFollowerDesc(Pageable pageable);
}
