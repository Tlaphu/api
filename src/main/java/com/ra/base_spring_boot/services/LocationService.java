package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.Location;
import com.ra.base_spring_boot.repository.ILocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final ILocationRepository repo;

    public LocationService(ILocationRepository repo) {
        this.repo = repo;
    }

    public List<Location> getAll() {
        return repo.findAll();
    }

    public Location getById(long id) {
        return repo.findById(id).orElse(null);
    }

    public Location save(Location location) {
        return repo.save(location);
    }

    public void delete(long id) {
        repo.deleteById(id);
    }
}
