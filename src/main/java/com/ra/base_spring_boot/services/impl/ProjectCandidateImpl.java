package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.FromExperienceCandidate;
import com.ra.base_spring_boot.dto.resp.ExperienceCandidateResponse;
import com.ra.base_spring_boot.model.ExperienceCandidate;
import com.ra.base_spring_boot.model.ProjectCandidate;
import com.ra.base_spring_boot.services.IProjectCandidateService;
import com.ra.base_spring_boot.repository.IProjectRepository;
import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.dto.req.FormProjectCandidate;
import com.ra.base_spring_boot.dto.resp.ProjectCandidateResponse;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.exception.HttpAccessDenied;
import org.springframework.stereotype.Service;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectCandidateImpl implements IProjectCandidateService {
    private final IProjectRepository iProjectRepository;
    private final JwtProvider jwtProvider;

    @Override
    public List<ProjectCandidateResponse> getProject(){
        Candidate current = jwtProvider.getCurrentCandidate();
        return iProjectRepository.findAllByCandidate_Id(current.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectCandidateResponse createProject(FormProjectCandidate cur){
        Candidate current = jwtProvider.getCurrentCandidate();

        ProjectCandidate exp = ProjectCandidate.builder()
                .candidate(current)
                .name(cur.getName())
                .link(cur.getLink())
                .started_at(cur.getStarted_at())
                .end_at(cur.getEnd_at())
                .info(cur.getInfo())
                .created_at(new Date())
                .updated_at(new Date())
                .build();
        return toResponse(iProjectRepository.save(exp));
    }

    @Override
    public ProjectCandidateResponse updateProject(Long id, FormProjectCandidate req) {
        Candidate current = jwtProvider.getCurrentCandidate();

        ProjectCandidate exp = iProjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!exp.getCandidate().getId().equals(current.getId())) {
            throw new HttpAccessDenied("Access denied: You can only update your own Project");
        }

        exp.setName(req.getName());
        exp.setLink(req.getLink());
        exp.setStarted_at(req.getStarted_at());
        exp.setEnd_at(req.getEnd_at());
        exp.setInfo(req.getInfo());
        exp.setUpdated_at(new Date());

        return toResponse(iProjectRepository.save(exp));
    }

    @Override
    public void deleteProject(Long id) {
        Candidate current = jwtProvider.getCurrentCandidate();

        ProjectCandidate exp = iProjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!exp.getCandidate().getId().equals(current.getId())) {
            throw new HttpAccessDenied("Access denied: You can only delete your own Project");
        }

        iProjectRepository.delete(exp);
    }

    private ProjectCandidateResponse toResponse (ProjectCandidate cur){
        return ProjectCandidateResponse.builder()
                .id(cur.getId())
                .name(cur.getName())
                .link(cur.getLink())
                .started_at(cur.getStarted_at())
                .end_at(cur.getEnd_at())
                .info(cur.getInfo())
                .created_at(cur.getCreated_at())
                .updated_at(cur.getUpdated_at())
                .build();
    }
}
