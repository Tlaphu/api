package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.FromExperienceCandidate;
import com.ra.base_spring_boot.dto.resp.ExperienceCandidateResponse;
import com.ra.base_spring_boot.services.IExperienceCandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/candidate/experiences")
@RequiredArgsConstructor
public class ExperienceCandidateController {

    private final IExperienceCandidateService experienceService;

    /**
     * @apiNote
     */
    @GetMapping
    public ResponseEntity<?> getMyExperiences() {
        List<ExperienceCandidateResponse> experiences = experienceService.getMyExperiences();
        return ResponseEntity.ok(
                ResponseWrapper.<List<ExperienceCandidateResponse>>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(experiences)
                        .build()
        );
    }

    /**
     * @apiNote
     */
    @PostMapping
    public ResponseEntity<?> createExperience(@RequestBody FromExperienceCandidate request) {
        ExperienceCandidateResponse newExp = experienceService.createExperience(request);
        return ResponseEntity.created(URI.create("/api/v1/candidate/experiences"))
                .body(ResponseWrapper.<ExperienceCandidateResponse>builder()
                        .status(HttpStatus.CREATED)
                        .code(201)
                        .data(newExp)
                        .build()
                );
    }

    /**
     * @apiNote
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateExperience(
            @PathVariable Long id,
            @RequestBody FromExperienceCandidate request) {

        ExperienceCandidateResponse updated = experienceService.updateExperience(id, request);
        return ResponseEntity.ok(
                ResponseWrapper.<ExperienceCandidateResponse>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(updated)
                        .build()
        );
    }

    /**
     * @apiNote
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExperience(@PathVariable Long id) {
        experienceService.deleteExperience(id);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Experience deleted successfully")
                        .build()
        );
    }
}
