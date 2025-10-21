package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.FormProjectCandidate;
import com.ra.base_spring_boot.dto.resp.ProjectCandidateResponse;
import com.ra.base_spring_boot.services.IProjectCandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/candidate/project")
@RequiredArgsConstructor
public class ProjectCandidateController {
    private final IProjectCandidateService iProjectCandidateService;

    /**
     * @apiNote
     */
    @GetMapping
    public ResponseEntity<?> getProject() {
        List<ProjectCandidateResponse> Project = iProjectCandidateService.getProject();
        return ResponseEntity.ok(
                ResponseWrapper.<List<ProjectCandidateResponse>>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(Project)
                        .build()
        );
    }

    /**
     * @apiNote
     */
    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody FormProjectCandidate request) {
        ProjectCandidateResponse newExp = iProjectCandidateService.createProject(request);
        return ResponseEntity.created(URI.create("/api/v1/candidate/Project"))
                .body(ResponseWrapper.<ProjectCandidateResponse>builder()
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
    public ResponseEntity<?> updateProject(
            @PathVariable Long id,
            @RequestBody FormProjectCandidate request) {

        ProjectCandidateResponse updated = iProjectCandidateService.updateProject(id, request);
        return ResponseEntity.ok(
                ResponseWrapper.<ProjectCandidateResponse>builder()
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
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        iProjectCandidateService.deleteProject(id);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Project deleted successfully")
                        .build()
        );
    }
}
