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

    @GetMapping
    public List<Location> getAll() {
        return service.getAll();
    }


    @GetMapping("/{id}")
<<<<<<< HEAD
    public Object getById(@PathVariable long id) {
=======
    public Object getById(@PathVariable Long id) {
>>>>>>> 215340914a830849723a589eea450b87f01dc786
        Location location = service.getById(id);
        if (location == null) {
            return ResponseEntity.status(404).body("Location not found with id: " + id);
        }
        return ResponseEntity.ok(location);
    }

  
    @PostMapping
    public Object create(@RequestBody Location location) {
        
        
     
        Date now = new Date();
        location.setCreated_at(now);
        location.setUpdated_at(now);
        
        return ResponseEntity.status(201).body(service.save(location));
    }

    @PutMapping("/{id}")
<<<<<<< HEAD
    public Object update(@PathVariable long id, @RequestBody Location form) {
=======
    public Object update(@PathVariable Long id, @RequestBody Location form) {
>>>>>>> 215340914a830849723a589eea450b87f01dc786
        Location existed = service.getById(id);
        if (existed == null) {
            return ResponseEntity.status(404).body("Location not found with id: " + id);
        }
        existed.setName(form.getName());
        existed.setUpdated_at(new Date());
        return ResponseEntity.ok(service.save(existed));
    }

   
    @DeleteMapping("/{id}")
<<<<<<< HEAD
    public Object delete(@PathVariable long id) {
=======
    public Object delete(@PathVariable Long id) {
>>>>>>> 215340914a830849723a589eea450b87f01dc786
        Location existed = service.getById(id);
        if (existed == null) {
            return ResponseEntity.status(404).body("No location to delete with id: " + id);
        }
        service.delete(id);
        return ResponseEntity.ok("Deleted successfully with id: " + id);
    }
}
