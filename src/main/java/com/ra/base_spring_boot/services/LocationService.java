package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.Location;
import com.ra.base_spring_boot.repository.ILocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final ILocationRepository repo;

<<<<<<< HEAD
    public LocationService(ILocationRepository repo) {
=======
    public LocationService(ILocationRepository   repo) {
>>>>>>> 215340914a830849723a589eea450b87f01dc786
        this.repo = repo;
    }

    public List<Location> getAll() {
        return repo.findAll();
    }

<<<<<<< HEAD
    public Location getById(long id) {
=======
    public Location getById(Long id) {
>>>>>>> 215340914a830849723a589eea450b87f01dc786
        return repo.findById(id).orElse(null);
    }

    public Location save(Location location) {
        return repo.save(location);
    }

<<<<<<< HEAD
    public void delete(long id) {
=======
    public void delete(Long id) {
>>>>>>> 215340914a830849723a589eea450b87f01dc786
        repo.deleteById(id);
    }
}
