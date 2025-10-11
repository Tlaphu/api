package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, String> {
}
