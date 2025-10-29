package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.FormEducationCandidate;
import com.ra.base_spring_boot.dto.resp.EducationCandidateResponse;
import com.ra.base_spring_boot.services.IEducationCandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/candidate/education")
@RequiredArgsConstructor
public class EducationCandidateController {

    private final IEducationCandidateService educationCandidateService;

    /**
     *
     */
    @GetMapping
    public ResponseEntity<?> getEducationList() {
        List<EducationCandidateResponse> list = educationCandidateService.getAllByCandidate(null);

        return ResponseEntity.ok(
                ResponseWrapper.<List<EducationCandidateResponse>>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(list)
                        .build()
        );
    }

    /**
     *
     */
    @PostMapping
    public ResponseEntity<?> createEducation(@RequestBody FormEducationCandidate request) {
        EducationCandidateResponse newEdu = educationCandidateService.createByCandidate(null, request);

        return ResponseEntity.created(URI.create("/api/v1/candidate/education"))
                .body(ResponseWrapper.<EducationCandidateResponse>builder()
                        .status(HttpStatus.CREATED)
                        .code(201)
                        .data(newEdu)
                        .build()
                );
    }

    /**
     *
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEducation(
            @PathVariable Long id,
            @RequestBody FormEducationCandidate request) {

        EducationCandidateResponse updated = educationCandidateService.updateByCandidate(id, null, request);

        return ResponseEntity.ok(
                ResponseWrapper.<EducationCandidateResponse>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(updated)
                        .build()
        );
    }

    /**
     *
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEducation(@PathVariable Long id) {
        educationCandidateService.deleteByCandidate(id, null);

        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Education deleted successfully")
                        .build()
        );
    }
}
