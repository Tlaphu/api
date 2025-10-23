package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ILocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByName(String name);
    List<Location> findAllByOrderByNameAsc();
   
}
