package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.Location;
import com.ra.base_spring_boot.repository.ILocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final ILocationRepository repo;

    // Sửa xung đột và chuẩn hóa cú pháp
    public LocationService(ILocationRepository repo) {
        this.repo = repo;
    }

    public List<Location> getAll() {
        return repo.findAll();
    }

    // Thống nhất dùng kiểu đối tượng Long cho ID
    public Location getById(Long id) { 
        return repo.findById(id).orElse(null);
    }

    public Location save(Location location) {
        return repo.save(location);
    }

    // Thống nhất dùng kiểu đối tượng Long cho ID
    public void delete(Long id) { 
        repo.deleteById(id);
    }
}