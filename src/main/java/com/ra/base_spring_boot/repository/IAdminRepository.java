package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IAdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
}
