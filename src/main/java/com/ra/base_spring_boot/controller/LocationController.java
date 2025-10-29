package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.model.Location;
import com.ra.base_spring_boot.services.LocationService;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public ResponseEntity<List<Location>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Location location = service.getById(id);
        if (location == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Location not found with id: " + id);
        }
        return ResponseEntity.ok(location);
    }

    @PostMapping
    public ResponseEntity<Location> create(@RequestBody Location location) {

        Date now = new Date();
        location.setCreated_at(now);
        location.setUpdated_at(now);

        Location savedLocation = service.save(location);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedLocation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Location form) {
        Location existed = service.getById(id);
        if (existed == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Location not found with id: " + id);
        }

        existed.setName(form.getName());
        existed.setUpdated_at(new Date());

        return ResponseEntity.ok(service.save(existed));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Location existed = service.getById(id);
        if (existed == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No location to delete with id: " + id);
        }
        service.delete(id);

        return ResponseEntity.noContent().build();

    }
}
