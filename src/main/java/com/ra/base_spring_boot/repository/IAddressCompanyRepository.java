package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.AddressCompany;
import com.ra.base_spring_boot.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IAddressCompanyRepository extends JpaRepository<AddressCompany, String> {
    List<AddressCompany> findByCompany(Company company);
}
