package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.resp.SkillsCandidateResponse;
import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.FormSkillCandidate;
import com.ra.base_spring_boot.services.ISkillsCandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/candidate/skills")
@RequiredArgsConstructor
public class SkillsCandidateController {

    private final ISkillsCandidateService skillsService;

    /**
     * @apiNote Lấy danh sách skill của candidate hiện tại
     */
    @GetMapping
    public ResponseEntity<?> getMySkills() {
        List<SkillsCandidateResponse> skills = skillsService.getMySkills();
        return ResponseEntity.ok(
                ResponseWrapper.<List<SkillsCandidateResponse>>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(skills)
                        .build()
        );
    }

    /**
     * @apiNote Tạo skill mới cho candidate hiện tại
     */
    @PostMapping
    public ResponseEntity<?> createSkill(@RequestBody FormSkillCandidate request) {
        SkillsCandidateResponse newSkill = skillsService.createSkill(request);
        return ResponseEntity.created(URI.create("/api/v1/candidate/skills"))
                .body(ResponseWrapper.<SkillsCandidateResponse>builder()
                        .status(HttpStatus.CREATED)
                        .code(201)
                        .data(newSkill)
                        .build()
                );
    }

    /**
     * @apiNote Cập nhật skill theo ID (chỉ được sửa của chính mình)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSkill(
            @PathVariable Long id,
            @RequestBody FormSkillCandidate request) {

        SkillsCandidateResponse updated = skillsService.updateSkill(id, request);
        return ResponseEntity.ok(
                ResponseWrapper.<SkillsCandidateResponse>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(updated)
                        .build()
        );
    }

    /**
     * @apiNote Xóa skill theo ID (chỉ được xóa của chính mình)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSkill(@PathVariable Long id) {
        skillsService.deleteSkill(id);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Skill deleted successfully")
                        .build()
        );
    }
}
