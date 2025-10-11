package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.Location;
import com.ra.base_spring_boot.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository repo;

    public LocationService(LocationRepository repo) {
        this.repo = repo;
    }

    public List<Location> getAll() {
        return repo.findAll();
    }

    public Location getById(String id) {
        return repo.findById(id).orElse(null);
    }

    public Location save(Location location) {
        return repo.save(location);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}
