package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.model.TypeJobRelation;
import com.ra.base_spring_boot.services.TypeJobRelationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/type-job-relation")
public class TypeJobRelationController {

    private final TypeJobRelationService service;

    public TypeJobRelationController(TypeJobRelationService service) {
        this.service = service;
    }

    // ✅ Lấy tất cả các liên kết Job - TypeJob
    @GetMapping
    public List<TypeJobRelation> getAll() {
        return service.getAll();
    }

    // ✅ Lấy liên kết theo id
    @GetMapping("/{id}")
    public Object getById(@PathVariable String id) {
        TypeJobRelation relation = service.getById(id);
        if (relation == null) {
            return ResponseEntity.status(404)
                    .body("No TypeJobRelation found with id: " + id);
        }
        return relation;
    }

    // ✅ Tạo mới liên kết giữa Job và TypeJob
    @PostMapping
    public Object create(@RequestBody TypeJobRelation relation) {
        TypeJobRelation existed = service.getById(relation.getId());
        if (existed != null) {
            return ResponseEntity.status(409)
                    .body("TypeJobRelation already exists with id: " + relation.getId());
        }
        return service.save(relation);
    }

    // ✅ Cập nhật liên kết
    @PutMapping("/{id}")
    public Object update(@PathVariable String id, @RequestBody TypeJobRelation relation) {
        relation.setId(id);
        return service.save(relation);
    }

    // ✅ Xóa liên kết
    @DeleteMapping("/{id}")
    public Object delete(@PathVariable String id) {
        TypeJobRelation existed = service.getById(id);
        if (existed == null) {
            return ResponseEntity.status(404)
                    .body("No TypeJobRelation exists to delete with id: " + id);
        }
        service.delete(id);
        return ResponseEntity.ok("Successfully deleted TypeJobRelation with id: " + id);
    }
}
