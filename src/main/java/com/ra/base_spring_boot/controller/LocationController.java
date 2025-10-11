package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.model.Location;
import com.ra.base_spring_boot.services.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/location")
@CrossOrigin(origins = "*")
public class LocationController {

    private final LocationService service;

    public LocationController(LocationService service) {
        this.service = service;
    }

    // 🔹 GET ALL
    @GetMapping
    public List<Location> getAll() {
        return service.getAll();
    }

    // 🔹 GET BY ID
    @GetMapping("/{id}")
    public Object getById(@PathVariable String id) {
        Location location = service.getById(id);
        if (location == null) {
            return ResponseEntity.status(404).body("Location not found with id: " + id);
        }
        return ResponseEntity.ok(location);
    }

    // 🔹 CREATE — tự nhập id
    @PostMapping
    public Object create(@RequestBody Location location) {
        // kiểm tra nếu đã tồn tại id
        if (service.getById(location.getId()) != null) {
            return ResponseEntity.status(409).body("Location already exists with id: " + location.getId());
        }

        location.setCreated_at(new Date());
        location.setUpdated_at(new Date());
        return ResponseEntity.status(201).body(service.save(location));
    }

    // 🔹 UPDATE
    @PutMapping("/{id}")
    public Object update(@PathVariable String id, @RequestBody Location form) {
        Location existed = service.getById(id);
        if (existed == null) {
            return ResponseEntity.status(404).body("Location not found with id: " + id);
        }
        existed.setName(form.getName());
        existed.setUpdated_at(new Date());
        return ResponseEntity.ok(service.save(existed));
    }

    // 🔹 DELETE
    @DeleteMapping("/{id}")
    public Object delete(@PathVariable String id) {
        Location existed = service.getById(id);
        if (existed == null) {
            return ResponseEntity.status(404).body("No location to delete with id: " + id);
        }
        service.delete(id);
        return ResponseEntity.ok("Deleted successfully with id: " + id);
    }
}
