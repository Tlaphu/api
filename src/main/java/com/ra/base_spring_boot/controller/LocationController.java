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

    // --- 1. GET: Lấy tất cả Location ---
    @GetMapping
    public ResponseEntity<List<Location>> getAll() {
        // Thay đổi kiểu trả về tường minh thành ResponseEntity
        return ResponseEntity.ok(service.getAll());
    }

    // --- 2. GET: Lấy Location theo ID ---
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) { // Thống nhất dùng Long
        Location location = service.getById(id);
        if (location == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Location not found with id: " + id);
        }
        return ResponseEntity.ok(location);
    }

    // --- 3. POST: Tạo mới Location ---
    @PostMapping
    public ResponseEntity<Location> create(@RequestBody Location location) {
        
        Date now = new Date();
        location.setCreated_at(now);
        location.setUpdated_at(now);
        
        Location savedLocation = service.save(location);
        // Trả về 201 CREATED
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLocation);
    }

    // --- 4. PUT: Cập nhật Location ---
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Location form) { // Thống nhất dùng Long
        Location existed = service.getById(id);
        if (existed == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Location not found with id: " + id);
        }
        
        // Cập nhật các trường
        existed.setName(form.getName());
        existed.setUpdated_at(new Date());
        
        return ResponseEntity.ok(service.save(existed));
    }

    // --- 5. DELETE: Xóa Location ---
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) { // Thống nhất dùng Long
        Location existed = service.getById(id);
        if (existed == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No location to delete with id: " + id);
        }
        service.delete(id);
        // Sử dụng NO_CONTENT (204) là chuẩn cho DELETE thành công không cần trả về nội dung
        return ResponseEntity.noContent().build(); 
        // Hoặc: return ResponseEntity.ok("Deleted successfully with id: " + id);
    }
}