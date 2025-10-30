package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.FormProjectCandidate;
import com.ra.base_spring_boot.dto.resp.ProjectCandidateResponse;
import com.ra.base_spring_boot.exception.HttpAccessDenied;
import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.model.ProjectCandidate;
import com.ra.base_spring_boot.repository.IProjectRepository;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.services.IProjectCandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectCandidateImpl implements IProjectCandidateService {

    private final IProjectRepository projectRepo;
    private final JwtProvider jwtProvider;

    @Override
    public List<ProjectCandidateResponse> getProject() {
        Candidate current = jwtProvider.getCurrentCandidate();

        return projectRepo.findAllByCandidate_Id(current.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectCandidateResponse createProject(FormProjectCandidate req) {
        Candidate current = jwtProvider.getCurrentCandidate();

        ProjectCandidate exp = ProjectCandidate.builder()
                .candidate(current)
                .candidateCV(null)
                .name(req.getName())
                .link(req.getLink())
                .started_at(req.getStarted_at())
                .end_at(req.getEnd_at())
                .info(req.getInfo())
                .created_at(new Date())
                .updated_at(new Date())
                .build();

        return toResponse(projectRepo.save(exp));
    }

    @Override
    public ProjectCandidateResponse updateProject(Long id, FormProjectCandidate req) {
        Candidate current = jwtProvider.getCurrentCandidate();

        ProjectCandidate exp = projectRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // FIX: Kiểm tra quyền sở hữu bằng getCandidate()
        if (exp.getCandidate() == null || !exp.getCandidate().getId().equals(current.getId())) {
            throw new HttpAccessDenied("Access denied: You can only update your own project");
        }

        exp.setName(req.getName());
        exp.setLink(req.getLink());
        exp.setStarted_at(req.getStarted_at());
        exp.setEnd_at(req.getEnd_at());
        exp.setInfo(req.getInfo());
        exp.setUpdated_at(new Date());

        return toResponse(projectRepo.save(exp));
    }

    @Override
    public void deleteProject(Long id) {
        Candidate current = jwtProvider.getCurrentCandidate();

        ProjectCandidate exp = projectRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // FIX: Kiểm tra quyền sở hữu bằng getCandidate()
        if (exp.getCandidate() == null || !exp.getCandidate().getId().equals(current.getId())) {
            throw new HttpAccessDenied("Access denied: You can only delete your own project");
        }

        projectRepo.delete(exp);
    }

    private ProjectCandidateResponse toResponse(ProjectCandidate exp) {
        return ProjectCandidateResponse.builder()
                .id(exp.getId())
                .name(exp.getName())
                .link(exp.getLink())
                .started_at(exp.getStarted_at())
                .end_at(exp.getEnd_at())
                .info(exp.getInfo())
                .created_at(exp.getCreated_at())
                .updated_at(exp.getUpdated_at())
                .build();
    }
}
